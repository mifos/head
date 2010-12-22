$(document).ready(function() {

    $(":regex(id, *.[0-9]+)").datepicker({
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

    if ($("input[id=schedulePreview.button.preview]") != null) {
        $("input[id=schedulePreview.button.preview]").click(function(event) {
            $("input[name=method]").val("preview");
            form = $("form[name=loanAccountActionForm]");
            form.action="loanAccountAction.do";
            form.submit();
        });
    }
  }
);