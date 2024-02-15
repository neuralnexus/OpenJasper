<script id="attributesMain" type="template/mustache">
    <div id="noAttributes"><p class="message"><spring:message code="jsp.attributes.no.attributes"/></p></div>

   <div class="attributesTable">
    <ol class="list tabular">
        <li class="leaf">
            <div class="wrap header">
                <div class="column attrName">
                    <spring:message code="jsp.attributes.attribute.name"/>
                </div>
                <div class="column attrValue">
                    <spring:message code="jsp.attributes.attribute.value"/>
                </div>
                <div class="column attrAction">

                </div>
            </div>
        </li>
      </ol>
      <ol class="list tabular items"> </ol>
      <ol class="list tabular">
        <li id="newAttribute" class="leaf">
            <div class="wrap">
                <div class="column attrName textArea">
                    <textarea rows="1"></textarea>
                    <span class="message warning"></span>
                </div>
                <div class="column attrValue textArea">
                    <textarea rows="1"></textarea>
                    <span class="message warning"></span>
                </div>
                <div class="column attrAction">
                    <a class="launcher"><spring:message code="resource.lov.add"/> </a>
                </div>
            </div>
        </li>
    </ol>
 </div>
</script>

<script id="attributesItem" type="template/mustache">
        <div class="wrap">
            <div class="column attrName textArea">
                <textarea rows="1" readonly>{{name}}</textarea>
                <span class="message warning"></span>
            </div>
            <div class="column attrValue textArea">
                <textarea rows="1" readonly>{{value}}</textarea>
                <span class="message warning"></span>
            </div>
            <div class="column attrAction">
                <a class="launcher"><spring:message code="resource.lov.remove"/></a>
            </div>
        </div>
</script>