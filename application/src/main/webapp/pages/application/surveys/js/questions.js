function setDisable() {
       var disabled = true;
       var value = document.genericActionForm.answerType.value;
       if (value == '1' || value == '4') {
           disabled = false;
       }
       document.getElementById('choice').disabled = disabled;
       document.getElementById('AddButton').disabled = disabled;
}

function clickCancel() {
	form = document.forms['genericActionForm'];
	form.action='adminAction.do?method=load';
	form.submit();
}


function submitQuestionForm(method) {
	form = document.forms['genericActionForm'];
	form.action='questionsAction.do?method=' + method;
	form.submit();
}

function submitForm(form, action) {
	form = document.forms[form];
	form.action=action;
	form.submit();
}

	
function submitSurveyForm(method) {
	form = document.forms['genericActionForm'];
	form.action='surveysAction.do?method=' + method;
	form.submit();
}

function submitSurveyInstanceForm(method) {
	form = document.forms['genericActionForm'];
	form.action='surveyInstanceAction.do?method=' + method;
	form.submit();
}
	
