-- Should only be run immediately after building the database. See "build_db"
-- target.
--
-- Also see:
--    http://thread.gmane.org/gmane.comp.finance.mifos.functional/983
--    http://thread.gmane.org/gmane.comp.finance.mifos.devel/4545
--    http://mifos.org/developers/wiki/ImprovePasswordInitialization
UPDATE personnel SET PASSWORD=0x22648CA42330A561964DBE32F54E6A04A49D03F203CA64B2515338D9 WHERE PERSONNEL_ID=1;
