CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS member
(
    member_id             uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    nickname              VARCHAR UNIQUE NOT NULL,
    profile_image_path    VARCHAR UNIQUE,
    authority             VARCHAR        NOT NULL,
    withdrawal_date       TIMESTAMP,
    until_suspension_date TIMESTAMP,
    registered_date       TIMESTAMP      NOT NULL,
    modified_date         TIMESTAMP      NOT NULL
);

CREATE TABLE IF NOT EXISTS member_info
(
    member_info_id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    blog_link      VARCHAR NOT NULL,
    instagram_link VARCHAR NOT NULL,
    youtube_link   VARCHAR NOT NULL,
    real_name      VARCHAR NOT NULL,
    bank           VARCHAR NOT NULL,
    account_number VARCHAR NOT NULL,
    introduction   TEXT    NOT NULL,
    phone_number   VARCHAR NOT NULL,
    member_id      uuid    NOT NULL UNIQUE,
    CONSTRAINT fk_member_id FOREIGN KEY (member_id) REFERENCES member_info (member_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS oauth_info
(
    oauth_id        VARCHAR PRIMARY KEY,
    oauth_type      VARCHAR     NOT NULL,
    member_id       uuid UNIQUE NOT NULL,
    registered_date TIMESTAMP   NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_member_id FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS refresh_token
(
    refresh_token_id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    member_id        uuid UNIQUE    NOT NULL,
    token            VARCHAR UNIQUE NOT NULL,
    registered_date  TIMESTAMP      NOT NULL,
    modified_date    TIMESTAMP      NOT NULL,
    CONSTRAINT fk_member_id FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS category
(
    category_id     uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    name            VARCHAR UNIQUE NOT NULL,
    registered_date TIMESTAMP      NOT NULL,
    modified_date   TIMESTAMP      NOT NULL,
    deleted_date    TIMESTAMP
);

CREATE TABLE IF NOT EXISTS favorite_category
(
    favorite_category_id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    favorite_member_id   uuid NOT NULL,
    category_id          uuid NOT NULL,
    CONSTRAINT fk_member_id FOREIGN KEY (favorite_member_id)
        REFERENCES member (member_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_category_id FOREIGN KEY (category_id)
        REFERENCES category (category_id)
);

CREATE TABLE IF NOT EXISTS pdf
(
    pdf_id           uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    file_path        VARCHAR UNIQUE NOT NULL,
    page_count       INT            NOT NULL,
    created_date     TIMESTAMP      NOT NULL,
    upload_member_id uuid           NOT NULL,
    CONSTRAINT fk_member_id FOREIGN KEY (upload_member_id) REFERENCES member (member_id)
);

CREATE TABLE IF NOT EXISTS preview_image
(
    preview_image_id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    image_path       VARCHAR UNIQUE NOT NULL,
    preview_order    INT            NOT NULL,
    pdf_id           uuid           NOT NULL,
    CONSTRAINT fk_pdf_id FOREIGN KEY (pdf_id) REFERENCES pdf (pdf_id)
);

CREATE TABLE IF NOT EXISTS ebook
(
    ebook_id          uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    selling_member_id uuid      NOT NULL,
    pdf_id            uuid      NOT NULL UNIQUE,
    main_image_id     uuid      NOT NULL UNIQUE,
    title             VARCHAR   NOT NULL,
    price             INT       NOT NULL,
    table_of_contents TEXT      NOT NULL,
    introduction      TEXT      NOT NULL,
    created_date      TIMESTAMP NOT NULL,
    modified_date     TIMESTAMP NOT NULL,
    deleted_date      TIMESTAMP,
    CONSTRAINT fk_member_id FOREIGN KEY (selling_member_id) REFERENCES member (member_id),
    CONSTRAINT fk_pdf_id FOREIGN KEY (pdf_id) REFERENCES pdf (pdf_id)
);

CREATE TABLE IF NOT EXISTS ebook_image
(
    ebook_image_id   uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    image_path       VARCHAR UNIQUE NOT NULL,
    image_order      INT            NOT NULL,
    upload_member_id uuid           NOT NULL,
    ebook_id         uuid,
    CONSTRAINT fk_ebook_id FOREIGN KEY (ebook_id) REFERENCES ebook (ebook_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS related_category
(
    related_category_id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    ebook_id            uuid NOT NULL,
    category_id         uuid NOT NULL,
    CONSTRAINT fk_ebook_id FOREIGN KEY (ebook_id) REFERENCES ebook (ebook_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_category_id FOREIGN KEY (category_id) REFERENCES category (category_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS wishlist
(
    wishlist_id  uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    member_id    uuid      NOT NULL,
    ebook_id     uuid      NOT NULL,
    created_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_member_id FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_ebook_id FOREIGN KEY (ebook_id) REFERENCES ebook (ebook_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS transaction
(
    transaction_id   uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    ebook_id         uuid      NOT NULL,
    price            INT       NOT NULL,
    payment_method   VARCHAR   NOT NULL,
    buyer_member_id  uuid      NOT NULL,
    transaction_date TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS review
(
    review_id        uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    rating           INT       NOT NULL,
    content          VARCHAR   NOT NULL,
    ebook_id         uuid      NOT NULL,
    writer_member_id uuid      NOT NULL,
    written_date     TIMESTAMP NOT NULL,
    modified_date    TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS review_comment
(
    review_comment_id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    content           VARCHAR   NOT NULL,
    review_id         uuid      NOT NULL,
    writer_member_id  uuid      NOT NULL,
    written_date      TIMESTAMP NOT NULL,
    modified_date     TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS ebook_inquiry
(
    ebook_inquiry_id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    content          VARCHAR   NOT NULL,
    ebook_id         uuid      NOT NULL,
    writer_member_id uuid      NOT NULL,
    written_date     TIMESTAMP NOT NULL,
    modified_date    TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS ebook_inquiry_comment
(
    ebook_inquiry_comment_id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    content                  VARCHAR   NOT NULL,
    inquiry_id               uuid      NOT NULL,
    writer_member_id         uuid      NOT NULL,
    written_date             TIMESTAMP NOT NULL,
    modified_date            TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS service_inquiry
(
    service_inquiry_id        uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    title                     VARCHAR   NOT NULL,
    content                   TEXT      NOT NULL,
    created_date              TIMESTAMP NOT NULL,
    modified_date             TIMESTAMP NOT NULL,
    inquiry_processing_status VARCHAR   NOT NULL,
    writer_member_id          uuid      NOT NULL,
    CONSTRAINT fk_member_id FOREIGN KEY (writer_member_id) REFERENCES member (member_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS service_inquiry_image
(
    service_inquiry_image_id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    image_path               VARCHAR UNIQUE NOT NULL,
    image_order              INT            NOT NULL,
    upload_member_id         uuid           NOT NULL,
    service_inquiry_id       uuid,
    CONSTRAINT fk_service_inquiry_id FOREIGN KEY (service_inquiry_id) REFERENCES service_inquiry (service_inquiry_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS notification
(
    notification_id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    type            VARCHAR   NOT NULL,
    content         VARCHAR   NOT NULL,
    note            jsonb     NOT NULL,
    receiver_id     uuid      NOT NULL,
    notified_date   TIMESTAMP NOT NULL,
    checked         BOOLEAN   NOT NULL
);
