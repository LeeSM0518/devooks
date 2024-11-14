INSERT INTO category (name, registered_date, modified_date)
VALUES ('IT/프로그래밍', NOW(), NOW()),
       ('게임', NOW(), NOW()),
       ('비즈니스', NOW(), NOW()),
       ('하드웨어', NOW(), NOW()),
       ('인공지능', NOW(), NOW()),
       ('디자인', NOW(), NOW()),
       ('금융/재테크', NOW(), NOW()),
       ('교양', NOW(), NOW())
ON CONFLICT (name) DO NOTHING;
