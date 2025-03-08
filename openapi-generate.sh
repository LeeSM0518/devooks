#!/bin/bash
set -e

PASSWORD=""
VERSION="0.0.0"

echo "VERSION=$VERSION"

while [ "$#" -gt 0 ]; do
  case "$1" in
    -password)
      PASSWORD="$2"
      shift 2
      ;;
    -version)
      VERSION="$2"
      shift 2
      ;;
    *)
      echo "Unknown parameter: $1"
      exit 1
      ;;
  esac
done

# 1. sample.application.yml -> application.yml
echo "Copying sample.application.yml -> application.yml ..."
cp src/main/resources/sample.application.yml src/main/resources/application.yml

if [ $? -ne 0 ]; then
  echo "[ERROR] Failed to copy sample.application.yml"
  exit 1
fi
echo "Successfully copied file."

# 2. Run docker-compose in openapi/docker-compose.yml
echo "Starting database via openapi/docker-compose.yml ..."
docker-compose -f openapi/docker-compose.yml up -d

if [ $? -ne 0 ]; then
  echo "[ERROR] Failed to start docker-compose"
  exit 1
fi
echo "Database containers are starting in the background."

# 3. Execute Gradle task: generateOpenApiDocs
echo "Running './gradlew generateOpenApiDocs' ..."
./gradlew generateOpenApiDocs

if [ $? -ne 0 ]; then
  echo "[ERROR] Gradle task generateOpenApiDocs failed"
  exit 1
fi

rm -rf openapi-generator
mkdir openapi-generator

cat <<EOF > openapi-generator/.openapi-generator-ignore
# OpenAPI Generator Ignore
.gitignore
.npmignore
git_push.sh
EOF

cat <<EOF > openapi-generator/package.json
{
  "name": "@leesm0518/devooks-api",
  "version": "$VERSION",
  "type": "module",
  "repository": {
    "url": "https://github.com/LeeSM0518/devooks"
  },
  "scripts": {
    "build": "rm -rf dist/* && tsc -p tsconfig.json"
  },
  "main": "dist/index.js",
  "module": "dist/index.js",
  "types": "dist/index.d.ts",
  "exports": {
    ".": {
      "import": "./dist/index.js",
      "require": "./dist/index.js"
    }
  },
  "devDependencies": {
    "typescript": "^5.8.2"
  },
  "dependencies": {
    "axios": "^1.8.2"
  }
}
EOF

cat <<EOF > openapi-generator/tsconfig-base.json
{
  "compilerOptions": {
    "target": "ES5",
    "allowJs": true,
    "allowSyntheticDefaultImports": true,
    "baseUrl": "src",
    "declaration": true,
    "esModuleInterop": true,
    "inlineSourceMap": false,
    "listEmittedFiles": false,
    "listFiles": false,
    "moduleResolution": "node",
    "noFallthroughCasesInSwitch": true,
    "pretty": true,
    "resolveJsonModule": true,
    "rootDir": "src",
    "skipLibCheck": true,
    "strict": true,
    "traceResolution": false
  },
  "compileOnSave": false,
  "exclude": ["node_modules", "dist"],
  "include": ["src"]
}
EOF

cat <<EOF > openapi-generator/tsconfig.json
{
  "extends": "./tsconfig-base.json",
  "compilerOptions": {
    "module": "esnext",
    "outDir": "dist",
    "target": "esnext"
  }
}
EOF

cat <<EOF > openapi-generator/.npmrc
@leesm0518:registry=https://npm.pkg.github.com
@LeeSM0518:registry=https://npm.pkg.github.com
//npm.pkg.github.com/:_authToken="$PASSWORD"
EOF

cat <<EOF > openapi-generator/.npmignore
**/*
!/dist/**
EOF

cp "$(ls -t build/openapi.json | head -n 1)" openapi-generator/openapi.json

docker run --rm \
  -v "$(pwd)/openapi-generator":/local openapitools/openapi-generator-cli generate \
  -i /local/openapi.json \
  -g typescript-axios \
  -o /local/src \
  --additional-properties=withSeparateModelsAndApi=true,apiPackage=apis,modelPackage=models,useSingleRequestParameter=true

cd openapi-generator && npm i && npm run build

echo "All steps completed successfully."