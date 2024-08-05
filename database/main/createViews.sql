CREATE VIEW scrapper.FullDownloadedCardData AS (
    SELECT * FROM scrapper.DownloadedCards
    LEFT JOIN scrapper.AllCards USING (multiverse_id)
    LEFT JOIN scrapper.LocalAddresses USING (multiverse_id)
);

CREATE VIEW scrapper.CardsWithPrices AS (
    SELECT multiverse_id, previous_price, actual_price FROM scrapper.DownloadedCards
);

CREATE VIEW CardsImagesAddresses AS (
    SELECT * FROM scrapper.downloadedcards LEFT JOIN scrapper.localaddresses USING (multiverse_id)
);

CREATE VIEW AllCardsView AS (
    SELECT * FROM scrapper.allcards
);

CREATE VIEW scrapper.ListsWithLogos AS (
    SELECT * FROM scrapper.Lists LEFT JOIN scrapper.ListsLogos USING (logo_id)
);

CREATE VIEW scrapper.FullListData AS (
    SELECT * FROM scrapper.Lists
    JOIN scrapper.ListCards USING(list_id)
    JOIN scrapper.FullDownloadedCardData using(multiverse_id)
);