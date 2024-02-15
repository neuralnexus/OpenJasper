<%--
  ~ Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
  ~ http://www.jaspersoft.com.
  ~
  ~ Unless you have purchased  a commercial license agreement from Jaspersoft,
  ~ the following license terms  apply:
  ~
  ~ This program is free software: you can redistribute it and/or  modify
  ~ it under the terms of the GNU Affero General Public License  as
  ~ published by the Free Software Foundation, either version 3 of  the
  ~ License, or (at your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  ~ GNU Affero  General Public License for more details.
  ~
  ~ You should have received a copy of the GNU Affero General Public  License
  ~ along with this program. If not, see <http://www.gnu.org/licenses/>.
  --%>

<%--
Overview:
    Login dialog. Used only on log in page.

Usage:
    <t:insertTemplate template="/WEB-INF/jsp/templates/login.jsp">
        <t:putAttribute name="jsEdition" value="[REQUIRED]"/>
        <t:putAttribute name="allowUserPasswordChange" value="[REQUIRED]"/>
        <t:putAttribute name="errorMessages">
            [OPTIONAL]
        </t:putAttribute>
        <t:putAttribute name="localeOptions">
            [REQUIRED]
        </t:putAttribute>
        <t:putAttribute name="timezoneOptions">
            [REQUIRED]
        </t:putAttribute>
    </t:insertTemplate>
--%>

<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib uri="/spring" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!--/WEB-INF/jsp/templates/login.jsp revision A-->
<t:useAttribute name="jsEdition" id="jsEdition" classname="java.lang.String" ignore="false"/>
<t:useAttribute name="allowUserPasswordChange" id="allowUserPasswordChange" classname="java.lang.String" ignore="false"/>
<t:useAttribute name="showPasswordChange" id="showPasswordChange" classname="java.lang.String" ignore="true"/>
<t:useAttribute name="warningMessages" id="warningMessages" classname="java.lang.String" ignore="true"/>
<t:useAttribute name="errorMessages" id="errorMessages" classname="java.lang.String" ignore="true"/>
<t:useAttribute name="localeOptions" id="localeOptions" classname="java.lang.String" ignore="false"/>
<t:useAttribute name="timezoneOptions" id="timezoneOptions" classname="java.lang.String" ignore="false"/>

<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerClass">panel dialog inlay login ${containerClass}</t:putAttribute>
    <t:putAttribute name="containerID" value="login"/>
    <t:putAttribute name="containerTitle"><spring:message code='jsp.Login.title'/></t:putAttribute>
    <t:putAttribute name="bodyContent">

        ${warningMessages}
        ${errorMessages}

        <fieldset>
            <legend class="offLeft"><span><spring:message code='jsp.Login.section'/></span></legend>
            <c:if test="${jsEdition != 'community'}">
                <label class="control input text hidden" accesskey="o" for="orgId">
                    <span class="wrap"></span><spring:message code='MT_ORGANIZATION'/>:</span>
                    <input id="orgId" name="orgId" type="text" autocapitalize="off"/>
                    <span class="message warning">error message here</span>
                </label>
            </c:if>
            <label class="control input text" for="j_username">
                <span class="wrap"><spring:message code='jsp.Login.username'/>:</span>
                <input id="j_username" name="j_username" type="text" autocapitalize="off" autofocus="autofocus"/>
                <span class="message warning">error message here</span>
            </label>
            <label class="control input password" for="j_password">
                <span class="wrap">
                    <c:choose>
                        <c:when test="${showPasswordChange eq 'true'}"><spring:message code='jsp.Login.password.old'/>:</c:when>
                        <c:otherwise><spring:message code='jsp.Login.password'/>:</c:otherwise>
                    </c:choose>
                </span>
                <input class="" id="j_password_pseudo" name="j_password_pseudo" type="password" maxlength="47"/>
                <input class="" id="j_password" name="j_password" type="hidden"/>
                <span class="message warning">error message here</span>
            </label>
        </fieldset>
        <fieldset>
            <legend><a id="showHideLocaleAndTimezone"><spring:message code="jsp.Login.link.showLocale"/></a></legend>
            <div id="localeAndTimeZone" class="hidden">
	            <label class="control select" for="userLocale">
	            	<span class="wrap"><spring:message code='jsp.Login.locale'/>:</span>
	                <select id="userLocale" name="userLocale">
	                    ${localeOptions}
	                </select>
	            </label>
	            <label class="control select" for="userTimezone"><spring:message code='jsp.Login.timezone'/>:</span>
	                <select id="userTimezone" name="userTimezone">
	                    ${timezoneOptions}
	                </select>
	            </label>
            </div>
        </fieldset>
        <c:if test="${allowUserPasswordChange eq 'true'}">
            <script type="text/javascript" language="JavaScript">
                var doesAllowUserPasswordChange = true;
            </script>
            <fieldset>
                <legend><a id="showHideChangePassword">
                    <c:choose>
                        <c:when test="${showPasswordChange eq 'true'}"><spring:message code="jsp.Login.link.cancelPassword"/></c:when>
                        <c:otherwise><spring:message code="jsp.Login.link.changePassword"/></c:otherwise>
                    </c:choose>
                </a></legend>
                <div id="changePassword" <c:if test="${not showPasswordChange eq 'true'}">class="hidden"</c:if>>
	                <label class="control input password" for="j_newpassword1">
	                	<span class="wrap"><spring:message code='jsp.Login.link.newPassaowrd'/>:</span>
	                    <input id="j_newpassword1_pseudo" name="j_newpassword1_pseudo" type="password"/>
	                    <input id="j_newpassword1" name="j_newpassword1" type="hidden"/>
	                    <span class="message warning">error message here</span>
	                </label>
	                <label class="control input password" for="j_newpassword2">
	                	<span class="wrap"><spring:message code='jsp.Login.link.repeatNewPassword'/>:</span>
	                    <input id="j_newpassword2_pseudo" name="j_newpassword2_pseudo" type="password"/>
	                    <input id="j_newpassword2" name="j_newpassword2" type="hidden"/>
	                    <span class="message warning">error message here</span>
	                </label>
	                <input type="hidden" name="passwordExpiredDays"/>
                </div>
            </fieldset>
        </c:if>
    </t:putAttribute>
    <t:putAttribute name="footerContent">
    	<button type="button" id="submitButton" class="button action primary up" disabled="disabled">
    		<span class="wrap"><spring:message code='jsp.Login.button.login'/></span>
    		<span class="icon"></span>
    	</button>
    	<h2><a id="needHelp"><spring:message code='LOGIN_NEED_HELP_LINK'/></a></h2>
        <!--
        <div class="cosmetic left"></div>
        <div class="cosmetic right"></div>
        -->
    </t:putAttribute>
</t:insertTemplate>
