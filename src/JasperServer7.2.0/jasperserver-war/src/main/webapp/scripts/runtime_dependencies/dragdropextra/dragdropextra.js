//  DragDropExtra Scriptaculous Enhancement, version 0.5
//  (c) 2007-2008 Christopher Williams, Iterative Designs
//
// v0.5 release
//      - Fixed bug where 2nd drag on an element in IE would result in funny placement of the
//        element. [shammond42]
// v0.4 release
//		- Fixed issue with dragging and dropping in IE7 due to an exception being thrown and not properly reseting in FinishDrag.
// v0.3 release
//    - Fixed bug found by Phillip Sauerbeck psauerbeck@gmail. Tests added based on Phillip's efforts.
// v0.2 release
//      - Minor bug fix for the releasing of objects after they have been dropped, prevents memory leak.
// v0.1 release
//      - initial release for the super ghosting capability
//      - Drags from one scrolling list to the other (overflow:auto)
//      - Retains the original object so that it can remain present despite being dragged
//
// dragdropextra.js is freely distributable under the terms of an MIT-style license.
// For details, see the Iterative Designs web site: http://www.iterativedesigns.com/
// Parts of this code have been taken from the original dragdrop.js library which is
// copyrighted by (c) 2005-2007 Thomas Fuchs (http://script.aculo.us,
// http://mir.aculo.us) and (c) 2005-2007 Sammi Williams
// (http://www.oriontransfer.co.nz, sammi@oriontransfer.co.nz) and available under
// a MIT-style license.

//////////////////////////////////////////////////////////////////////////////////////////////
//Jaspersoft Updates (look for comment: JASPERSOFT #x)
//////////////////////////////////////////////////////////////////////////////////////////////
// #1 3/27/10 if options.mouseOffset is true then dragging thing positioned with mousepointer
// 		   see Draggable.prototype.draw
// #2 9/27/10 restore zIndex of coloned element after dragging
// #1 11/30/100 updated #1 to allow for parent element offsets
//////////////////////////////////////////////////////////////////////////////////////////////

Draggable.prototype.startDrag = function(event) {
	Draggable.isDragging = true;
    this.dragging = true;
    if (!this.delta)
        this.delta = this.currentDelta();

    if (this.options.zindex) {
        this.originalZ = parseInt(Element.getStyle(this.element, 'z-index') || 0);
        this.element.style.zIndex = this.options.zindex;
    }
    if (this.options.ghosting) {
        this._clone = this.element.cloneNode(true);
        this._originallyAbsolute = (this.element.getStyle('position') == 'absolute');
        if (!this._originallyAbsolute) {
            Position.absolutize(this.element);
        }
        this.element.parentNode.insertBefore(this._clone, this.element);
        if (this.element.parentNode.tagName === "TR") {
            document.body.appendChild(this.element);
        }
    }

    if (this.options.superghosting) {
        Position.prepare();
        var pointer = [Event.pointerX(event), Event.pointerY(event)];
        var body = document.getElementsByTagName("body")[0];
        var me = this.element;
        this._clone = me.cloneNode(true);
        if (Prototype.Browser.IE) {
            // Clear event handing from the clone
            // Solves the second drag issue in IE
            this._clone.clearAttributes();
            this._clone.mergeAttributes(me.cloneNode(false));
        }
        me.parentNode.insertBefore(this._clone, me);
        me.id = "clone_" + me.id;
        me.hide();

        Position.absolutize(me);
        me.parentNode.removeChild(me);
        body.appendChild(me);
        //Retain height and width of object only if it has been nulled out.  -v0.3 Fix
        if (me.style.width == "0px" || me.style.height == "0px") {
            me.style.width = Element.getWidth(this._clone) + "px";
            me.style.height = Element.getHeight(this._clone) + "px";
        }

        //overloading in order to reduce repeated code weight.
        this.originalScrollTop = (Element.getHeight(this._clone) / 2);

        this.draw(pointer);

        me.show();
    }

    if (this.options.scroll) {
        if (this.options.scroll == window) {
            var where = this._getWindowScroll(this.options.scroll);
            this.originalScrollLeft = where.left;
            this.originalScrollTop = where.top;
        } else {
            this.originalScrollLeft = this.options.scroll.scrollLeft;
            this.originalScrollTop = this.options.scroll.scrollTop;
        }
    }

    Draggables.notify('onStart', this, event);

    if (this.options.starteffect) this.options.starteffect(this.element);
};


Draggable.prototype.draw = function(point) {
    var pos = Position.cumulativeOffset(this.element);

    if (this.options.ghosting) {
        var r = Position.realOffset(this.element);
        pos[0] += r[0] - Position.deltaX;
        pos[1] += r[1] - Position.deltaY;
    }

    var d = this.currentDelta();
    pos[0] -= d[0];
    pos[1] -= d[1];

    if(this.options.scroll) {
        pos[0] -= this.options.scroll.scrollLeft;
        pos[1] -= this.options.scroll.scrollTop;
    }

    if (this.options.scroll && (this.options.scroll != window && this._isScrollChild)) {
        pos[0] -= this.options.scroll.scrollLeft - this.originalScrollLeft;
        pos[1] -= this.options.scroll.scrollTop - this.originalScrollTop;
    }

    var p = [0,1].map(function(i) {
        /*
         * JASPERSOFT #1 - see comment block top of page for more info
         */
        return (point[i] - pos[i] - (this.options.mouseOffset ? (-2) : this.offset[i]));
    }.bind(this));

    if (this.options.snap) {
        if (Object.isFunction(this.options.snap)) {
            p = this.options.snap(p[0], p[1], this);
        } else {
            if (Object.isArray(this.options.snap)) {
                p = p.map(function(v, i) {
                    return (v / this.options.snap[i]).round() * this.options.snap[i]
                }.bind(this))
            } else {
                p = p.map(function(v) {
                    return (v / this.options.snap).round() * this.options.snap
                }.bind(this))
            }
        }
    }

    if (this.options.superghosting) {
        if (this.element.getStyle('position') == 'absolute') {
            p[1] = point[1] - this.originalScrollTop;
        } else {
            p[1] -= this.originalScrollTop || 10;
        }
    }

    var style = this.element.style;
    //JASPERSOFT #1 - see comment block top of page for more info
    if ((!this.options.constraint) || (this.options.constraint == 'horizontal'))
        style.left = p[0] + "px";
    if ((!this.options.constraint) || (this.options.constraint == 'vertical'))
        style.top = p[1] + "px";
    //END JASPERSOFT #2

    if (style.visibility == "hidden") style.visibility = ""; // fix gecko rendering
};

Draggable.prototype.initDrag = function(event) {
    if (!Object.isUndefined(Draggable._dragging[this.element]) &&
            Draggable._dragging[this.element]) return;

    if ((event.touches && event.touches.length == 1) || Event.isLeftClick(event)) {
        // abort on form elements, fixes a Firefox issue
        var src = Event.element(event);
        var tag_name = src.tagName.toUpperCase();
        if (tag_name == 'INPUT' ||
            tag_name == 'SELECT' ||
            tag_name == 'OPTION' ||
            tag_name == 'BUTTON' ||
            tag_name == 'TEXTAREA') return;

        if(jQuery(this.element).parents('#sortDialog').length > 0 && tag_name == 'B') {
            return;
        }

        var pointer = [Event.pointerX(event), Event.pointerY(event)];
        var pos = Position.cumulativeOffset(this.element);
        this.offset = [0,1].map(function(i) {
            return (pointer[i] - pos[i])
        });

        Draggables.activate(this);
        this.countdown = Draggables.DEFAULT_TOLERANCE;
        Event.stop(event);
        this.element.fire('drag:mousedown', {targetEvent: event});
    }
};

Droppables.isAffected = function(point, element, drop) {
    //Position.prepare();
    //var positioned_within = Position.withinIncludingScrolloffsets(drop.element, point[0], point[1]);
    var jo = jQuery(drop.element);
    var w = jo.width();
    var h = jo.height();
    var p0 = jo.offset();
    var p1 = {
    	left: p0.left + w,
    	top: p0.top + h
    };
    var positioned_within = point[0] > p0.left && point[0] < p1.left && point[1] > p0.top && point[1] < p1.top;

    return ((drop.element != element) &&
           ((element.parentNode === $(document.body)) || (!drop._containers) || this.isContained(element, drop)) &&
           ((!drop.accept) || (Element.classNames(element).detect(function(v) { return drop.accept.include(v) } ))) &&
           positioned_within );
};

Draggable.prototype.finishDrag = function(event, success) {
	Draggable.isDragging = false;
    this.dragging = false;

    if (isIE()) document.body.onmousemove = function(){};  // fix for bug 25666

    if (this.options.quiet) {
        Position.prepare();
        var pointer = [Event.pointerX(event), Event.pointerY(event)];
        Droppables.show(pointer, this.element);
    }

    if (this.options.ghosting) {
        if (!this._originallyAbsolute) {
            Position.relativize(this.element);
            if (this._clone.parentNode.tagName === "TR") {
                this._clone.parentNode.insertBefore(this.element, this._clone);
            }
        }
        delete this._originallyAbsolute;
        Element.remove(this._clone);
        this._clone = null;
    }

    var dropped = false;

    if (success) {
        dropped = Droppables.fire(event, this.element);
        if (!dropped) dropped = false;
    }
    if (dropped && this.options.onDropped) this.options.onDropped(this.element);
    Draggables.notify('onEnd', this, event);

    var revert = this.options.revert;
    if (revert && Object.isFunction(revert)) revert = revert(this.element);

    var d = this.currentDelta();
    if (revert && this.options.reverteffect) {
        if (dropped == 0 || revert != 'failure')
            this.options.reverteffect(this.element,
                    d[1] - this.delta[1], d[0] - this.delta[0]);
    } else {
        this.delta = d;
    }

    if (this.options.zindex) {
        this.element.style.zIndex = this.originalZ;
        /*
         * JASPERSOFT #2 - see comment block top of page for more info
         */
        this._clone && (this._clone.style.zIndex = this.originalZ);
    }

    this.options.endeffect && this.options.endeffect(this.element);

    if (this.options.superghosting) {
        // If detached drag element from DOM, we should attach it again!
        if (this.element.parentNode == null) {
            Element.hide(this.element);
            $(document.body).appendChild(this.element);
        }
        Element.remove(this.element);
        new Draggable(this._clone, this.options);
    }

    Draggables.deactivate(this);
    Droppables.reset();
};

Sortable.defaultOnHover = Sortable.onHover;
Sortable.onHover = function(element, dropon, overlap) {
    if (!element.hasClassName("dialog")) {
        Sortable.defaultOnHover(element, dropon, overlap);
    }
};

Sortable.defaultOnEmptyHover = Sortable.onEmptyHover;
Sortable.onEmptyHover = function(element, dropon, overlap) {
    if (!element.hasClassName("dialog")) {
        Sortable.defaultOnEmptyHover(element, dropon, overlap);
    }
};

var SortableObserver = Class.create({
    initialize: function(element, observer) {
        this.element = $(element);
        this.observer = observer;
        this.lastValue = Sortable.serialize(this.element);
    },
    onStart: function() {
        this.lastValue = Sortable.serialize(this.element);
    },
    onEnd: function(eventName, draggable) {
        Sortable.unmark();
        if(this.lastValue != Sortable.serialize(this.element)) {
            this.observer(this.element, draggable);
        }
    }
});

Droppables.show = function(point, element) {
    if (!this.drops.length) return;
    var drop, affected = [];

    this.drops.each(function(drop) {
        if(Droppables.isAffected(point, element, drop)) {
            if(element.hasClassName('sortDialogAvailable') || element.hasClassName('sortDialogSortFields')) {
                if(drop.element.id == 'sortDialogAvailable' || drop.element.id == 'sortDialogSortFields') {
                    affected.push(drop);
                }
            } else {
                affected.push(drop);
            }
        }
    });

    if (affected.length > 0)
        drop = Droppables.findDeepestChild(affected);

    if (this.last_active && this.last_active != drop) {
    	this.deactivate(this.last_active);
    }
    if (drop) {
        Position.within(drop.element, point[0], point[1]);
        if (drop.onHover) {
            // Hack to dynamically change level/measure appearing when dragging from tree to some drop container
            if (element.classNames().include("wrap")) {
                element.relativize();
                element.classNames().include("measure") ?
                        element.classNames().set("draggable dragging measure" + (element.classNames().include("supportsFilter") ? " supportsFilter"  : "")) :
                        element.classNames().set("draggable dragging dimension" + (element.classNames().include("supportsFilter") ? " supportsFilter"  : ""));
                element.style.position = 'relative';
                element.style.display = 'inline-block';
                element.style.width = '';
                element.style.height = '';
            }

            drop.onHover(element, drop.element, Position.overlap(drop.overlap, drop.element));
        }

        if (drop != this.last_active) {
        	Droppables.activate(drop);
        }
    }
};

