package org.mifos.application.questionnaire.struts;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.customers.client.util.helpers.ClientConstants;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.platform.questionnaire.exceptions.BadNumericResponseException;
import org.mifos.platform.questionnaire.exceptions.MandatoryAnswerNotFoundException;
import org.mifos.platform.questionnaire.exceptions.ValidationException;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetails;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.util.CollectionUtils;
import org.mifos.security.util.UserContext;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class QuestionnaireFlowAdapter {

    private ActionForwards joinFlowAt;
    private String event;
    private String source;
    private String cancelToURL;
    private QuestionnaireServiceFacadeLocator serviceLocator;

    public QuestionnaireFlowAdapter(String event, String source, ActionForwards joinFlowAt, String cancelToURL, QuestionnaireServiceFacadeLocator serviceLocator) {
       this.event = event;
       this.source = source;
       this.joinFlowAt = joinFlowAt;
       this.cancelToURL = cancelToURL;
       this.serviceLocator = serviceLocator;
    }

    public ActionForward fetchAppliedQuestions(ActionMapping mapping, QuestionResponseCapturer form, HttpServletRequest request,
                                               ActionForwards defaultForward) {
        if (CollectionUtils.isEmpty(form.getQuestionGroups())) {
            List<QuestionGroupDetail> questionGroups = getQuestionGroups(request);
            if (CollectionUtils.isEmpty(questionGroups)) {
                //NOTE: this is done, because the ActionForm is stored in session and when recycled still has the previous questionGroup lists!
                form.setQuestionGroups(null);
                return mapping.findForward(defaultForward.toString());
            }
            /**
             * TODO if user goes back before "joinFlowAt" and continues, then Question group capture form will be reinitialized!
             * We can use the flowkey to check need for reset. consider modifying QuestionResponseCapturerForm set flowkey and
             * then compare the request attribute's flowkey to check! Unfortunately, usage of flowkey may not be consistent!
             * Another situation, although very unlikely, is admin associates another question group with event and source in the meantime!
             * In that case, the questionGroups need to be merged!
             */
            form.setQuestionGroups(questionGroups);
        }
        setQuestionnaireAttributesToRequest(request, form);
        return mapping.findForward(ActionForwards.captureQuestionResponses.toString());
    }

    public ActionErrors validateResponses(HttpServletRequest request, QuestionResponseCapturer form) {
        List<QuestionGroupDetail> groups = form.getQuestionGroups();
        QuestionnaireServiceFacade questionnaireServiceFacade = serviceLocator.getService(request);
        if ((groups == null) || (questionnaireServiceFacade == null)) {
            return null;
        }
        ActionErrors errors = new ActionErrors();
        try {
            questionnaireServiceFacade.validateResponses(groups);
        } catch (ValidationException e) {
            if (e.containsChildExceptions()) {
                for (ValidationException ve : e.getChildExceptions()) {
                   if (ve instanceof MandatoryAnswerNotFoundException) {
                       errors.add(ClientConstants.ERROR_REQUIRED, new ActionMessage(ClientConstants.ERROR_REQUIRED, ve.getQuestionTitle()));
                   }
                   else if (ve instanceof BadNumericResponseException) {
                       populateNumericError((BadNumericResponseException) ve, errors);
                   }
                }
            }
        }
        setQuestionnaireAttributesToRequest(request, form);
        return errors;
    }

    private void populateNumericError(BadNumericResponseException exception, ActionErrors actionErrors) {
        String title = exception.getQuestionTitle();
        Integer allowedMinValue = exception.getAllowedMinValue();
        Integer allowedMaxValue = exception.getAllowedMaxValue();
        if (exception.areMinMaxBoundsPresent()) {
            ActionMessage actionMessage = new ActionMessage(ClientConstants.INVALID_NUMERIC_RANGE_RESPONSE, allowedMinValue, allowedMaxValue, title);
            actionErrors.add(ClientConstants.INVALID_NUMERIC_RANGE_RESPONSE, actionMessage);
        } else if (exception.isMinBoundPresent()) {
            ActionMessage actionMessage = new ActionMessage(ClientConstants.INVALID_NUMERIC_MIN_RESPONSE, allowedMinValue, title);
            actionErrors.add(ClientConstants.INVALID_NUMERIC_MIN_RESPONSE, actionMessage);
        } else if (exception.isMaxBoundPresent()) {
            ActionMessage actionMessage = new ActionMessage(ClientConstants.INVALID_NUMERIC_MAX_RESPONSE, allowedMaxValue, title);
            actionErrors.add(ClientConstants.INVALID_NUMERIC_MAX_RESPONSE, actionMessage);
        } else {
            ActionMessage actionMessage = new ActionMessage(ClientConstants.INVALID_NUMERIC_RESPONSE, title);
            actionErrors.add(ClientConstants.INVALID_NUMERIC_RESPONSE, actionMessage);
        }
    }

    public void saveResponses(HttpServletRequest request, QuestionResponseCapturer form, int associateWithId) {
        List<QuestionGroupDetail> questionResponses = form.getQuestionGroups();
        if (CollectionUtils.isNotEmpty(questionResponses)) {
            QuestionnaireServiceFacade questionnaireServiceFacade = serviceLocator.getService(request);
            //MifosUser loggedinUser = ((MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            if (questionnaireServiceFacade != null) {
                UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
                questionnaireServiceFacade.saveResponses(
                        new QuestionGroupDetails(userContext.getId(), associateWithId, questionResponses));
            }
        }
    }

    public ActionForward rejoinFlow(ActionMapping mapping) {
        return mapping.findForward(joinFlowAt.toString());
    }

    private Object getContextUri(HttpServletRequest request) {
        return request.getRequestURI().replace(request.getContextPath(), "");
    }

    private List<QuestionGroupDetail> getQuestionGroups(HttpServletRequest request) {
        QuestionnaireServiceFacade questionnaireServiceFacade = serviceLocator.getService(request);
        return questionnaireServiceFacade != null ? questionnaireServiceFacade.getQuestionGroups(event, source) : null;
    }

    private void setQuestionnaireAttributesToRequest(HttpServletRequest request, QuestionResponseCapturer form) {
        request.setAttribute("questionsHostForm", form);
        request.setAttribute("origFlowRequestURI", getContextUri(request));
        request.setAttribute("cancelToURL", cancelToURL);
    }

    public ActionForward editResponses(ActionMapping mapping, HttpServletRequest request, QuestionResponseCapturer form) {
        setQuestionnaireAttributesToRequest(request, form);
        return mapping.findForward(ActionForwards.captureQuestionResponses.toString());
    }

}
