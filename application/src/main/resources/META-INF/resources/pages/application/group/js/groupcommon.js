function goToCancelPage(groupActionForm){
	if (this.goBackToViewGroupDetails != null){
		goBackToViewGroupDetails.submit();
	} else {
		groupActionForm.action="groupCustAction.do?method=cancel";
		groupActionForm.submit();		
	}
  }