

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
