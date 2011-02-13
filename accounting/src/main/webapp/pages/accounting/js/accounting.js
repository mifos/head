function goToAdmin() {
	window.location.href = "AdminAction.do?method=load";
}

function gotToConfirmExportDeletePage() {
	window.location.href = 'confirmExportsDelete.ftl';
}

function goToViewExports() {
	window.location.href = 'renderAccountingDataCacheInfo.ftl';
}

function deleteCacheDir() {
	$.ajax({
		url : 'deleteCacheDir.ftl',
		cache : false,
		error : function(xhr, ajaxOptions, thrownError) {
			alert(xhr.responseText + thrownError);
		},
		success : function(data) {
			goToViewExports();
		}
	})
}

function loadExportsList(listStartDay, totalNumberOfExports) {
	$.ajax({
		url : 'generateExportsList.ftl?listStartDay=' + listStartDay +'&type=all',
		cache : false,
		error : function(xhr, ajaxOptions, thrownError) {
			alert(xhr.responseText + thrownError);
		},
		success : function(data) {
			$("#export_list").html(data);
			addExportListLink(listStartDay, totalNumberOfExports);
		}
	})
}

function addExportListLink(listStartDay, totalNumberOfExports) {
	if(listStartDay == 0) {
	   $("#previous").html("<font color='grey'>Previous</font>");
	}
	else
	{
        $("#previous").html("<a href='#' onclick=\"javascript:loadExportsList("+ (listStartDay-10) + ","+totalNumberOfExports+");\">Previous</a>");
    }

	if(listStartDay > (totalNumberOfExports - 11)) {
	    $("#results").html("Results "+(listStartDay+1)+"-"+(totalNumberOfExports)+" of "+totalNumberOfExports);
	    $("#next").html("<font color='grey'>Next</font>");
	}
	else 
	{
	    $("#results").html("Results "+(listStartDay+1)+"-"+(listStartDay +10)+" of "+totalNumberOfExports);
        $("#next").html("<a href='#' onclick=\"javascript:loadExportsList("+ (listStartDay+10) + ","+totalNumberOfExports+");\">Next</a>");
    }
}

var gAutoPrint = true;

function processPrint(table) {

	if (document.getElementById != null) {
		var html = '<HTML>\n<HEAD>\n';
		if (document.getElementsByTagName != null) {
			var headTags = document.getElementsByTagName("head");
			if (headTags.length > 0)
				html += headTags[0].innerHTML;

		}

		html += '\n</HE' + 'AD>\n<BODY>\n';
		var printReadyElem = document.getElementById(table);

		if (printReadyElem != null)
			html += printReadyElem.innerHTML;
		else {
			alert("Error, no contents.");
			return;
		}

		html += '\n</BO' + 'DY>\n</HT' + 'ML>';

		var printWin = window.open("", "processPrint");
		printWin.document.open();
		printWin.document.write(html);
		printWin.document.close();

		if (gAutoPrint)
			printWin.print();
	} else
		alert("Browser not supported.");
}