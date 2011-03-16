mysqldump -u root -p $2 $1 --complete-insert --extended-insert --skip-add-locks --skip-comments --skip-quote-names > acceptance_test_dump.sql
