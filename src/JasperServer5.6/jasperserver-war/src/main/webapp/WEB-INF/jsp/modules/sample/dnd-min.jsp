<html>
<head>
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/lib/prototype-1.7.1-patched.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/lib/dragdropextra-0.2-patched.js"></script>

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
      div#droppable_demo1 {
        width: 160px;
        height: 120px;
        background: #fff;
        border: 5px solid #ccc;
        text-align: center;
        position: relative;
        top: -60px;
        left: 140px;
        line-height: 100px; }
      div#droppable_demo2 {
        width: 160px;
        height: 120px;
        background: #fff;
        border: 5px solid #ccc;
        text-align: center;
        position: relative;
        top: -190px;
        left: 340px;
        line-height: 100px; }
      div#droppable_demo1.hover, div#droppable_demo2.hover {
        border: 5px dashed #aaa;
        background:#efefef; }
    </style>

    <meta name="decorator" content="empty" />
</head>
<body>
    <div class="demo" id="droppable_container">
        <div id="draggable_demo" class="draggable">
            Drag me!
        </div>

        <div id="drop_zone">
            <div id="droppable_demo1">
                Drop here!
            </div>
            <div id="droppable_demo2">
                Drop here!
            </div>
        </div>
    </div>

    <script type="text/javascript">
        var over = null;

        new Draggable('draggable_demo', {
            revert: true,
            onEnd:function() {
                over = null;
            }
        });

        Droppables.add('drop_zone', {
            accept: 'draggable',
            hoverclass: 'hover',
            onHover: function(dragged, dropped, pos, event) {
                over = event.element();
            },
            onDrop: function(dragged, dropped, event) {
                console.log("drag - %o, drop - %o, event - o%", dragged, dropped, event);
                over.highlight();
            }
        });

        $("drop_zone").observe("mouseover", function(e) {
            over && console.log("mouseover");
        });
        $("drop_zone").observe("mouseout", function(e) {
            over && console.log("mouseout");
        });
        $("drop_zone").observe("touchmove", function(e) {
            over = event.element();
        });
    </script>

</body>
</html>