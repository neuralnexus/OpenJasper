<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>

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

<script id="bool" type="template/mustache">
    <div id="{{-id}}" class="leaf">
        <js:xssNonce/>
        <label class="control input checkBox" for="{{-uuid}}" title="{{-description}}">
            <span class="wrap">{{ if (mandatory) { }}* {{ } }}{{-label}}</span>
            <input  type="checkbox" {{if (readOnly) { }}disabled{{ } }} />
        </label>
    </div>
</script>

<script id="singleValueText" type="template/mustache">
    <div id="{{-id}}" class="leaf">
        <js:xssNonce/>
        <label class="control input text"  title="{{-description}}">
            <span class="wrap">{{if (mandatory) { }}* {{ } }}{{-label}}</span>
            <input class=""  type="text" value="" {{if (readOnly) { }}disabled{{ } }} />
            <span class="warning">{{-message}}</span>
        </label>
    </div>
</script>

<script id="singleValueNumber" type="template/mustache">
    <div id="{{-id}}" class="leaf">
        <js:xssNonce/>
        <label class="control input text"  title="{{-description}}">
            <span class="wrap">{{if (mandatory) { }}* {{ } }}{{-label}}</span>
            <input class=""  type="text" value="" {{if (readOnly) { }}disabled{{ } }} />
            <span class="warning">{{-message}}</span>
        </label>
    </div>
</script>

<script id="singleValueDate" type="template/mustache">
    <div id="{{-id}}" class="leaf">
        <js:xssNonce/>
        <label class="control picker"
               title="{{-label}}. &#10;<spring:message code="DATE_IC_TOOLTIP" javaScriptEscape="true"/>">
            <span class="wrap">{{if (mandatory) { }}* {{ } }}{{-label}}</span>
            <input class="date"  type="text" value="" {{if (readOnly) { }}disabled{{ } }} />
            <div class="warning">{{-message}}</div>
        </label>
    </div>
</script>

<script id="singleValueDatetime" type="template/mustache">
    <div id="{{-id}}" class="leaf">
        <js:xssNonce/>
        <label class="control picker"
               title="{{-label}}. &#10;<spring:message code="DATE_IC_TOOLTIP" javaScriptEscape="true"/>">
            <span class="wrap">{{if (mandatory) { }}* {{ } }}{{-label}}</span>
            <input class="date" type="text" value="" {{if (readOnly) { }}disabled{{ } }} />
            <div class="warning">{{-message}}</div>
        </label>
    </div>
</script>

<script id="singleValueTime" type="template/mustache">
    <div id="{{-id}}" class="leaf">
        <js:xssNonce/>
        <label class="control picker"
               title="{{-label}}. &#10;<spring:message code="DATE_IC_TOOLTIP" javaScriptEscape="true"/>">
            <span class="wrap">{{if (mandatory) { }}* {{ } }}{{-label}}</span>
            <input class="date" type="text" value="" {{if (readOnly) { }}disabled{{ } }} />
            <div class="warning">{{-message}}</div>
        </label>
    </div>
</script>

<script id="singleSelect" type="template/mustache">
    <div id="{{-id}}" class="leaf visibleOverflow">
        <js:xssNonce/>
        <label class="control select"  title="{{-description}}">
            <span class="wrap">{{if (mandatory) { }}* {{ } }}{{-label}}</span>
            <div class="ssPlaceholder jr jr-mInput {{if (readOnly) { }} jr-isDisabled{{ } }}" {{if (readOnly) { }}disabled="disabled"{{ } }}></div>
            <span class="warning">{{-message}}</span>
        </label>
    </div>
</script>

<script id="multiSelect" type="template/mustache">
    <div class="leaf visibleOverflow" id="{{-id}}">
        <js:xssNonce/>
        <div class="control select multiple"  title="{{-description}}">
            <span class="wrap">{{if (mandatory) { }}* {{ } }}{{-label}}</span>
            <div class="msPlaceholder jr {{if (readOnly) { }} jr-mInput jr-isDisabled {{ } }}" {{if (readOnly) { }}disabled="disabled"{{ } }}></div>
            <span class="warning">{{-message}}</span>
            <div class="resizeOverlay hidden"></div>
        </div>
    </div>
</script>

<script id="singleSelectRadio" type="template/mustache">
    <div id="{{-id}}" class="leaf visibleOverflow">
        <js:xssNonce/>
        <fieldset class="control select multiple radio" title="{{-description}}">
            <legend>{{if (mandatory) { }}* {{ } }}{{-label}}</legend>
            <ul class="list inputSet">
                <!--#data-->
                {{ if (data) { }}
                {{ for (var i = 0; i < data.length; i++) { }}
                <li>
                    <div class="control radio">
                        <input {{ if (data[i].selected) { }}checked="checked"{{ } }} class="" id="{{-data[i].uuid}}" type="radio" name="{{-data[i].name}}" {{if (data[i].readOnly) { }}disabled="disabled"{{ } }} value="{{-data[i].value}}"/>
                        <label class="wrap" for="{{-data[i].uuid}}" title="{{-data[i].description}}">
                            {{-data[i].label}}&nbsp;
                        </label>
                    </div>
                </li>
                {{ } }}
                {{ } }}
                <!--/data-->
            </ul>
        </fieldset>
        <span class="warning">{{-message}}</span>
        <div class="resizeOverlay hidden"></div>
        <div class="jr-mSizer jr-mSizerVertical jr hidden"><span class="ui-icon ui-icon-grip-solid-horizontal"></span></div>
    </div>
</script>

<script id="multiSelectCheckbox" type="template/mustache">
    <div id="{{-id}}" class="leaf visibleOverflow">
        <js:xssNonce/>
        <fieldset class="control select checkbox" title="{{-description}}">
            <legend>{{if (mandatory) { }}* {{ } }}{{-label}}</legend>
            <ul class="list inputSet">
                <!--#data-->
                {{ if (data) { }}
                {{ for (var i = 0; i < data.length; i++) { }}
                    <li>
                        <div class="control checkBox">
                            <label class="wrap" for="{{-data[i].uuid}}" title="{{-data[i].description}}">
                                {{-data[i].label}}&nbsp;
                            </label>
                            <input {{ if (data[i].selected) { }}checked="checked"{{ } }} class="" id="{{-data[i].uuid}}" type="checkbox" {{if (data[i].readOnly) { }}disabled="disabled"{{ } }} value="{{-data[i].value}}"/>
                        </div>
                    </li>
                {{ } }}
                {{ } }}
                <!--/data-->
            </ul>
	        <div class="{{if (readOnly) { }}hidden{{ } }}">
                <a href="#" name="multiSelectAll"><spring:message code="button.select.all" javaScriptEscape="true"/></a>
                <a href="#" name="multiSelectNone"><spring:message code="button.select.none" javaScriptEscape="true"/></a>
                <a href="#" name="multiSelectInverse"><spring:message code="button.select.inverse" javaScriptEscape="true"/></a>
            </div>
        </fieldset>
        <span class="warning">{{-message}}</span>
        <div class="resizeOverlay hidden"></div>
        <div class="jr-mSizer jr-mSizerVertical jr hidden"><span class="ui-icon ui-icon-grip-solid-horizontal"></span></div>
    </div>
</script>

<script id="reportOptions" type="template/mustache">
    <div>
        <js:xssNonce/>
        <label class="control select" for="reportOptionsSelect" title="{{-title}}">
            <span class="wrap">{{-label}}</span>
            <select id="reportOptionsSelect">
                <!--#data-->
                {{ if (data) { }}
                {{ for (var i = 0; i < data.length; i++) { }}
                    <option value="{{-data[i].id}}" {{ if (data[i].selected) { }}selected="selected"{{ } }}>{{-data[i].label}}</option>
                {{ } }}
                {{ } }}
                <!--/data-->
            </select>
        </label>
    </div>
</script>

<!-- ${sessionScope.XSS_NONCE} do not remove -->




