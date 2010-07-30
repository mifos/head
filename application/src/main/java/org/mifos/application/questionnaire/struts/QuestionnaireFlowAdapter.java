package org.mifos.application.questionnaire.struts;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.customers.client.util.helpers.ClientConstants;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mifos.platform.questionnaire.service.ValidationException;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetails;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.util.CollectionUtils;
import org.mifos.security.util.UserContext;
import org.mifos.service.MifosServiceFactory;

import antlr.debug.GuessingEvent;

public class QuestionnaireFlowAdapter {

    private ActionForwards joinFlowAt;
    private String event;
    private String source;
    private String cancelToURL;

    public QuestionnaireFlowAdapter(String event, String source, ActionForwards joinFlowAt, String cancelToURL) {
       this.event = event;
       this.source = source;
       this.joinFlowAt = joinFlowAt;
       this.cancelToURL = cancelToURL;
    }

    public ActionForward mapToAppliedQuestions(ActionMapping mapping, QuestionResponseCapturer form, HttpServletRequest request,
                                               ActionForwards defaultForward) {
        List<QuestionGroupDetail> questionGroups = getQuestionGroups(request);
        if ((questionGroups == null) || (questionGroups.isEmpty()))  {
            return mapping.findForward(defaultForward.toString());
        }
        form.setQuestionGroups(questionGroups);
        request.setAttribute("questionsHostForm", form);
        request.setAttribute("origFlowRequestURI", getContextUri(request));
        request.setAttribute("cancelToURL", cancelToURL);
        return mapping.findForward(ActionForwards.captureQuestionResponses.toString());
    }

    private Object getContextUri(HttpServletRequest request) {
        return request.getRequestURI().replace(request.getContextPath(), "");
    }

    private List<QuestionGroupDetail> getQuestionGroups(HttpServletRequest request) {
        QuestionnaireServiceFacade questionnaireServiceFacade = MifosServiceFactory.getQuestionnaireServiceFacade(request);
        if (questionnaireServiceFacade == null) {
            return null;
        }
        return questionnaireServiceFacade.getQuestionGroups(event, source);
    }

    public ActionErrors validateQuestionGroupResponses(HttpServletRequest request, QuestionResponseCapturer form) {
            List<QuestionGroupDetail> groups = form.getQuestionGroups();
            QuestionnaireServiceFacade questionnaireServiceFacade = MifosServiceFactory.getQuestionnaireServiceFacade(request);
            if (groups == null) {
                return null;
            }
            ActionErrors errors = new ActionErrors();
            try {
                questionnaireServiceFacade.validateResponses(groups);
            } catch (ValidationException e) {
                if (e.containsChildExceptions()) {
                    for (ValidationException validationException : e.getChildExceptions()) {
                       ActionMessage actionMessage =
                         new ActionMessage(ClientConstants.ERROR_REQUIRED, validationException.getSectionQuestionDetail().getTitle());
                       errors.add(ClientConstants.ERROR_REQUIRED, actionMessage);
                    }
                }
            }
            if (!errors.isEmpty()) {
                request.setAttribute("questionsHostForm", form);
                request.setAttribute("origFlowRequestURI", getContextUri(request));
                request.setAttribute("cancelToURL", cancelToURL);
            }
            return errors;
    }

    public void saveQuestionResponses(HttpServletRequest request, QuestionResponseCapturer form, int associateWithId) {
        List<QuestionGroupDetail> questionResponses = form.getQuestionGroups();
        if (CollectionUtils.isEmpty(questionResponses)) {
            return;
        }
        //PersonnelPersistence personnelPersistence = new PersonnelPersistence();
        //PersonnelBO currentUser = personnelPersistence.findPersonnelById(userContext.getId());
        QuestionnaireServiceFacade questionnaireServiceFacade = MifosServiceFactory.getQuestionnaireServiceFacade(request);
        if (questionnaireServiceFacade != null) {
            UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
            questionnaireServiceFacade.saveResponses(
                    new QuestionGroupDetails(userContext.getId(), associateWithId, questionResponses));
        }
    }

    public ActionForward rejoin(ActionMapping mapping) {
        return mapping.findForward(joinFlowAt.toString());
    }
}
