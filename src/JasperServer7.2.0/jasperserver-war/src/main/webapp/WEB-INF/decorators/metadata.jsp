<%@ page contentType="text/html; charset=utf-8" %>
<%--
  ~ Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
  ~ http://www.jaspersoft.com.
  ~
  ~ Unless you have purchased a commercial license agreement from Jaspersoft,
  ~ the following license terms apply:
  ~
  ~ This program is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU Affero General Public License as
  ~ published by the Free Software Foundation, either version 3 of the
  ~ License, or (at your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  ~ GNU Affero General Public License for more details.
  ~
  ~ You should have received a copy of the GNU Affero General Public License
  ~ along with this program. If not, see <http://www.gnu.org/licenses/>.
  --%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>
<%@ taglib uri="sitemesh-decorator" prefix="decorator" %>
 <decorator:usePage id="main" />
 <html>
 <head>
 <title>Raz's Site -
    <decorator:title default="<spring:message code='decorators.metadata.welcome'/>" />
 </title>
 <decorator:head />
 </head>

 <body>
 <js:xssNonce/>
 <h1><decorator:title default="<spring:message code='decorators.metadata.welcome'/>" /></h1>
 <h3>
 <a href="mailto:<decorator:getProperty property="meta.author"
            default="staff@example.com" />">
 <decorator:getProperty property="meta.author"
              default="<spring:message code='decorators.metadata.metaAuthor'/>" />
 </a></h3><hr />
 <decorator:body />
 <p><small>
    (<a href="?printable=true">printable version</a>)
    </small>
 </p>
 </body>
 </html>


