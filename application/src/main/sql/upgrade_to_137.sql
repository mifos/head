--  Updating parent permission  for
--  "Can adjust payment when account status is "closed-obligation met"". 

update ACTIVITY set PARENT_ID= 113 where ACTIVITY_ID= 217;

update DATABASE_VERSION set DATABASE_VERSION = 137 where DATABASE_VERSION = 136;
