function goToCancelPage(groupActionForm){
	groupActionForm.action="groupCustAction.do?method=cancel";
	groupActionForm.submit();
  }