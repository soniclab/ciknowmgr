package ciknowmgr.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;


public class CIKNOWMGRLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
	
    @Override
    public void onLogoutSuccess(HttpServletRequest request,
            HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        //setDefaultTargetUrl("/login.jsp?logout=1");
        setDefaultTargetUrl("/logout-complete.html");
        super.onLogoutSuccess(request, response, authentication);

        Session session = Sessions.getCurrent();
        if (session != null) {
            session.invalidate();
        }
    }
}
