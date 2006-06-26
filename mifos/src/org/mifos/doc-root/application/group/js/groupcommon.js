function goToCancelPage(groupActionForm){
	groupActionForm.action="GroupAction.do?method=cancel";
	groupActionForm.submit();
  }