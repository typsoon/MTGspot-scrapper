CREATE TABLE Lists (
    list_id SERIAL PRIMARY KEY,
    list_name VARCHAR UNIQUE NOT NULL
);

CREATE TABLE Cards (
       card_id SERIAL PRIMARY KEY,
       card_name VARCHAR NOT NULL,
       previous_price NUMERIC(4, 2) CHECK ( previous_price IS NULL OR previous_price > 0 ),
       actual_price NUMERIC(4,2) CHECK ( actual_price IS NULL OR actual_price > 0 ),
       image_url VARCHAR UNIQUE NOT NULL
);

CREATE TABLE ListCards (
    list_id INTEGER REFERENCES Lists NOT NULL,
    card_id INTEGER REFERENCES Cards NOT NULL,
    UNIQUE (list_id, card_id)
);
