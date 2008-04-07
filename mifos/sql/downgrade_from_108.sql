-- If the database has a customization of this value, don't change it.
-- But if they have the default 'City' (the result of the upgrade), downgrade it.
update LOOKUP_ENTITY set ENTITY_NAME = 'City ' where
  ENTITY_ID = 80 and ENTITY_NAME = 'City';

UPDATE DATABASE_VERSION SET DATABASE_VERSION = 107 WHERE DATABASE_VERSION = 108;
