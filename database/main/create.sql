CREATE TABLE ListsLogos (
    logo_id SERIAL PRIMARY KEY,
    logo_path VARCHAR UNIQUE NOT NULL
);

CREATE TABLE Lists (
    list_id SERIAL PRIMARY KEY,
    list_name VARCHAR UNIQUE NOT NULL,
    logo_id INTEGER REFERENCES ListsLogos
);
CREATE UNIQUE INDEX ON Lists (list_name);

CREATE TABLE Cards (
   multiverse_id INTEGER PRIMARY KEY CHECK ( multiverse_id > 0 ),
   card_name VARCHAR UNIQUE NOT NULL,
   previous_price NUMERIC(4, 2) CHECK ( previous_price = -1 OR previous_price IS NULL OR previous_price > 0 ),
   actual_price NUMERIC(4,2) CHECK ( actual_price = -1 OR actual_price IS NULL OR actual_price > 0 ),
   image_url VARCHAR UNIQUE NOT NULL
);
CREATE UNIQUE INDEX ON Cards (card_name);

CREATE TABLE LocalAddresses (
   multiverse_id INTEGER PRIMARY KEY REFERENCES Cards,
   local_address VARCHAR UNIQUE NOT NULL
);

CREATE TABLE ListCards (
    list_id INTEGER REFERENCES Lists NOT NULL,
    multiverse_id INTEGER REFERENCES Cards NOT NULL,
    UNIQUE (list_id, multiverse_id)
);
CREATE INDEX ON ListCards (list_id);

CREATE VIEW FullCardData AS (
    SELECT * FROM Cards LEFT JOIN LocalAddresses USING (multiverse_id)
);

CREATE OR REPLACE VIEW FullListData AS (
        SELECT * FROM Lists
        JOIN ListCards USING(list_id)
        JOIN FullCardData using(multiverse_id)
);

CREATE TABLE NamesAndMultiverseID (
    multiverse_id INTEGER primary key ,
    name VARCHAR UNIQUE NOT NULL
);
CREATE UNIQUE INDEX ON NamesAndMultiverseID(name);

INSERT INTO Lists(list_name) VALUES ('Collection');