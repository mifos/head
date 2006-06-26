function fun_cancel(form){
	form.method.value="cancelAdjustment";
	form.action="custApplyAdjustment.do?method=cancelAdjustment";
	form.submit();
}

function fn_submit(){
	
	if(document.getElementsByName("method")[0].value=='loadAdjustment'){
		custApplyAdjustmentActionForm.method.value="previewAdjustment";
		custApplyAdjustmentActionForm.action="custApplyAdjustment.do?method=previewAdjustment";
		func_disableSubmitBtn("submit_btn");
		return true;
	}else{

		custApplyAdjustmentActionForm.method.value="applyAdjustment";
		custApplyAdjustmentActionForm.action="custApplyAdjustment.do?method=applyAdjustment";
		func_disableSubmitBtn("submit_btn");
		return true;
	}
	
function trim(str) {
	return str.replace(/^\s*|\s*$/g,"");
} 


	

}