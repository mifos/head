-- Should only be run immediately after building the database. See "build_db"
-- target.
--
-- Also see:
--    http://thread.gmane.org/gmane.comp.finance.mifos.functional/983
--    http://thread.gmane.org/gmane.comp.finance.mifos.devel/4545
--    http://mifos.org/developers/wiki/ImprovePasswordInitialization
update personnel set password=0x22648ca42330a561964dbe32f54e6a04a49d03f203ca64b2515338d9 where personnel_id=1;
