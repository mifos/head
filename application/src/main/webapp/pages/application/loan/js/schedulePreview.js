$(document).ready(function() {

    $(":regex(id, dueDate.[0-9]+)").datepicker({
        dateFormat: 'dd-M-yy',
        showOn: "button",
        buttonImage: "pages/framework/images/mainbox/calendaricon.gif",
		buttonImageOnly: true
    });

    if ($("input[id=schedulePreview.button.validate]") != null) {
        $("input[id=schedulePreview.button.validate]").click(function(event) {
            $("input[name=method]").val("validateInstallments");
            form = $("form[name=loanAccountActionForm]");
            form.action="loanAccountAction.do";
            form.submit();
        });
    }
  }
);