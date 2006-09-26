# This class call all the testcases from the Check_List_Test_Cases class
require 'mysql'
require 'test/unit'
require 'watir'
include Watir
require 'modules/check_list/Check_List_Test_Cases'

class Check_List
  checklist_obj=Check_List_Test_Cases.new
  checklist_obj.CheckList_login
  checklist_obj.Click_Admin_page
  checklist_obj.Check_CheckList_Links
  checklist_obj.Check_New_CheckList_cancel
  filename = File.expand_path(File.dirname($PROGRAM_NAME))+"/data/checklist.xls"
  checklist_obj.open(filename,1)  
  count=0
  rowid=-1
  while(rowid < $maxrow * $maxcol - 1)
    checklist_obj.read_checklist_values(rowid)
   if (0 == count )
    checklist_obj.Man_New_CheckList
    checklist_obj.Man_New_CheckList_with_name(checklist_obj.Checklist_name)
    checklist_obj.Man_New_CheckList_with_type(checklist_obj.Checklist_type)
    checklist_obj.Man_New_CheckList_with_status(checklist_obj.Checklist_status)
    $ie.link(:text,"Admin").click
   end    
  checklist_obj.Create_New_CheckList(checklist_obj.Checklist_name,checklist_obj.Checklist_type,checklist_obj.Checklist_status,checklist_obj.Checklist_items,checklist_obj.Type )
  count = 1
  rowid+=$maxcol
  end
  checklist_obj.delete_all_checklist()
  checklist_obj.checklist_logout()
end
