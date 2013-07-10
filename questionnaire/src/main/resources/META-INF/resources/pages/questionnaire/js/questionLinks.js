$(document).ready(function() {
    
    var updateSectionsAndQuestionsVisibility = function(response) {
        for (var questionId in response.questions) {
            if (response.questions.hasOwnProperty(questionId)) {
                var isVisible = response.questions[questionId];
                $("#question" + questionId).css("display", 
                        isVisible ? "table-row" : "none");
            }
        }
        for (var sectionId in response.sections) {
            if (response.sections.hasOwnProperty(sectionId)) {
                var isVisible = response.sections[sectionId];
                $("#section" + sectionId).css("display", 
                        isVisible ? "table" : "none");
                $("#section" + sectionId + ".section-name").next('fieldset').css("display", 
                        isVisible ? "block" : "none");
            }
        }
    },
    updateQuestions = function() {                
        var questionId = $(this).closest("li, div[class^=row]").attr("data-question-id"),
            questionResponse = $(this).val();
                
        $.ajax({
            type: "POST",
            url: "getHiddenVisibleQuestions.ftl",
            data: {questionId: questionId, response: questionResponse},
            success: function(response) {
                updateSectionsAndQuestionsVisibility(response);
            }
         });
    };
    
    $('.question.date-pick, .date-pick').change(updateQuestions);
    $('.question, .free-text, .numeric').blur(updateQuestions);
    $('input[type=radio]').click(updateQuestions);

    
    var allQuestionsIds = ""
            
    $('li, div').filter(function() { 
            return /^question[0-9]+$/.test(this.id); 
        }).each(function() {
            allQuestionsIds += $(this).attr("data-question-id") + ",";
        });
    
    if (allQuestionsIds !== "") {
        allQuestionsIds = allQuestionsIds.substring(0, allQuestionsIds.length-1);
    }
    
    var allSectionsIds = ""
        
    $('div').filter(function() { 
            return /^section[0-9]+$/.test(this.id); 
        }).each(function() {
            allSectionsIds += $(this).attr("data-section-id") + ",";
        });
    
    if (allSectionsIds !== "") {
        allSectionsIds = allSectionsIds.substring(0, allSectionsIds.length-1);
    }
       
    $.ajax({
        type: "POST",
        url: "hideAttachedQuestions.ftl",
        data: {questionsId: allQuestionsIds, sectionsId: allSectionsIds},
        success: function(response) {
            for (var questionId in response.questions) {
                $("#question" + response.questions[questionId]).css("display", "none");
            }
            for (var sectionId in response.sections) {
               $("#section" + response.sections[sectionId]).css("display", "none");
               $("#section" + response.sections[sectionId] + ".section-name").next('fieldset').css("display", "none");
            }
        }
     });
 
});