DROP TABLE IF EXISTS "android_metadata";
CREATE TABLE android_metadata (locale TEXT);
INSERT INTO "android_metadata" VALUES('en_US');
DROP TABLE IF EXISTS "tbl_noti";
CREATE TABLE "tbl_noti" ("id" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , "msg" VARCHAR, "ontime" DATETIME DEFAULT CURRENT_TIMESTAMP);
DROP TABLE IF EXISTS "tbl_order";
CREATE TABLE "tbl_order" ("id" INTEGER NOT NULL , "Menu_name" VARCHAR, "Quantity" INTEGER, "Total_price" NUMERIC);
