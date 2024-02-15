<%@ taglib uri="/spring" prefix="spring"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<t:insertTemplate template="/WEB-INF/jsp/templates/page.jsp">
    <t:putAttribute name="pageTitle" value="Button Gallery"/>
    <t:putAttribute name="bodyID" value="buttons"/>
    <t:putAttribute name="bodyClass" value="twoColumn"/>
    <t:putAttribute name="headerContent" >
        <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code='samples.css'/>" type="text/css" />
        <style type="text/css">
          div#droppable_container {
            height: 140px;
            width: 400px; }
          div#draggable_demo {
            width: 60px;
            height: 60px;
            cursor: move;
            background: #9fcfba;
            border: 1px solid #666;
            text-align: center;
            position: relative;
            top: 30px;
            line-height: 50px; }
          div#droppable_demo {
            width: 160px;
            height: 120px;
            background: #fff;
            border: 5px solid #ccc;
            text-align: center;
            position: relative;
            top: -60px;
            left: 140px;
            line-height: 100px; }
          div#droppable_demo.hover {
            border: 5px dashed #aaa;
            background:#efefef; }
        </style>

        <script type="text/javascript" language="JavaScript" src="${pageContext.request.contextPath}/scripts/lib/yui-3.1.0.js"></script>
        <script type="text/javascript" language="JavaScript" src="${pageContext.request.contextPath}/scripts/lib/yui.console-3.1.0.js"></script>
        <script type="text/javascript" language="JavaScript" src="${pageContext.request.contextPath}/scripts/lib/yui.console-filters-3.1.0.js"></script>
        <script type="text/javascript" language="JavaScript" src="${pageContext.request.contextPath}/scripts/lib/yui.test-3.1.0.js"></script>
    </t:putAttribute>
    <t:putAttribute name="bodyContent" >
	
		<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
		    <t:putAttribute name="containerClass" value="column decorated primary"/>
		    <t:putAttribute name="containerTitle">DnD</t:putAttribute>
		    <t:putAttribute name="bodyClass" value="oneColumn"/>
		    <t:putAttribute name="bodyContent">

                <div class="demo" id="droppable_container">
                    <div id="draggable_demo" class="draggable">
                        Drag me!
                    </div>

                    <div id="droppable_demo">
                        Drop here!
                    </div>
                </div>

                <script type="text/javascript">
                    new Draggable('draggable_demo', {
                        revert: true
                    });

                    Droppables.add('droppable_demo', {
                        accept: 'draggable',
                        hoverclass: 'hover',
                        onDrop: function() {
                            $('droppable_demo').highlight();
                        }
                    });
                </script>


            </t:putAttribute>
		    <t:putAttribute name="footerContent">
		    	<!-- custom content here; remove this comment -->
		    </t:putAttribute>
		</t:insertTemplate>		
		
		<t:insertTemplate template="/WEB-INF/jsp/modules/sample/sampleIndex.jsp"/>

        <script type="text/javascript">
            document.observe('dom:loaded', function() {
//                layoutModule.initialize();

                YUI({ logInclude: { TestRunner: true } }).use('test', 'console', function(Y) {
                    var suite = new Y.Test.Suite("DnD");


                    suite.add(new Y.Test.Case({
                        name: "iPad",

                        setUp: function() {
                        },

                        testListItemInstance: function() {
                        }

                    }));

                    Y.Test.Runner.add(suite);

                    // For IE7 used Yahoo console
                    if (isIE7()) {
                        new Y.Console({ newestOnTop: false }).render('#log');
                    }

                    Y.Test.Runner.run();

                });

            });
        </script>
    </t:putAttribute>
</t:insertTemplate>