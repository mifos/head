	function getAccountingDataForm() {
		$.ajax({
			url   : 'accountingDataForm.ftl',
			cache : false,
			error : function (xhr, ajaxOptions, thrownError){
                    alert(xhr.responseText + thrownError);
                    },
			success : function(data) {
				      $("#view").html(data);
				      addDatePicker();
			        }
		     })
	}

	function getAccountingDataCacheInfo() {
		$.ajax({
			url   : 'renderAccountingDataCacheInfo.ftl',
			cache : false,
			error : function (xhr, ajaxOptions, thrownError){
                    alert(xhr.responseText + thrownError);
                    },
			success : function(data) {
				       $("#view").html(data);
				       addDatePicker();
			        }
		     })
	}
	
	function getAdvanceOptions() {
		$.ajax({
			url   : 'advanceOptions.ftl',
			cache : false,
			error : function (xhr, ajaxOptions, thrownError){
                    alert(xhr.responseText + thrownError);
                    },
			success : function(data) {
				       $("#view").html(data);
				       addDatePicker();
			        }
		     })
	}

	function submitAccountingDataForm() {
		var fromDate = document.getElementById("fromDate").value;
		var toDate = document.getElementById("toDate").value;
		getAccountionDataDetails(fromDate, toDate);
	}

	function getAccountionDataDetails(fromDate, toDate) {
		document.getElementById("view").innerHTML = "<img src='image/loading.gif' />";
		$.ajax({
			url   : 'renderAccountingData.ftl',
			cache : false,
			data  : 'fromDate=' + fromDate + '&toDate=' + toDate,
			error : function (xhr, ajaxOptions, thrownError){
                    alert(xhr.responseText + thrownError);
                    },
			success : function(data) {
				      $("#view").html(data);
			        }
		      })
	}

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

	function deleteCacheDir() {
		document.getElementById("view").innerHTML = "<img src='image/loading.gif' />";
		$.ajax({
			url   : 'deleteCacheDir.ftl',
			cache : false,
			error : function (xhr, ajaxOptions, thrownError){
                    alert(xhr.responseText + thrownError);
                    },
			success : function(data) {
				      $("#view").html(data);
			        }
		      })
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