package ciknowmgr.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ciknowmgr.util.Beans;
import ciknowmgr.util.Constants;


public class DummyFilter implements Filter {
    private static Log log = LogFactory.getLog(DummyFilter.class);
    private boolean baseURLExtracted = false;
    private ServletContext context;

    public void init(FilterConfig filterConfig) throws ServletException {
        context = filterConfig.getServletContext();
        Beans.init(context);
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (!baseURLExtracted) {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            
            StringBuilder sb = new StringBuilder();
            sb.append(request.getScheme());
            sb.append("://");
            sb.append(request.getServerName());
            if (request.getServerPort() != 80) {
            	sb.append(":");
            	sb.append(request.getServerPort());
            }
            sb.append(request.getContextPath());
            String baseUrl = sb.toString();
            context.setAttribute(Constants.APP_BASE_URL, baseUrl);
            log.info("Base URL: " + baseUrl);
            
            String realPath = context.getRealPath("/");
            context.setAttribute(Constants.APP_REAL_PATH, realPath);
            log.info("Real Path: " + realPath);

            baseURLExtracted = true;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {

    }
}
