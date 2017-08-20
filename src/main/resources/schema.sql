CREATE TABLE ACCOUNT (
   name VARCHAR(512),
   password VARCHAR(512)
);

CREATE TABLE URL (
  url VARCHAR(512),
  shortUrl VARCHAR(512),
  redirectType INTEGER(512),
  hitCount INTEGER,
  accountName VARCHAR(512),
  FOREIGN KEY (accountName) REFERENCES ACCOUNT(name),
  UNIQUE KEY (url,accountName)

);
