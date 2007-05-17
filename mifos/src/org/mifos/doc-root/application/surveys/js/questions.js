function setDisable() {
       var disabled = true;
       var value = document.questionActionForm.answerType.selectedIndex;
       if (value == '2') {
           disabled = false;
       }
       document.getElementById('choice').disabled = disabled;
       document.getElementById('AddButton').disabled = disabled;
}

function submitQuestionForm(method) {
	form = document.forms['questionActionForm'];
	form.action='questionsAction.do?method=' + method;
	form.submit();
}
	

