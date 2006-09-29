# This is a trivial mifos client in Ruby

require 'soap/wsdlDriver'

if ARGV.size == 1
  wsdl_url="http://localhost:8080/mifos/services/MifosService?wsdl"
  soap = SOAP::WSDLDriverFactory.new(wsdl_url).create_rpc_driver
  soap.endpoint_url = "http://localhost:8080/mifos/services/MifosService/findLoan"

  param = {}
  param["id"] = ARGV[0].to_i

  result = soap.findLoan(param)

  puts "Borrower with Id #{result.loan.id}, #{result.loan.borrowerName} owes #{result.loan.balance} #{result.loan.balanceCurrencyName}S"
  # A good extension demo would generate a Rails app, and use ActiveSupport's 
  #   pluralize to deal with the currency display.
else
  puts "Usage: mifosSoapTest <id>"
end
