package org.mifos.platform.questionnaire.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestQuestionnaireController {
     @RequestMapping("/questionInfo.ftl")
     public String viewQuestionInfo() {
          return "viewQuestionInfo";
     }
}
