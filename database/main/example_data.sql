INSERT INTO Lists(list_name) VALUES ('test list'), ('all cards list'), ('third list'), ('4-th list');

INSERT INTO Cards(multiverse_id, card_name, previous_price, actual_price, image_address) VALUES
(129626, 'Llanowar Elves', null, null, 'http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=129626&type=card'),
(600, 'Black Lotus', null, null, 'http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=600&type=card'),
(532539, 'Beast Within', null, null, 'http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=532539&type=card');

INSERT INTO ListCards(list_id, multiverse_id) VALUES (1, 129626), (2,129626), (2,600), (2,532539), (3,600), (4,532539);
