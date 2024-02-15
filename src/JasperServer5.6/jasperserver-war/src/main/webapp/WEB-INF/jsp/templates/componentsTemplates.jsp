<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<script id="componentsNotificationTemplate" type="text/mustache">
    <div class="notification">
        <span class="message warning">{{message}}</span>
    </div>
</script>

<script id="ajaxUploadTemplate" type="text/mustache">
    <iframe class="hidden" name="{{name}}"></iframe>
</script>
