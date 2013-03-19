$(document).ready(function(){
    $('a[id="filters-toggler"]').click(function(){
        $('td[class="search-filters"]').toggle();
        $('span[class="showorhide"]').toggle();
    });
});