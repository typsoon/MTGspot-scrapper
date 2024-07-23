INSERT INTO Lists(list_name) VALUES ('test list');

INSERT INTO Cards(card_name, previous_price, actual_price, image_url) VALUES
('Llanowar Elves', null, null, 'http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=129626&type=card'),
('Black Lotus', null, null, 'http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=600&type=card'),
('Beast Within', null, null, 'http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=532539&type=card');

INSERT INTO ListCards(list_id, card_id) VALUES (1, 1);
