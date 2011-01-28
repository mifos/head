var CreateHoliday = {};

CreateHoliday.renderTree = $(function () {
 	$("#officeTree").jstree({ 
        "themes" : {
            "theme" : "default",
            "dots" : true,
            "icons" : false
        },
	        "json_data" : {
	            "ajax" : {
	                "url" : "holidayAction.do",
                    "data" : function (n) { 
                        return { method : n.attr ? n.attr("method") : 'officeHierarchy' }; 
                    }
	            }
	        },
		"plugins" : [ "themes", "json_data", "ui", "checkbox"]
	});
});

CreateHoliday.restoreState = $(function () {
    $("#officeTree").bind("loaded.jstree", function (event, data) 
    {
    	CreateHoliday.setState();
    });
});

CreateHoliday.setState = function checkSelected() {
    var values = $('#selectedOfficeIds').val().split(',');
    $("#officeTree").find('li').each(function () {
        if($.inArray(this.id, values) >= 0 ){
            jQuery.jstree._reference($("#officeTree")).check_node(this);
        }
    });
};

CreateHoliday.selectedOfficeIds = function getTreeVal(){
    var checkedId = '';
    var checkedNodes = jQuery.jstree._reference($("#officeTree")).get_checked();
    checkedNodes.each(function () {
        if(checkedId != ''){
            checkedId = checkedId + ','
        }
        checkedId = checkedId + this.id;
    });
    return checkedId;
};

CreateHoliday.submitPage = $(document).ready(function(){
   $("#holiday\\.button\\.preview").click(function(event){
        $('#selectedOfficeIds').val(CreateHoliday.selectedOfficeIds());
		$('#holidayActionForm').submit();
   });
});
 