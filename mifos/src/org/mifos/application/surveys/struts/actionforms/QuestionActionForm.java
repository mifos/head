package org.mifos.application.surveys.struts.actionforms;

import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.framework.struts.actionforms.BaseActionForm;

public class QuestionActionForm extends BaseActionForm {

       private String questionText;
       private String answerType;
       private String choice;

       public void clear() {
    	   setQuestionText("");
    	   setAnswerType(Integer.toString(AnswerType.FREETEXT.getValue()));
    	   setChoice("");
       }
       public String getQuestionText() {
               return questionText;
       }

       public void setQuestionText(String text) {
               this.questionText = text;
       }

       public String getAnswerType() {
               return answerType;
       }

       public void setAnswerType(String type) {
               this.answerType = type;
       }

       public String getChoice() {
               return choice;
       }

       public void setChoice(String choice) {
               this.choice = choice;
       }
}
