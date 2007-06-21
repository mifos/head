function setDisable() {
       var disabled = true;
       var value = document.questionActionForm.answerType.selectedIndex;
       if (value == '2') {
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
	form = document.forms['questionActionForm'];
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
	
