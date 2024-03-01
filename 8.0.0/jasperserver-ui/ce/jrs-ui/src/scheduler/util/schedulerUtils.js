/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import $ from 'jquery';
import config from 'js-sdk/src/jrs.configs';
import _ from 'underscore';
import {redirectToUrl} from "../../util/utils.common";
import schedulerOverlayIframeTemplate from "../template/schedulerOverlayIframeTemplate.htm";
import schedulerConstants from './schedulerConstants';
import i18n from '../../i18n/all.properties';

export default {
    el: $(_.template(schedulerOverlayIframeTemplate, {i18n: i18n})),
    SCHEDULER_BACK_URL_STORAGE_NAME: schedulerConstants.SCHEDULER_BACK_URL_STORAGE_NAME,
    getParamsFromUri: function () {
        var params = {};    // first, let's parse normal query parameters
        // first, let's parse normal query parameters
        var parts = document.location.search.substr(1).split('&');
        _.each(parts, function (part) {
            var tmp = part.split('='), key = tmp[0], value = tmp[1];
            if (value === '') {
                return;
            }
            params[key] = decodeURIComponent(value);
        });    /// then, let's parse hash
        /// then, let's parse hash
        var hash = document.location.hash.substr(1);    // get rid of "runInBackground@" mark in document.location.hash
        // get rid of "runInBackground@" mark in document.location.hash
        if (hash.indexOf('runInBackground@') === 0) {
            hash = hash.replace('runInBackground@', '');
        }
        var hashParts = hash.split('@@');
        params['reportUri'] = hashParts.shift();    // get first element from array
        // get first element from array
        _.each(hashParts, function (part) {
            var tmp = part.split('='), key = tmp[0], value = tmp[1];
            if (value === '') {
                return;
            }
            params[key] = decodeURIComponent(value);
        });
        return params;
    },
    saveCurrentLocation: function () {
        if (document.referrer.indexOf('login.html') === -1) {
            window.localStorage && localStorage.setItem(this.SCHEDULER_BACK_URL_STORAGE_NAME, encodeURIComponent(document.referrer));
        }
    },
    getBackToPreviousLocation: function () {
        var currentUrl = encodeURIComponent(document.location.href);
        var lastUrl = window.localStorage ? localStorage.getItem(this.SCHEDULER_BACK_URL_STORAGE_NAME) : '';
        if (lastUrl && lastUrl !== currentUrl) {
            var url = decodeURIComponent(lastUrl);
            if (url) {
                redirectToUrl(url);
                return;
            }
        }    // in bad scenario, we need to get to the standard URL
        // in bad scenario, we need to get to the standard URL
        redirectToUrl(config.contextPath + '/flow.html?_flowId=searchFlow');
    },
    _scheduleDashboard: function _scheduleDashboard(paramsMap, schedulerPage) {
        _.bindAll(this, '_onWindowResize');
        $(window).on('resize', this._onWindowResize);
        let link = config.contextPath + schedulerPage,
            encode = true,
            encodeParams = this._serializeParams(paramsMap, encode) ? '?' + this._serializeParams(paramsMap, encode) : '';

        let Url = link + ($.isEmptyObject(paramsMap) ? '' : encodeParams);

        if (schedulerPage) {
            let body = $('body'),
                bannerHeight = $("body").find("> #banner").outerHeight(true) || 0;

            this._addDimmer();
            this.el.attr('src', Url).css({
                'width': '100%',
                'height':body.find('> #frame').height() - bannerHeight,
                'z-index': schedulerConstants.DASHBOARD_SCHEDULAR_IFRAME_Z_INDEX,
                'position': 'absolute',
                'top': bannerHeight * 2 + 'px'
            }).show();
            body.append(this.el);
        }
    },
    _serializeParams: function _serializeParams(paramsMap, encode) {
        let result = schedulerConstants.SCHEDULER_EMPTY;

        for (let name in paramsMap) {
            if (!Object.isUndefined(name)) {
                result += '&' + name + '=' + (encode ? encodeURIComponent(paramsMap[name]) : paramsMap[name]);
            }
        }
        result = result !== schedulerConstants.SCHEDULER_EMPTY ? result.substring(1) : schedulerConstants.SCHEDULER_EMPTY;
        return result;
    },
    _ParamMapping: function _ParamMapping(paramsMap) {
        this._scheduleDashboard(paramsMap, '/scheduler/main.html');
    },
    _closeScheduleOverlay: function _closeScheduleOverlay() {
        $('.schedulerAccelerator').slideUp('slow',function () {
            window.parent.document.getElementsByClassName("schedulerOverlayIframe")[0].hide();
            window.parent.document.getElementById("dialogDimmer").hide();
        })
    },
    _addDimmer: function () {
        if (!$('#dialogDimmer').length) {
            this.$dimmer = $('<div id=\'dialogDimmer\' class=\'dimmer hidden\' style=\'z-index:' + schedulerConstants.DIALOG_DIMMER_IFRAME_Z_INDEX + '; display: none;\'></div>');
            $('body').append(this.$dimmer);
        } else {
            !this.$dimmer && (this.$dimmer = $('#dialogDimmer'));
        }
        this.$dimmer.removeClass('hidden').show();
    },
    _onWindowResize: function () {
        let body = $('body'),
            bannerHeight = $("body").find("> #banner").outerHeight(true) || 0;
        this.resizeTimer && clearTimeout(this.resizeTimer);
        this.resizeTimer = setTimeout(_.bind(function () {
            this.el && this.el.height(body.find('> #frame').height() - bannerHeight);
            this.el && this.el.width(body.width());
        }, this), 100);
    },
    _detachEvents: function () {
        $(window).off('resize', this._onWindowResize);
        this.el.css('display','none');
    }
};
