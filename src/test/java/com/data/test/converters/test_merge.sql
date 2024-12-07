INSERT INTO my_table (name, age) VALUES ('Alice', '30') ON DUPLICATE KEY UPDATE age='30';
INSERT INTO my_table (name, age) VALUES ('Bob', '25') ON DUPLICATE KEY UPDATE age='25';
INSERT INTO my_table (name) VALUES ('Foo');
