    function addDatePicker() {
        var format = 'yy-mm-dd';
        $(function() {
            $("#fromDate").datepicker({
                dateFormat: format,
                showOn: "button",
                buttonImage: "pages/framework/images/mainbox/calendaricon.gif",
                buttonImageOnly: true
            });
            $("#fromDate").val(todayDate())
        });
        $(function() {
            $("#toDate").datepicker({
                dateFormat: format,
                showOn: "button",
                buttonImage: "pages/framework/images/mainbox/calendaricon.gif",
                buttonImageOnly: true
            });
            $("#toDate").val(todayDate())
        });
    }

    function todayDate() {
        return new Date().getFullYear() + "-" + (new Date().getMonth() + 1) + "-" + new Date().getDate();
    }

    function goToAdmin() {
         window.location.href="AdminAction.do?method=load";
    }

    function gotToConfirmExportDeletePage() {
         window.location.href='confirmExportsDelete.ftl';
     }

    function goToViewExports() {
         window.location.href='renderAccountingDataCacheInfo.ftl';
    }
    
    function deleteCacheDir() {
        $.ajax({
            url   : 'deleteCacheDir.ftl',
            cache : false,
            error : function (xhr, ajaxOptions, thrownError){
                    alert(xhr.responseText + thrownError);
                    },
            success : function(data) {
                         goToViewExports();
                    }
              })
    }
    
   function loadExportsList(i) {
        $.ajax({
            url   : 'generateExportsList.ftl?listSize='+i,
            cache : false,
            error : function (xhr, ajaxOptions, thrownError){
                    alert(xhr.responseText + thrownError);
                    },
            success : function(data) {
                         $("#auto_export_table").html(data);
                         addExportListLink(i);
                    }
              })
    }
    function addExportListLink(i) {
        if(i == 0 ) {
                      $("#add_auto_export").html("<a href='#' onclick='javascript:loadExportsList(10);'>Show Past Exports from Today</a>");
                      $('#auto_export_table').html('');
       } else {
             $("#add_auto_export").html("<a href='#' onclick='javascript:loadExportsList("+(i+10)+");'>More 10</a> &nbsp;&nbsp;&nbsp;"
                                       +"<a href='#' onclick='javascript:loadExportsList("+(i-10)+");'>Less 10</a>&nbsp;&nbsp;&nbsp;"
                                       +"<a href='#' onclick='javascript:loadExportsList(0);'>Clear</a>&nbsp;&nbsp;&nbsp;");
              }
             
    }


var gAutoPrint = true;

function processPrint(table){

if (document.getElementById != null){
var html = '<HTML>\n<HEAD>\n';
if (document.getElementsByTagName != null){
var headTags = document.getElementsByTagName("head");
if (headTags.length > 0) html += headTags[0].innerHTML;

}

html += '\n</HE' + 'AD>\n<BODY>\n';
var printReadyElem = document.getElementById(table);

if (printReadyElem != null)
html += printReadyElem.innerHTML;
else{
alert("Error, no contents.");
return;
}

html += '\n</BO' + 'DY>\n</HT' + 'ML>';

var printWin = window.open("","processPrint");
printWin.document.open();
printWin.document.write(html);
printWin.document.close();

if (gAutoPrint) printWin.print();
} else alert("Browser not supported.");
}