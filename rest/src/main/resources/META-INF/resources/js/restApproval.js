var baseURL = "rest/API/approval/";

function approve(id) {
	var url = baseURL +"id-"+ id +"/approve.json";
	$.post(url,function(data) {
                location.reload(true);
            })
}

