$(document).ready(function(){
    $.datepicker.setDefaults($.datepicker.regional[""]);
    
    $("[name^=filters\\.creationDate]").datepicker({  
        showOn: "button",
        buttonImage: "pages/framework/images/mainbox/calendaricon.gif",
        buttonImageOnly: true,
        dateFormat: 'dd/mm/yy'
    });
    
    $('a[id="filters-toggler"]').click(function(){
        $('td[class="search-filters"]').toggle();
        $('span[class="showorhide"]').toggle();
    });
});