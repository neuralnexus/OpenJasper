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

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/spring" prefix="spring" %>

<% request.setAttribute("id", ((String) request.getAttribute("name")).replace(".", "_"));%>

<input type="text" name="${name}" id="${id}" value="${value}"
       onmousedown="cancelEventBubbling(event)"
       <c:if test="${not empty onchange}">onchange="${onchange}"</c:if>
       <c:if test="${readOnly}">disabled="disabled"</c:if>
        />
<c:if test="${not readOnly}">

    <script type="text/javascript">
        jQuery('#${id}').${(hasDate == true) ? 'date' : ''}${(hasTime == true) ? 'time' : ''}picker({
            dateFormat:'${datePattern}',
            timeFormat:'${timePattern}',
            showSecond:'${showSecond}',
            showOn:"button",
            buttonText:"",
            changeYear:true,
            changeMonth:true,
            showButtonPanel:true,
            onChangeMonthYear:null,
            beforeShow:jQuery.datepicker.movePickerRelativelyToTriggerIcon
        }).next().addClass('button').addClass('picker');
        // Prototype.js compatibility
        jQuery('#${id}')[0].getValue = function () {
            return jQuery(this).val()
        }
    </script>
</c:if>
