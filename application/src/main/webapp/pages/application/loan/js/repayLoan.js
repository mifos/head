var RepayLoan = {};

RepayLoan.renderAmount = function (selectedOption) {
    if(selectedOption == "false") {
        $("#totalRepaymentAmount").show();
        $("#waivedRepaymentAmount").hide();
        $("#waiverInterestWarning").hide();
        $("input[name=amount]").attr('value',$("input[name=repaymentAmount]").val())
    }else{
        $("#totalRepaymentAmount").hide();
        $("#waivedRepaymentAmount").show();
        $("#waiverInterestWarning").show();
        $("input[name=amount]").attr('value',$("input[name=waivedAmount]").val())
    }
}

$(document).ready(function(){

    $("input[name=waiverInterest]").bind("change", function(){
        RepayLoan.renderAmount($(this).val());
    });

    RepayLoan.renderAmount($('input:radio[name=waiverInterest]:checked').val());

});