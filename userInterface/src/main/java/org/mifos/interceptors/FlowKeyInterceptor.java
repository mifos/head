package org.mifos.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Temporary class used to maintain compatibility spring/ftl with struts/jsp.
 * Along with this class, refactoring of project structure was needed. 
 * Five classes are temporarily moved from mifos-appdomain to appropriate packages in mifos-common, 
 * due to inability to access them in module mifos-appdomain from module mifos-userinterface.
 * Moved classes:
 * {@link FlowManager}
 * {@link Flow}
 * {@link QueryResult}
 * {@link QueryInputs}
 * {@link PageExpiredException}
 * See {@link BaseAction#createToken} and {@link BaseAction#joinToken} 
 */
public class FlowKeyInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws PageExpiredException {
        String flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);
        if (flowKey == null) {
            createToken(request);
        } else {
            joinToken(request);
        }

		return true;
	}

    private void createToken(HttpServletRequest request) {
        String flowKey = String.valueOf(new DateTimeService().getCurrentDateTime().getMillis());
        FlowManager flowManager = (FlowManager) request.getSession().getAttribute(Constants.FLOWMANAGER);
        if (flowManager == null) {
            flowManager = new FlowManager();
            request.getSession(false).setAttribute(Constants.FLOWMANAGER, flowManager);
        }
        flowManager.addFLow(flowKey, new Flow(), this.getClass().getName());
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);

    }

    private void joinToken(HttpServletRequest request) throws PageExpiredException {
        String flowKey = request.getParameter(Constants.CURRENTFLOWKEY);
        if(null == flowKey){
            flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);
        }
        FlowManager flowManager = (FlowManager) request.getSession().getAttribute(Constants.FLOWMANAGER);
        if (flowKey == null || !flowManager.isFlowValid(flowKey)) {
            throw new PageExpiredException("no flow for key " + flowKey);
        }

    }
	
}
