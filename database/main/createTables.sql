CREATE SCHEMA scrapper;

CREATE TABLE scrapper.ListsLogos (
     logo_id SERIAL PRIMARY KEY,
     logo_path VARCHAR UNIQUE NOT NULL
);

CREATE TABLE scrapper.Lists (
    list_id SERIAL PRIMARY KEY,
    list_name VARCHAR UNIQUE NOT NULL,
    logo_id INTEGER REFERENCES scrapper.ListsLogos
);
CREATE UNIQUE INDEX ON scrapper.Lists (list_name);

CREATE TABLE scrapper.AllCards (
   multiverse_id INTEGER PRIMARY KEY CHECK ( multiverse_id > 0 ),
   card_name VARCHAR UNIQUE NOT NULL
);
CREATE UNIQUE INDEX ON scrapper.AllCards (card_name);

CREATE TABLE scrapper.DownloadedCards (
    multiverse_id INTEGER PRIMARY KEY REFERENCES scrapper.AllCards,
    previous_price NUMERIC(4, 2) CHECK ( previous_price = -1 OR previous_price IS NULL OR previous_price > 0 ),
    actual_price NUMERIC(4,2) CHECK ( actual_price = -1 OR actual_price IS NULL OR actual_price > 0 ),
    image_url VARCHAR UNIQUE NOT NULL
);

CREATE TABLE scrapper.LocalAddresses (
   multiverse_id INTEGER PRIMARY KEY REFERENCES scrapper.DownloadedCards,
   local_address VARCHAR UNIQUE NOT NULL
);

CREATE TABLE scrapper.ListCards (
    list_id INTEGER REFERENCES scrapper.Lists NOT NULL,
    multiverse_id INTEGER REFERENCES scrapper.DownloadedCards NOT NULL,
    UNIQUE (list_id, multiverse_id)
);
CREATE INDEX ON scrapper.ListCards (list_id);


INSERT INTO scrapper.Lists(list_name) VALUES ('Collection');