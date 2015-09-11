-- // create_menu_items
-- Migration SQL that makes the change goes here.

CREATE TABLE IF NOT EXISTS menu_items (
  id          BIGINT(10) UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  parent_id   BIGINT(10) UNSIGNED,
  name         VARCHAR(100)     NOT NULL,
  state        VARCHAR(100)     NOT NULL UNIQUE,
  css          VARCHAR(100),
  position     INT(10) UNSIGNED,
  shortcut     VARCHAR(100),
  description  VARCHAR(255),
  created_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);


-- //@UNDO
-- SQL to undo the change goes here.

DROP TABLE menu_items;


