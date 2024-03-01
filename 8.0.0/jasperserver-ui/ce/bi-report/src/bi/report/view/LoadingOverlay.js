/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import _ from 'underscore';
import $ from 'jquery';
import Backbone from 'backbone';
import i18n from 'js-sdk/src/i18n/CommonBundle.properties';
import domUtil from 'js-sdk/src/common/util/domUtil';

import logger from "js-sdk/src/common/logging/logger";

let localLogger = logger.register("LoadingOverlay");

export default Backbone.View.extend({
    overlayDOMObject: [],
    $overlayHolder: false,
    scaleFactor: 1,
    savedExternalContainersOverflow: false,
    initialize: function (options) {
        this.propertiesModel = options.propertiesModel;
        this.setExternalContainer(options.externalContainer);
        this.setBiComponentContainer(options.biComponentContainer);
        this.setBiComponent(options.biComponent);
        this.setScaleFactor(options.scaleFactor);
    },
    setExternalContainer: function (externalContainer) {
        this.$externalContainer = $(externalContainer);
    },
    setBiComponentContainer: function (biComponentContainer) {
        this.$biComponentContainer = $(biComponentContainer);
    },
    setBiComponent: function (biComponent) {
        this.$biComponent = $(biComponent);
    },
    setScaleFactor: function (scaleFactor) {
        if (_.isUndefined(scaleFactor)) {
            return;
        }
        this.scaleFactor = scaleFactor;
    },
    applyScale: function (scaleFactor) {
        if (_.isUndefined(scaleFactor)) {
            return;
        }
        this.scaleFactor = scaleFactor;
        this._applyScaleTransform();
    },
    show: function () {
        // if component container is absent, don't do anything
        if (this.$biComponentContainer.length < 1) {
            localLogger.debug('loading overlay: external container is absent');
            return;
        }    // we shouldn't show loading overlay unless the container has width and height
        // we shouldn't show loading overlay unless the container has width and height
        if (this.$biComponentContainer.height() === 0 || this.$biComponentContainer.width() === 0) {
            localLogger.debug('loading overlay: component container has zero size');
            return;
        }
        if (this.propertiesModel.get('isolateDom')) {
            if (!this.$biComponent.height()) {
                localLogger.debug('iframe mode: bi component hasn\'t any size');
                return;
            }
        }
        this._buildOverlay();
        this._attachToContainer();
        this.hideScrollBarOnContainer();
        this._adjustOverlaySizeAndPosition();
        this._applyScaleTransform();
        this.$overlayHolder.show();
    },
    hide: function () {
        this.$overlayHolder && this.$overlayHolder.hide();
        this.showScrollBarOnContainer();
    },
    showScrollBarOnContainer: function () {
        if (this.savedExternalContainersOverflow === true) {
            this.$externalContainer.css('overflow-x', this.savedExternalContainerOverflowValueX);
            this.$externalContainer.css('overflow-y', this.savedExternalContainerOverflowValueY);
            this.savedExternalContainersOverflow = false;
        }
    },
    hideScrollBarOnContainer: function () {
        if (this.savedExternalContainersOverflow === false) {
            // we'll remove the scroll bars only in case the container has at least one scroll bar
            var containerHasScrollBar = domUtil.hasScrollBar(this.$externalContainer[0], 'vertical') || domUtil.hasScrollBar(this.$externalContainer[0], 'horizontal');
            if (containerHasScrollBar) {
                this.savedExternalContainerOverflowValueX = this.$externalContainer.css('overflow-x');
                this.savedExternalContainerOverflowValueY = this.$externalContainer.css('overflow-y');
                this.savedExternalContainersOverflow = true;
                this.$externalContainer.css('overflow-x', 'hidden');
                this.$externalContainer.css('overflow-y', 'hidden');
            }
        }
    },
    remove: function () {
        this.hide();
        return Backbone.View.prototype.remove.call(this);
    },
    _buildOverlay: function () {
        // if it was initialized, don't do anything
        if (this.overlayDOMObject.length !== 0) {
            return;
        }    // Block this ability in this release (Amber)
        /*
			// check if user has supplied custom loading overlay
            var loadingOverlay = this.propertiesModel.get("loadingOverlay");
            if (_.isString(loadingOverlay)) {
				// in case of string we build an jQuery object from the string
				this.overlayDOMObject = $(loadingOverlay);
			}

			// if user has supplied customOverlay as jQuery object, let's use it
			if (_.isObject(loadingOverlay)) {
				this.overlayDOMObject = $(loadingOverlay).clone();
			}
			*/
        // check if supplied by user overlay is correct and if not -- use the default one
        // Block this ability in this release (Amber)
        /*
			// check if user has supplied custom loading overlay
            var loadingOverlay = this.propertiesModel.get("loadingOverlay");
            if (_.isString(loadingOverlay)) {
				// in case of string we build an jQuery object from the string
				this.overlayDOMObject = $(loadingOverlay);
			}

			// if user has supplied customOverlay as jQuery object, let's use it
			if (_.isObject(loadingOverlay)) {
				this.overlayDOMObject = $(loadingOverlay).clone();
			}
			*/
        // check if supplied by user overlay is correct and if not -- use the default one
        if (this.overlayDOMObject.length === 0) {
            this.overlayDOMObject = $('<div>' + i18n['dialog.overlay.loading'] + '</div>').css({
                position: 'absolute',
                top: 20,
                left: 0,
                width: '100%',
                color: '#fff',
                'text-align': 'center'
            });
        }
    },
    _attachToContainer: function () {
        if (this.$overlayHolder) {
            this.$overlayHolder.remove();
        }    // create the overlay holder (which will holds custom or default loading overlay and the background blur)
        // create the overlay holder (which will holds custom or default loading overlay and the background blur)
        this.$overlayHolder = $('<div></div>').append($('<div></div>').css({
            width: '100%',
            height: '100%',
            filter: 'alpha(opacity=40)',
            opacity: '.4',
            'background-color': 'black'
        }));    // add overlayDOMObject (overlay loading element) to the loading overlay holder
        // add overlayDOMObject (overlay loading element) to the loading overlay holder
        this.$overlayHolder.append(this.overlayDOMObject);    // now, insert the loading overlay holder into the container
        // now, insert the loading overlay holder into the container
        this.$overlayHolder.prependTo(this.$biComponentContainer);
    },
    _adjustOverlaySizeAndPosition: function () {
        var top = 0, left = 0, externalContainerWidth, externalContainerHeight, width, height, exContainer = {
            padding: {
                left: 0,
                top: 0,
                right: 0,
                bottom: 0
            },
            innerHeight: this.$externalContainer.innerHeight(),
            innerWidth: this.$externalContainer.innerWidth(),
            outerHeight: this.$externalContainer.outerHeight(),
            outerWidth: this.$externalContainer.outerWidth(),
            scrollHeight: this.$externalContainer[0].scrollHeight,
            scrollWidth: this.$externalContainer[0].scrollWidth,
            scrollTop: this.$externalContainer.scrollTop(),
            scrollLeft: this.$externalContainer.scrollLeft()
        };
        exContainer.padding.top = parseInt(this.$externalContainer.css('paddingTop'), 10);
        if (_.isNaN(exContainer.padding.top)) {
            exContainer.padding.top = 0;
        }
        exContainer.padding.right = parseInt(this.$externalContainer.css('paddingRight'), 10);
        if (_.isNaN(exContainer.padding.right)) {
            exContainer.padding.right = 0;
        }
        exContainer.padding.bottom = parseInt(this.$externalContainer.css('paddingBottom'), 10);
        if (_.isNaN(exContainer.padding.bottom)) {
            exContainer.padding.bottom = 0;
        }
        exContainer.padding.left = parseInt(this.$externalContainer.css('paddingLeft'), 10);
        if (_.isNaN(exContainer.padding.left)) {
            exContainer.padding.left = 0;
        }
        if (exContainer.scrollWidth <= exContainer.outerWidth) {
            localLogger.debug('width: biComponent is smaller than container or equal OR container doesn\'t have horizontal scroll bars');
            externalContainerWidth = exContainer.innerWidth - (exContainer.padding.left + exContainer.padding.right);
            left = exContainer.scrollLeft;
        } else {
            localLogger.debug('width: biComponent is larger than container');
            left = exContainer.scrollLeft - (exContainer.scrollLeft < exContainer.padding.left ? 0 : exContainer.padding.left);
            externalContainerWidth = exContainer.innerWidth + exContainer.padding.right;
            if (exContainer.scrollLeft + exContainer.outerWidth >= exContainer.scrollWidth - exContainer.padding.right) {
                localLogger.debug('width: scrolled to the end');
                externalContainerWidth = exContainer.innerWidth;
            }
        }
        if (exContainer.scrollHeight <= exContainer.outerHeight) {
            localLogger.debug('height: biComponent is smaller than container or equal OR container doesn\'t have vertical scroll bars');
            externalContainerHeight = exContainer.innerHeight - (exContainer.padding.top + exContainer.padding.bottom);
            top = exContainer.scrollTop;
        } else {
            localLogger.debug('height: biComponent is larger than container');
            top = exContainer.scrollTop - (exContainer.scrollTop < exContainer.padding.top ? 0 : exContainer.padding.top);
            externalContainerHeight = exContainer.innerHeight + exContainer.padding.bottom;
            if (exContainer.scrollTop + exContainer.outerHeight >= exContainer.scrollHeight - exContainer.padding.bottom) {
                localLogger.debug('height: scrolled to the end');
                externalContainerHeight = exContainer.innerHeight;
            }
        }
        this.$overlayHolder.css({
            position: 'absolute',
            top: top,
            left: left,
            'z-index': 495,
            width: externalContainerWidth / this.scaleFactor,
            height: externalContainerHeight / this.scaleFactor,
            display: 'none'
        });
    },
    _applyScaleTransform: function () {
        var scale = 'scale(' + this.scaleFactor + ')', origin = 'top left', css = {
            '-webkit-transform': scale,
            '-moz-transform': scale,
            '-ms-transform': scale,
            '-o-transform': scale,
            // transform: scale, analog for IE8 and lower.
            'filter': 'progid:DXImageTransform.Microsoft.Matrix(M11=' + this.scaleFactor + ', M12=0, M21=0, M22=' + this.scaleFactor + ', SizingMethod=\'auto expand\')',
            'transform': scale,
            '-webkit-transform-origin': origin,
            '-moz-transform-origin': origin,
            '-ms-transform-origin': origin,
            '-o-transform-origin': origin,
            'transform-origin': origin
        };
        this.$overlayHolder && this.$overlayHolder.css(css);
    }
});