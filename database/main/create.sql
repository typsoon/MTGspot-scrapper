CREATE TABLE ListsLogos (
    logo_id SERIAL PRIMARY KEY,
    logo_path VARCHAR UNIQUE NOT NULL
);

CREATE TABLE Lists (
    list_id SERIAL PRIMARY KEY,
    list_name VARCHAR UNIQUE NOT NULL,
    logo_id INTEGER REFERENCES ListsLogos
);

CREATE TABLE Cards (
       multiverse_id INTEGER PRIMARY KEY CHECK ( multiverse_id > 0 ),
       card_name VARCHAR NOT NULL,
       previous_price NUMERIC(4, 2) CHECK ( previous_price IS NULL OR previous_price > 0 ),
       actual_price NUMERIC(4,2) CHECK ( actual_price IS NULL OR actual_price > 0 ),
       image_address VARCHAR UNIQUE NOT NULL
);

CREATE TABLE ListCards (
    list_id INTEGER REFERENCES Lists NOT NULL,
    multiverse_id INTEGER REFERENCES Cards NOT NULL,
    UNIQUE (list_id, multiverse_id)
);

INSERT INTO Lists(list_name) VALUES ('Collection');