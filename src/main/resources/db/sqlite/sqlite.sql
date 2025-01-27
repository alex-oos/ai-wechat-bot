-- Create database if not exists (SQLite does not have this syntax directly)
-- For SQLite, there's no separate command to create a database. You can directly connect to a database file.

-- Use the database (SQLite does not require a separate command to switch databases)
-- Assuming you've connected to the 'demodb' database file.

-- Drop table if exists (SQLite)
DROP TABLE IF EXISTS "user";

-- Create table (SQLite syntax)
CREATE TABLE "user"
(
    id          INTEGER PRIMARY KEY NOT NULL,
    username    TEXT     DEFAULT NULL,
    age         INTEGER  DEFAULT NULL,
    email       TEXT     DEFAULT NULL,
    manager_id  INTEGER  DEFAULT NULL,
    create_time DATETIME DEFAULT NULL,
    update_time DATETIME DEFAULT NULL,
    password    TEXT,
    version     INTEGER  DEFAULT '1',
    deleted     INTEGER  DEFAULT '0'
);

-- Insert data into the 'user' table (SQLite syntax)
INSERT INTO "user" (id, username, age, email, manager_id, create_time)
VALUES (1, '老板', 40, 'boss@baomidou.com', NULL, '2021-03-28 13:12:40'),
       (2, '王狗蛋', 40, 'gd@baomidou.com', 1, '2021-03-28 13:12:40'),
       (3, '王鸡蛋', 40, 'jd@baomidou.com', 2, '2021-03-28 13:12:40'),
       (4, '王鸭蛋', 40, 'yd@baomidou.com', 2, '2021-03-28 13:12:40'),
       (5, '王猪蛋', 40, 'zd@baomidou.com', 2, '2021-03-28 13:12:40'),
       (6, '王软蛋', 40, 'rd@baomidou.com', 2, '2021-03-28 13:12:40'),
       (7, '王铁蛋', 40, 'td@baomidou.com', 2, '2021-03-28 13:12:40');


CREATE TABLE "friends"
(
  "id"          INTEGER PRIMARY KEY NOT NULL,
  "userName" TEXT NOT NULL,
  "nickName" TEXT DEFAULT NULL,
  "pyInitial" TEXT DEFAULT NULL,
  "quanPin" TEXT DEFAULT NULL,
  "sex" integer DEFAULT NULL,
  "remark" TEXT ,
  "remarkPyInitial" TEXT ,
  "remarkQuanPin" TEXT,
  "signature" TEXT DEFAULT '1',
  "alias" TEXT DEFAULT '0',
  "snsBgImg" TEXT,
  "country" TEXT,
  "bigHeadImgUrl" TEXT,
  "smallHeadImgUrl" TEXT,
  "description" TEXT,
  "cardImgUrl" TEXT,
  "labelList" TEXT,
  "province" TEXT,
  "city" TEXT,
  "phoneNumList" TEXT,
);
