CREATE OR REPLACE RULE add_list AS ON INSERT TO listswithlogos
    DO INSTEAD (
        INSERT INTO lists(list_name) VALUES (new.list_name) RETURNING logo_id, list_id, list_name, null::varchar;
    );

CREATE OR REPLACE RULE update_price AS ON UPDATE TO scrapper.CardsWithPrices
    DO INSTEAD (
    UPDATE scrapper.downloadedcards SET previous_price = actual_price, actual_price = new.actual_price
    WHERE multiverse_id = new.multiverse_id;
);

CREATE OR REPLACE RULE set_address AS ON INSERT TO scrapper.cardsimagesaddresses
    DO INSTEAD (
    INSERT INTO localaddresses(multiverse_id, local_address) VALUES (new.multiverse_id, new.local_address);
);

CREATE OR REPLACE RULE add_card AS ON INSERT TO scrapper.FullDownloadedCardData
    DO INSTEAD (
    INSERT INTO scrapper.AllCards(multiverse_id, card_name) VALUES (new.multiverse_id, new.card_name)
    ON CONFLICT (card_name) DO UPDATE SET multiverse_id = excluded.multiverse_id;

    INSERT INTO scrapper.DownloadedCards(multiverse_id, image_url)
    VALUES (new.multiverse_id, new.image_url);
);

CREATE OR REPLACE RULE add_card_to_list AS ON INSERT TO scrapper.fulllistdata
    DO INSTEAD (
        INSERT INTO scrapper.listcards(list_id, multiverse_id) VALUES (new.list_id, new.multiverse_id);
    );

CREATE OR REPLACE RULE delete_card_from_list AS ON DELETE TO scrapper.fulllistdata
    DO INSTEAD (
        DELETE FROM scrapper.listcards WHERE (list_id, multiverse_id) = (old.list_id, old.multiverse_id);
    );

CREATE OR REPLACE FUNCTION scrapper.delete_list(deleted_list_name VARCHAR) RETURNS VOID AS
$$
    DECLARE
        deleted_list_id INTEGER = (
                SELECT list_id FROM lists WHERE lists.list_name = deleted_list_name
            );

        deleted_logo_id INTEGER = (
                SELECT logo_id FROM lists WHERE list_id = deleted_list_id
            );
    BEGIN
        IF deleted_list_name IS NULL THEN
            RAISE NOTICE 'cannot delete list with null name';
        END IF;

        DELETE FROM listcards WHERE list_id = deleted_list_id;
        DELETE FROM lists WHERE list_id = deleted_list_id;

        IF (SELECT COUNT(*) FROM lists WHERE logo_id = deleted_logo_id) = 0 THEN
            DELETE FROM listslogos WHERE logo_id = deleted_logo_id;
        END IF;
    END
$$
LANGUAGE plpgsql;

CREATE OR REPLACE RULE delete_card_from_list AS ON DELETE TO listswithlogos
    DO INSTEAD (
        SELECT delete_list(old.list_name);
    );