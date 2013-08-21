DROP TABLE IF EXISTS "user";
CREATE TABLE user (
    id INTEGER PRIMARY KEY,
    username VARCHAR (128),
    password VARCHAR (128),
    firstname VARCHAR (128),
    lastname VARCHAR (128),
    phone VARCHAR (128),
    email VARCHAR (1024)
);
