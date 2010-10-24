$(document).ready(function() {
    $("a.stackTrace").click(function(e) {
        e.preventDefault();
        $("a.stackTrace").toggle();
        $("div.stackTrace").toggle();
    });
});
