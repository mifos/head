#this class calls all the testcases from the loan class
require 'mysql'
require 'test/unit'
require 'watir'
include Watir
require 'modules/Loans/FeePeriodic'

class FeeP 
loansobject=FeePeriodic.new
loansobject.loans_login
loansobject.loan_account_add_fee
loansobject.loan_account_remove_fee
#$ie.link(:text,"Logout").click
loansobject.mifos_logout
end
