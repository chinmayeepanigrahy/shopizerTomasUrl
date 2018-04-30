package com.salesmanager.admin.controller.login;

import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.salesmanager.admin.components.security.AdminAuthenticationToken;
import com.salesmanager.admin.model.common.Messages;
import com.salesmanager.admin.model.common.ResponseToJson;
import com.salesmanager.admin.model.user.LogonUser;
import com.salesmanager.admin.utils.Constants;

@Controller
public class LoginController {
	
	
	@Inject
	private AuthenticationManager authenticationManager;
	
	@Inject
	private Messages messages;
	
	@RequestMapping("/login")
	public String display(HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		return "login/login";
	}
	
	@RequestMapping(value="/login/process", method = RequestMethod.POST)
	public ResponseEntity<String> login(@Valid @RequestBody LogonUser user, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		
		
	    if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(ResponseToJson.toObject(messages.get("message.credentials.required", locale), Constants.Code.VALIDATION_ERROR));
        }
	    

		try {

	        // Perform the security
	    	Authentication authentication = null;
	    	try {
	    		
	    		authentication = authenticationManager.authenticate(
	    				new AdminAuthenticationToken(
	    			            user.getUserName(),
	    			            user.getPassword()
	    			    )
	    		 );
	    		
	    	} catch(Exception e) {
	    		return ResponseEntity.badRequest().body(ResponseToJson.toObject(messages.get("error", locale), Constants.Code.ERROR));
	    	}
	    	
	    	if(authentication == null) {
	    		return ResponseEntity.badRequest().body(ResponseToJson.toObject(messages.get("authentication.failed",locale),Constants.Code.ERROR));
	    	}
	    	
	    	SecurityContextHolder.getContext().setAuthentication(authentication);
	    	
	    	/**
	    	 * Get token for further usage
	    	 */
	    	

	    	
	    	return ResponseEntity.ok().body("{}");

			
		} catch (Exception e) {
			//LOGGER.error("Error while login user",e);
			try {
				response.sendError(503, "Error while login user " + e.getMessage());
			} catch (Exception ignore) {
			}
			
			return null;
		}
	}

}
