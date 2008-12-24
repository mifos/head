#calls all the testcases in the BulkEntry 
require 'watir'
require 'modules/common/TestClass'
require 'modules/common/inputs'
require 'modules/Bulkentry/BulkEntry'

class BulkEntryTestCase 
  
    bulkentry=BulkEntry.new 
    type_val1=0
    type_val2=1
    type_val3=2
    bulkentry.init
    bulkentry.check_errormsg_all()
    bulkentry.err_msg_with_only_BOselected()
    bulkentry.err_msg_with_BOandLOselected()
    bulkentry.err_msg_with_BO_LO_Center_selected()
    bulkentry.msg_no_collection_due()
    bulkentry.bulk_entry(type_val1)
    bulkentry.setvalue_diff(type_val2)
    bulkentry.setvalue_diff(type_val3)
    bulkentry.clean
end
