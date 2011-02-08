LIQUIBASE_DIR=`dirname $0`
$LIQUIBASE_DIR/liquibase.sh --contexts=contraction rollbackToDate $1
