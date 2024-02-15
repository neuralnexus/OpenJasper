<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script id="importMainTemplate" type="text/mustache">
<form id="fileUploadForm" method="post" enctype="multipart/form-data" action="rest_v2/import">
    <fieldset id="importDataFile" class="group">
        <legend class="">
            <span><spring:message code="import.file.name"/></span>
        </legend>
        <label for="uploadFile">
            <input name="file" id="uploadFile" type="file" />
            <span class="message warning" id="fileMessage"><spring:message code="import.select.import.file"/></span>
        </label>
    </fieldset>
    <fieldset id="importOptions" class="group">
        <legend class="">
            <span><spring:message code="import.options"/></span>
        </legend>
        <ul class="list inputSet">
            <li class="leaf">
                <div class="control checkBox">
                    <label class="wrap" title="" for=""><spring:message code="import.update"/></label>
                    <input id="update" class="" type="checkbox" value="" name="update" {{#update}}checked="checked"{{/update}}>
                </div>
                <ul class="list inputSet">
                    <li class="leaf">
                        <div class="control checkBox">
                            <label class="wrap" title="" for=""><spring:message code="import.skip.user.update"/></label>
                            <input id="skipUserUpdate" class="" type="checkbox" value="" name="skip-user-update" {{#skipUserUpdate}}checked="checked"{{/skipUserUpdate}}>
                        </div>
                    </li>
                </ul>
            </li>
            <li class="leaf">
                <div class="control checkBox">
                    <label class="wrap" title="" for=""><spring:message code="export.include.access.events"/></label>
                    <input id="includeAccessEvents" class="" type="checkbox" value="" name="include-access-events" {{#includeAccessEvents}}checked="checked"{{/includeAccessEvents}} >
                </div>
            </li>
            <li class="leaf">
                <div class="control checkBox">
                    <label class="wrap" for=""><spring:message code="import.include.configuration.settings"/></label>
                    <input id="" class="" type="checkbox" value="" name="include-server-settings" {{#includeConfigurationSettings}}checked="checked"{{/includeConfigurationSettings}}>
                </div>
            </li>
        </ul>
    </fieldset>
</form>
</script>

<script id="importFooterTemplate" type="text/mustache">
<fieldset id="controlButtons">
    <button id="importButton" class="button action primary up" disabled="disabled">
        <span class="wrap"><spring:message code="import.import"/></span>
        <span class="icon"></span>
    </button>
</fieldset>
</script>
