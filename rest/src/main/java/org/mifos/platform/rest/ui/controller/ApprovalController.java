package org.mifos.platform.rest.ui.controller;

import java.util.HashMap;
import java.util.Map;

import org.mifos.platform.rest.approval.service.RESTCallInterruptException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ApprovalController {


	@ExceptionHandler(RESTCallInterruptException.class)
	public @ResponseBody Map<String, String> callInterruptExceptionHandler() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("status", "interrupt");
		map.put("cause", "The call has been interrupt for approval");
		return map;
	}
}
