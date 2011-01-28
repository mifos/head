function fun_cancel(form){
	form.method.value="cancelAdjustment";
	form.action="applyAdjustment.do?method=cancelAdjustment";
	form.submit();
}

function fn_submit(){
	
	if(document.getElementsByName("method")[0].value=='loadAdjustment'){

		/*if(!document.getElementsByName("adjustcheckbox")[0].checked || trim(document.getElementsByName("adjustmentNote")[0].value)==''){
		
			return false;
		}*/
		applyAdjustmentActionForm.method.value="previewAdjustment";
		applyAdjustmentActionForm.action="applyAdjustment.do?method=previewAdjustment";
		func_disableSubmitBtn("submit_btn");
		return true;
	}else{

		applyAdjustmentActionForm.method.value="applyAdjustment";
		applyAdjustmentActionForm.action="applyAdjustment.do?method=applyAdjustment";
		func_disableSubmitBtn("submit_btn");
		return true;
	}
	
function trim(str) {
	return str.replace(/^\s*|\s*$/g,"");
} 

	

}