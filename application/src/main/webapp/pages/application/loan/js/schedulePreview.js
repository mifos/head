$(document).ready(
   function(evt) {
    $(function()
    {
        $('.date-pick').datePicker({startDate:'01/01/1900'});
    });
    $('.date-pick').keyfilter(/[0-9,\/]/);

    $("input[id=schedulePreview.button.validate]").click(function(event) {
        $("input[name=method]").val("validate");
        form = $("form[name=loanAccountActionForm]");
        form.action="loanAccountAction.do";
        form.submit();
    });
  }
);
