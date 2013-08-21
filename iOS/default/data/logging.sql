DROP TABLE IF EXISTS "logs";
CREATE TABLE logs (
    id INTEGER PRIMARY KEY,
    eventName TEXT,
    eventDate DOUBLE,
    param TEXT,
    mparam TEXT
);

