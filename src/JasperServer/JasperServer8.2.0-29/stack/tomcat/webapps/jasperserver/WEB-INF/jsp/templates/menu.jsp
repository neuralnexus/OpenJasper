<%@ page contentType="text/html; charset=utf-8" %>
<%--
  ~ Copyright (C) 2005-2023. Cloud Software Group, Inc. All Rights Reserved.
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

<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>

<%--main menu--%>
<div id='menu' class="menu vertical context hidden" role="none"></div>

<div id="commonMenu" class="hidden" role="none">
    <div class="content" role="none">
        <ul id="menuList_template" role="menu">
        </ul>
        <js:xssNonce/>
    </div>
</div>

<ul class="hidden" aria-hidden="true" role="menubar">
    <%--simply action list option--%>
    <li id="menuList_simpleAction" class="leaf" role="none"><p role="menuitem" class="wrap button"><span class="icon"></span><!--Item text goes here--></p></li>

    <%--separator for menu--%>
    <li id="menuList_separator" class="leaf separator" role="none"></li>

    <%--submenu list--%>
    <li id="menuList_flyout" class="node" role="none"><p role="menuitem" class="wrap button"><span class="icon"></span><!--Item text goes here--></p></li>

    <%--extra pop up menu--%>
    <li id="menuList_extraInput" class="leaf" role="none"><p role="menuitem" class="wrap button"><span class="icon"></span><!--Item text goes here--></p></li>

    <%--dropdown list element--%>
    <li id="menuList_listItem" class="leaf" role="none"><p role="menuitem" class="wrap toggle button"><span class="icon"></span><!--Item text goes here--></p></li>

    <%--main navigation mutton--%>
    <li id="navigation_mutton" class="node mutton" role="none"><p role="menuitem" class="wrap button"><span class="icon"></span></p></li>
</ul>