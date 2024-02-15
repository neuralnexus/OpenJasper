/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.war;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.util.LocaleHelper;
import com.jaspersoft.jasperserver.api.common.util.TimeZoneContextHolder;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.MetadataUserDetails;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.HttpOnlyResponseWrapper;
import com.jaspersoft.jasperserver.api.security.encryption.EncryptionRequestUtils;
import com.jaspersoft.jasperserver.war.common.JasperServerConstImpl;
import com.jaspersoft.jasperserver.war.common.JasperServerHttpConstants;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id: UserPreferencesFilter.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class UserPreferencesFilter implements Filter
{
	private static String USER_LOCALE_PARAM = "userLocale";
	private static String USER_TIMEZONE_PARAM = "userTimezone";
	private static String USER_NAME = "j_username";
	private static String USER_PASSWORD = "j_newpassword1";

    private int cookieAge;
	UserAuthorityService userService;

	public UserAuthorityService getUserService()
	{
		return userService;
	}

	public void setUserService(UserAuthorityService userService)
	{
		this.userService = userService;
	}


	public void init(FilterConfig config) throws ServletException
	{
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpSession session = httpRequest.getSession();

        String userLocale = request.getParameter(USER_LOCALE_PARAM)!=null ? request.getParameter(USER_LOCALE_PARAM) : LocaleContextHolder.getLocale().toString();

        boolean updateTimezoneInSession = false;
        String userTimezone = null;

        if (request instanceof HttpServletRequest){
            userTimezone = ((HttpServletRequest)request).getHeader(JasperServerHttpConstants.HEADER_ACCEPT_TIMEZONE);
        }

        if (userTimezone == null){
            userTimezone = request.getParameter(USER_TIMEZONE_PARAM);
            // compatibility with previous implementation
            updateTimezoneInSession = true;
        }

        HttpOnlyResponseWrapper httpOnlyResponseWrapper = new HttpOnlyResponseWrapper((HttpServletResponse)response);

		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			session.removeAttribute("js_uname");
			session.removeAttribute("js_upassword");
		}

        Locale sessionLocale = (Locale) session.getAttribute(
					JasperServerConstImpl.getUserLocaleSessionAttr());

		if (userLocale != null && userLocale.length() > 0 ) {
			Locale locale = LocaleHelper.getInstance().getLocale(userLocale);
			if (sessionLocale == null || !sessionLocale.equals(locale)) {
				session.setAttribute(JasperServerConstImpl.getUserLocaleSessionAttr(), locale);
				Cookie cookie = new Cookie(JasperServerConstImpl.getUserLocaleSessionAttr(), userLocale);
				cookie.setMaxAge(cookieAge);
                httpOnlyResponseWrapper.addCookie(cookie);
			}
		}

        if (sessionLocale != null) {
            LocaleContextHolder.setLocale(sessionLocale);
        }

		if (userTimezone != null && userTimezone.length() > 0) {
			String sessionTimezone = (String) session.getAttribute(
					JasperServerConstImpl.getUserTimezoneSessionAttr());
			if (sessionTimezone == null || !sessionTimezone.equals(userTimezone)) {
                // compatibility with previous implementation
                if (updateTimezoneInSession){
                    session.setAttribute(JasperServerConstImpl.getUserTimezoneSessionAttr(), userTimezone);
                    Cookie cookie = new Cookie(JasperServerConstImpl.getUserTimezoneSessionAttr(), userTimezone);
                    cookie.setMaxAge(cookieAge);
                    httpOnlyResponseWrapper.addCookie(cookie);
                }
                TimeZoneContextHolder.setTimeZone(TimeZone.getTimeZone(userTimezone));
			}
		} else {
            userTimezone = (String)session.getAttribute(JasperServerConstImpl.getUserTimezoneSessionAttr());
            if (userTimezone != null && userTimezone.length() > 0) {
                TimeZoneContextHolder.setTimeZone(TimeZone.getTimeZone(userTimezone));
            } else {
                TimeZoneContextHolder.setTimeZone(TimeZone.getDefault());
            }
        }

		String userName = EncryptionRequestUtils.getValue(httpRequest, USER_NAME);
		String userNewPassword = EncryptionRequestUtils.getValue(httpRequest, USER_PASSWORD);
		String passwordExpiredDays = request.getParameter("passwordExpiredDays");

		String testFilter = (String)session.getAttribute("js_uname");

		if (testFilter == null) {

           if (userName != null) {
        	  session.setAttribute("js_uname", userName);
           }
           if (userNewPassword != null) {
        	  session.setAttribute("js_upassword", userNewPassword);
           }
           if (passwordExpiredDays != null) {
        	  session.setAttribute("passwordExpiredDays", passwordExpiredDays);
           }
		} else {
		   userName = (String)session.getAttribute("js_uname");
		   userNewPassword = (String)session.getAttribute("js_upassword");
           //this part of if condition will be reached after second calling of
           //userpreferences filter namely after authenticationProcessingFilter filter
           //so now we must have not null authentication in security context
           Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		   if (userNewPassword != null) {
		      if (!("".equals(userNewPassword.trim()))) {
			  	 User user = getUser(null, auth);
				 user.setPassword(userNewPassword);
				 // reset password timer
				 user.setPreviousPasswordChangeTime(new Date());
                 session.removeAttribute("js_uname");
                 session.removeAttribute("js_upassword");
                 session.removeAttribute("passwordExpiredDays");
				 userService.putUser(null, user);
				 chain.doFilter(request, response);
				 return;
		      }
		   }
		   session.removeAttribute("js_uname");
		   session.removeAttribute("js_upassword");
           // check if password expired, if so, log off user and go back to login page and show the password change UI
		   String nDate = (String)session.getAttribute("passwordExpiredDays");
		   if (nDate != null) {
			  int totalDate = 0;
			  try {
				 totalDate = Integer.parseInt(nDate);
			  } catch (NumberFormatException e) {
				 // do nothing, then 0
			  }
			  if (totalDate > 0) {
		         if (isPasswordExpired(null, auth, totalDate)) {
			        // log user off and show password change UI
                   RequestDispatcher rd = request.getRequestDispatcher("/exituser.html?showPasswordChange=true");
                   rd.forward(request, response);
                   return;
		         }
			  }
		   }
		   session.removeAttribute("passwordExpiredDays");

		}
		chain.doFilter(request, response);
	}

    protected boolean isPasswordExpired(ExecutionContext context, Authentication auth, int nDays) {
		if (auth == null || !auth.isAuthenticated())
			return false;

		// external user's password does not expire in JRS
		Object principal = auth.getPrincipal();
		if (principal instanceof MetadataUserDetails && ((MetadataUserDetails) principal).isExternallyDefined())
			return false;
        return userService.isPasswordExpired(context, auth.getName(), nDays);
    }

    protected User getUser(ExecutionContext context, Authentication auth) {
        return userService.getUser(context, auth.getName());
    }

	public void destroy()
	{
	}

	public int getCookieAge()
	{
		return cookieAge;
	}

	public void setCookieAge(int cookieAge)
	{
		this.cookieAge = cookieAge;
	}
}
