$(document).ready(function() {

	$("#interestRateZero").attr("checked", function(){
		if ( parseFloat($("#interestRate").val()) === 0 ){
			$("#interestRateDetails").css({ 'display' : 'none'});
			return true;
		}
		return false;
	});
	
	$("#interestRate").attr("disabled", $("#interestRateZero").is(":checked"));
	
	$("#interestRateZero").click(function(){
		$("#interestRate").attr("disabled", $(this).is(":checked"));
		if (  $(this).is(":checked") ){
			$("#interestRate").val("0");
			$("#interestRateDetails").fadeOut(1000);		
		} else {
			$("#interestRate").val("");
			$("#interestRateDetails").fadeIn(1000);	
		}
	});
	
	$("#interestRate").change(function(){
		if ( parseFloat($(this).val()) === 0 ){
			$("#interestRateZero").attr("checked", true);
			$("#interestRateZero").triggerHandler("click");
		}
	})
 
});