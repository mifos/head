#this is the ruby file which has common methods like login,dbconnection
require 'mysql'
require 'test/unit'
require 'modules/logger/example_logger1'
require 'test/unit/ui/console/testrunner'
require 'test/unit/assertions'
include Test::Unit::Assertions
require 'watir'
include Watir
class TestClass 
    #creates ie object and loggerobject
	def start()
		$ie=IE.new	
		$logger = LoggerFactory.start_xml_logger($filePrefix) 
		$ie.set_logger($logger)
	end
	def meeting(type)
     begin
      @@meetin_msg="Meeting schedule for "+type
      $ie.link(:text,"Schedule Meeting").click
      assert($ie.contains_text(@@meetin_msg))
      $logger.log_results("Meeeting page Opened","NA","NA","Passed")
     rescue =>e
      $logger.log_results("Meeeting page Opened","NA","NA","Failed")
     end
    end
	#opens Excel file and counts the noof rows and columns
	def open(filename,book)
		if FileTest.exist?(filename) 
		@@excel = WIN32OLE::new('excel.Application')
		workbook = @@excel.Workbooks.Open(filename)
		puts filename
		worksheet = workbook.Worksheets(book)
		rowval1=2
		colval1=1
		@@s=Array.new
		i=0
		$maxrow=0
		$maxcol=0
		while  (worksheet.cells(rowval1,colval1).value)
			colval2=2
			while (worksheet.cells(rowval1,colval2).value) 
			
			name=worksheet.cells(rowval1,colval2).value
			$maxcol=colval2-1
			colval2+=1
			@@s[i]=name
			i+=1
			end 
			$maxrow=rowval1-1
		rowval1+=1
		
		end
		@@excel.quit
		
		
		else
		$logger.log_results("file"+filename+" does not exist ","NA","NA","Exiting")
		$logger.end_log
        $ie.link(:text,"Logout").click
		$ie.close
		exit
		end
	end
	def string_replace(error_message,string_to_replace,replace_string)
	   @@message_error=error_message
	   @@message_error["<li>"]=""	   
	   @@message_error["</li>"]=""
	   @@message_error[string_to_replace]=replace_string
	   @@message_error.squeeze(" ")
	   @@message_error
	end
	def string_replace_message(error_message,string_to_replace,replace_string)
	   error=error_message.clone
	   error[string_to_replace]=replace_string
	   error.squeeze(" ")
	   error
	end   
	#def New_error_message
	#   @@message_error
	#end
	#Method to read lables and error messages from properties file
	def load_properties(properties_filename)
	    properties = {}
        File.open(properties_filename, 'r') do |properties_file|
        properties_file.read.each_line do |line|
        line.strip!
        if (line[0] != ?# 	and line[0] != ?=)
          i = line.index('=')
          if (i)
            properties[line[0..i - 1].strip] = line[i + 1..-1].strip
          else
            properties[line] = ''
          end
        end
      end      
    end
    properties
  end
	#clicks the Clients & Accounts link
	def click_clientslink
		$ie.link(:text,"Clients & Accounts").click
	end
		
	def arrval
		@@s
	end
	# logging in to the application
	def login(uname,pword)
		#start()
		error_chk()
		$ie.text_field(:name, $username).set(uname)
		$ie.text_field(:name, $password).set(pword)
		$ie.button(:caption, $log_in).click
		$ie.wait
		begin
		assert($ie.link(:text,"Admin").exists?()) and assert($ie.link(:text,"Clients & Accounts").exists?())
		$logger.log_results("login",$validname,$validname,"passed")
		rescue Test::Unit::AssertionFailedError=>e
		$logger.log_results("login","","","failed")
		$loggger.end_log
		$ie.close
		exit
		end
	end
	# checking for the errors
	def error_chk()
		
		begin
		$ie.goto($test_site)
		$ie.wait
		$ie.maximize
		rescue StandardError => bang
		$logger.log_results("cannot access site"+$test_site+""+bang,"","","exiting")
		#$ie.close
		$logger.end_log
		exit
		end 
	end
	#clicks on fee name
	def click_fee(fee_name)
		$ie.link(:text,fee_name).click
		$ie.wait
	end
 
	def error_page()
		begin
		assert($ie.title()!="Error Page")
		rescue Test::Unit::AssertionFailedError=>e
		$logger.log_results("navigated to error page","","","failed")
		$logger.end_log
		$ie.close
		exit
		end
	end
    #coonects to DB
	def db_connect()
		begin
		$dbh=Mysql.real_connect($hostname,$dbuser,$dbpwd,$db)
		rescue MysqlError=>e
		$logger.log_results("error "+e.to_s,"NA","NA","exiting")
		$ie.link(:text,"Logout").click
		$logger.end_log
		$ie.close
		exit
		end
	end
	#closes all the opened objects
	def clean_up()
		$dbh.close unless ($dbh.nil?)
		#puts "value of dbh "+$dbh.to_s
		$logger.end_log
		$ie.close
	end
    #storing the database values in to result set
	def dbquery(query)
		$res=$dbh.real_query(query)
		res1=$res
		@@resultset=$res.fetch_row.to_a
		$row=$res.num_rows()
		puts $row
	end
	# gets next row in the result set	
	def get_next_data()
	     @@resultset=$res.fetch_row.to_a
	end
	def dbresult()
		@@resultset
	end
	#checks the database values
	def dbcheck(message,input,indb)
		begin
		assert_equal(indb,input)
		$logger.log_results(message,input.to_s,"In database "+indb.to_s,"passed")
		rescue Test::Unit::AssertionFailedError=>e
		$logger.log_results(message,input.to_s,"In database "+indb.to_s,"failed")
		end
	end
    def link_check(linkname)
		begin
		assert($ie.link(:text,linkname).exists?())
		$logger.log_results("link",linkname,"exists","passed")
		$ie.link(:text,linkname).click
		rescue Test::Unit::AssertionFailedError=>e
		$logger.log_results("error ","could not find"+linkname,"NA","failed")
		$ie.link(:text,"Logout").click
		$logger.end_log
		$ie.close
		exit
		end
	end
	def check_value(value)
    begin
      if value== "0" or value== "0.0" then 
         value = ""
      end
      value  
    end
    end 
     # Login to Mifos and check the first page after login page
   def mifos_login()
        start
		login($validname,$validpwd)
		db_connect
		dbquery("select display_name from personnel where login_name = '" + $validname + "'")		
		if $ie.contains_text("Welcome,  "+ dbresult[0].to_s) then
		  $logger.log_results("Mandatory Check after login", "UserName: " + $validname, "Successful login","Passed")
		else 
		  $logger.log_results("Mandatory Check after login", "UserName: " + $validname, "Successful login","Failed")
		end
   end
    
  # Check for admin page after login
  def Click_Admin_page()
      $ie.link(:text,"Admin").click
      verify_admin_page()
  end
  
  # Verifies the admin page
  def verify_admin_page()  
    assert($ie.contains_text("Administrative tasks")) and assert($ie.contains_text("Manage organization"))
        $logger.log_results("Office- Check for the admin page", "Click on admin link","Access to the admin page","Passed")
    rescue =>e
        $logger.log_results("Office- Check for the admin page", "Click on admin link","Access to the admin page","Failed") 
  end 

  # Enter the data in the text field
  def set_value_txtfield(field_name, value)
	#if(not ("" == value))
    #    $ie.text_field( :name, field_name).set(value)      
    # end
    if(!value.nil?)
      $ie.text_field( :name, field_name).set(value)      
    end
  end

  # Enter the data in the list  
  def set_value_selectlist(field_name, value)
	if(not ("" == value))
    	$ie.select_list(:name, field_name).select(value)
     end
  end
  
  #to check for error message for max length of strings
  def max_field_len(string_name,maxlen,message)
  if (string_name.nil?)
    len=string_name.to_i
  #puts string_name.length
  else
    len=string_name.length
  end
  if(len > maxlen)
       
      begin 
        assert($ie.contains_text(message))
        $logger.log_results("Error message appears when input string length "+len.to_s+" is greater than max field length of "+maxlen.to_s,string_name,message,"passed")
        rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Error message does not appear when input string length "+len.to_s+" is greater than max field length of "+maxlen.to_s,string_name,message,"failed")
        end
  else
       begin
        assert(!$ie.contains_text(message))
        $logger.log_results("No error message as Size of input string is " +len.to_s+" and less than "+maxlen.to_s,string_name.to_s,"No error message","passed")
        rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("max length error message appears when string length is  "+len.to_s+" less than "+maxlen.to_s,string_name.to_s,"No error message","failed")
       end
  
  end
  end
  
  def max_error_message_number(num,max,message,field)
    if (num.to_f > max)
      begin 
        assert($ie.contains_text(message))
        $logger.log_results("Error message appears for field "+field.to_s+" when input number "+num.to_s+" is greater than max value of "+max.to_s,num.to_s,message.to_s,"passed")
        rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Error message does not appear for field "+field.to_s+" when input number "+num.to_s+" is greater than max value of "+max.to_s,num.to_s,message.to_s,"failed")
        end
    else
      begin
        assert(!$ie.contains_text(message))
        $logger.log_results("No error message appears for field "+field.to_s+" when input number "+num.to_s+" is less than or equal than max value of "+max.to_s,num.to_s,"no error message","passed")
        rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results("Error message appears for field "+field.to_s+" when input number "+num.to_s+" is less than or equal than max value of "+max.to_s,num.to_s,"no error message","failed")
        end
    end
    
  end
  
  
  #used to check text on a page 
  def assert_on_page(stringname)
     begin
      assert($ie.contains_text(stringname))
      $logger.log_results(stringname+" appears on page","NA","NA","passed")
     rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results(stringname+" does not appear on page","NA","NA","failed")
     end
  
  end
  
  def count_items(query)
    dbquery(query)
    count=dbresult[0]
    return count
  end
  # To log out from the application 
  def mifos_logout()
    begin
      $ie.link(:text,"Logout").click
	  #$ie.close
	  clean_up()
	  exit
    end
  end  
  
end
