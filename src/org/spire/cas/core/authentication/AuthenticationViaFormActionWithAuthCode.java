package org.spire.cas.core.authentication;  
  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpSession;  
  
import org.apache.commons.lang3.StringUtils;  
import org.jasig.cas.authentication.Credential;  
import org.jasig.cas.authentication.RootCasException;  
import org.jasig.cas.web.flow.AuthenticationViaFormAction;  
import org.jasig.cas.web.support.WebUtils;  
import org.springframework.binding.message.MessageBuilder;  
import org.springframework.binding.message.MessageContext;  
import org.springframework.webflow.execution.RequestContext;  
  
/** 
 * 验证码校验类 
 *  
 * @author Langyou02 
 * 
 */  
public class AuthenticationViaFormActionWithAuthCode extends  
        AuthenticationViaFormAction {  
    /** 
     * authcode check 
     */  
    public final String validatorCode(final RequestContext context,  
            final Credential credentials, final MessageContext messageContext)  
            throws Exception {  
        final HttpServletRequest request = WebUtils  
                .getHttpServletRequest(context);  
        HttpSession session = request.getSession();  
        String authcode = (String) session  
                .getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);  
        session.removeAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);  
  
        UsernamePasswordCredentialWithAuthCode upc = (UsernamePasswordCredentialWithAuthCode) credentials;  
        String submitAuthcode = upc.getAuthcode();  
        if (StringUtils.isEmpty(submitAuthcode)  
                || StringUtils.isEmpty(authcode)) {  
            populateErrorsInstance(new NullAuthcodeAuthenticationException(),  
                    messageContext);  
            return "error";  
        }  
        if (submitAuthcode.equals(authcode)) {  
            return "success";  
        }  
        populateErrorsInstance(new BadAuthcodeAuthenticationException(),  
                messageContext);  
        return "error";  
    }  
  
    private void populateErrorsInstance(final RootCasException e,  
            final MessageContext messageContext) {  
  
        try {  
            messageContext.addMessage(new MessageBuilder().error()  
                    .code(e.getCode()).defaultText(e.getCode()).build());  
        } catch (final Exception fe) {  
            logger.error(fe.getMessage(), fe);  
        }  
    }  
}  