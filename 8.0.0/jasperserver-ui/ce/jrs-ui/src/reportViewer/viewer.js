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


/**
 * @version: $Id$
 */

/*global alert, requirejs*/

import Report from 'bi-report/src/bi/report/Report';
import reportStatuses from 'bi-report/src/bi/report/enum/reportStatuses';
import jrScaleStrategiesMapping from 'bi-report/src/bi/report/enum/jrScaleStrategiesMapping';
import ReportViewRuntime from './report.view.runtime';
import $ from 'jquery';
import _ from 'underscore';
import 'jquery-ui/ui/widgets/draggable';
import buttonManager from '../core/core.buttonManager';
import dialogs from '../components/components.dialogs';
import stdnav from 'js-sdk/src/common/stdnav/stdnav';
import xssUtil from 'js-sdk/src/common/util/xssUtil';
import i18n from '../i18n/all.properties';
import hyperlinkTypes from 'bi-report/src/bi/report/jive/enum/hyperlinkTypes';
import errorDialogTemplate from './templates/errorDialogTemplate.htm';

var defaultReportStatus = {
    pages: {
        current: 1,
        total: null,
        lastPartialPage: null,
        dataTimestampMessage: null
    },
    snapshotSaveStatus: null,
    isExportRunning: false
};

var Viewer = function(options) {
    var it = this;
    it._reportInstance = null;
    it.jive = null;
    it.canSave = true;
    it.config = {
        at: null,
        reporturi: null,
        async: true,
        page: null,
        anchor: null,
        toolbar: true
    };
    it.dfds = {
        'jive.inactive': null
    };
    it.loaded = false;
    it.search = {
        currentIndex: 0,
        results: []
    };
    it.tabs = {
        maxCount: 4, //FIXME: this is configured with JRL props
        maxLabelLength: 16 //FIXME: this is configured with JRL props
    };

    $.extend(it.config, options);

    it.reportStatus = $.extend(true, {}, defaultReportStatus);

    it.features = {
        zoom: {
            options: [],
            optionType: 'toggle',
            selectedKeys: ['1', 'fit_actual'],
            onClick: function(val) {
                it.saveCurrentLevel();
                it.processZoomOption(val);
            },
            levels: {
                previous: {value: null, literal: null},
                current: {value: 1, literal: null},
                dflt: null
            },
            containerWidth: $('div#reportContainer').width()
        },
        search: {
            options: [],
            optionType: 'select',
            selectedKeys: [],
            config: {
                caseSensitive: false,
                wholeWordsOnly: false
            },
            onClick: function(val) {
                var cfg = it.features.search.config,
                    prop;
                cfg[val] = !cfg[val];

                it.features.search.selectedKeys = [];
                for (prop in cfg) {
                    if (cfg.hasOwnProperty(prop) && cfg[prop]) {
                        it.features.search.selectedKeys.push(prop);
                    }
                }
            }
        }
    };

    /********** Report Container Zoom **********/
    if ($('script#reportZoomText').length > 0) {
        it.features.zoom.options = JSON.parse($('script#reportZoomText').html());

        $('button#zoom_in').on('click', function(evt) {
            it.saveCurrentLevel();
            it.zoomIn();
        });
        $('button#zoom_out').on('click', function(evt) {
            it.saveCurrentLevel();
            it.zoomOut();
        });
        $('input#zoom_value').on('keydown', function(evt) {
            if (evt.which === 13) { // on enter key released
                if (this.value.length) {
                    it.saveCurrentLevel();
                    it.zoomTo(parseFloat(this.value)/100);
                    it.getOptionsMenu('zoom').hide();
                }
            }
        });
        $('button#zoom_value_button').on('click', function(evt) {
            var btnOpt = $(this),
                input = $("input#zoom_value"),
                offset = input.offset(),
                optionsMenu = it.getOptionsMenu('zoom'),
                openFor = optionsMenu.data('openFor') || "";

            if (optionsMenu.is(':visible') && openFor === "zoom") {
                optionsMenu.hide();
                optionsMenu.data('openFor', null);
                input.focus();
            } else {
                if (optionsMenu.is(':visible') && openFor !== "zoom") {
                    optionsMenu.hide();
                }
                optionsMenu.show().offset({
                    left: offset.left,
                    top: offset.top + btnOpt.height()
                });
                optionsMenu.data('openFor', 'zoom');
                input.blur();
            }
        });
    }
    /************************/

    /*********** Report Search **********/
    if ($('script#reportSearchText').length > 0) {
        it.features.search.options = JSON.parse($('script#reportSearchText').html());
        $('input#search_report').on('keydown', function(evt) {
            if (evt.which === 13) { // on enter key released
                var searchValue = this.value;
                if (this.value.length) {
                    it._reportInstance.search({
                        text: this.value,
                        caseSensitive: it.features.search.config.caseSensitive,
                        wholeWordsOnly: it.features.search.config.wholeWordsOnly
                    }).done(function(results) {
                        if (results.length) {
                            var searchResults = Array.prototype.slice.call(results);

                            //make sure search results are sorted ascending, by page number
                            searchResults.sort(function (r1, r2) {
                                return r1.page - r2.page;
                            });

                            var firstSearchPage = searchResults[0].page;
                            it.search.results = searchResults;
                            it.search.currentPage = firstSearchPage;

                            it.goToPage(firstSearchPage).then(function() {
                                var elem = $('.jr_search_result:first');
                                elem.addClass('highlight');
                                it.scrollElementIntoView(elem[0]);

                                if (it.search.results.length > 1 || it.search.results.length === 1 && it.search.results[0].hitCount > 1) {
                                    $('button#search_next').prop('disabled', false);
                                    $('button#search_previous').prop('disabled', false);
                                } else {
                                    $('button#search_next').prop('disabled', true);
                                    $('button#search_previous').prop('disabled', true);
                                }
                            });
                        } else {
                            // FIXME: use localized message
                            dialogs.errorPopup.show("Jaspersoft has finished searching the document. No matches were found for: \"" + xssUtil.hardEscape(searchValue) + "\"!");
                            it.resetSearch(true);
                        }
                    });
                } else {
                    // FIXME: use localized message
                    dialogs.errorPopup.show("Search string cannot be empty!");
                }
            }
        });

        $('button#search_report_button').on('click', function(evt) {
            $('input#search_report').trigger($.Event("keydown", {which: 13}));
        });

        $('button#search_options').on('click', function(evt) {
            var btnOpt = $(this),
                offset = $('input#search_report').offset(),
                optionsMenu = it.getOptionsMenu('search'),
                openFor = optionsMenu.data('openFor') || "";

            if (optionsMenu.is(':visible') && openFor === "search") {
                optionsMenu.hide();
                optionsMenu.data('openFor', null);
            } else {
                if (optionsMenu.is(':visible') && openFor !== "search") {
                    optionsMenu.hide();
                }
                optionsMenu.show().offset({
                    left: offset.left,
                    top: offset.top + btnOpt.height()
                });
                optionsMenu.data('openFor', 'search');
            }
        });

        var btnSearchNext = $('button#search_next'),
            btnSearchPrev = $('button#search_previous');

        btnSearchNext.on('click', function(evt) {
            var spans = $('.jr_search_result'),
                currentPage = it.reportStatus.pages.current,
                nextPage = null,
                resultsForPage = 0, i, ln, elem;

            // search results are sorted by page number in ascending order
            for (i = 0, ln = it.search.results.length; i < ln; i++) {
                if (currentPage === it.search.results[i].page) {
                    resultsForPage = it.search.results[i].hitCount;
                }
                if (currentPage < it.search.results[i].page) {
                    nextPage = it.search.results[i].page;
                    break;
                }
            }

            if (nextPage == null) {
                nextPage = it.search.results[0].page;
            }

            if (it.search.currentIndex < resultsForPage - 1) {
                spans.eq(it.search.currentIndex).removeClass('highlight');
                elem = spans.eq(++it.search.currentIndex);
                elem.addClass('highlight');
                it.scrollElementIntoView(elem[0]);

                it.search.currentPage = currentPage;
            } else {
                if (nextPage === currentPage) {
                    spans.eq(it.search.currentIndex).removeClass('highlight');
                    it.search.currentIndex = 0;

                    elem = spans.eq(it.search.currentIndex);
                    elem.addClass('highlight');
                    it.scrollElementIntoView(elem[0]);

                    it.search.currentPage = currentPage;
                } else {
                    it.disableSearchButtons();
                    it.goToPage(nextPage).then(function() {
                        it.enableSearchButtons();
                        var elem = $('.jr_search_result:first');
                        elem.addClass('highlight');
                        it.scrollElementIntoView(elem[0]);
                        it.search.currentIndex = 0;
                        it.search.currentPage = nextPage;
                    });
                }
            }
        });

        btnSearchPrev.on('click', function(evt) {
            var spans = $('.jr_search_result'),
                currentPage = it.reportStatus.pages.current,
                prevPage = null,
                prevPageResults = 0,
                resultsForPage = 0, i, elem;

            for (i = it.search.results.length - 1; i >= 0; i--) {
                if (currentPage === it.search.results[i].page) {
                    resultsForPage = it.search.results[i].hitCount;
                }
                if (currentPage > it.search.results[i].page) {
                    prevPage = it.search.results[i].page;
                    prevPageResults = it.search.results[i].hitCount;
                    break;
                }
            }

            if (prevPage == null) {
                prevPage = it.search.results[it.search.results.length - 1].page;
                prevPageResults = it.search.results[it.search.results.length - 1].hitCount;
            }

            if (it.search.currentIndex > 0 && resultsForPage > 0) {
                spans.eq(it.search.currentIndex).removeClass('highlight');
                elem = spans.eq(--it.search.currentIndex);
                elem.addClass('highlight');
                it.scrollElementIntoView(elem[0]);

                it.search.currentPage = currentPage;
            } else {
                if (prevPage === currentPage) {
                    spans.eq(it.search.currentIndex).removeClass('highlight');
                    it.search.currentIndex = resultsForPage - 1;

                    elem = spans.eq(it.search.currentIndex);
                    elem.addClass('highlight');
                    it.scrollElementIntoView(elem[0]);

                    it.search.currentPage = currentPage;
                } else {
                    it.disableSearchButtons();
                    it.goToPage(prevPage).then(function() {
                        it.enableSearchButtons();
                        var elem = $('.jr_search_result:last');
                        elem.addClass('highlight');
                        it.scrollElementIntoView(elem[0]);
                        it.search.currentIndex = prevPageResults - 1;
                        it.search.currentPage = prevPage;
                    });
                }
            }
        });
    }
    /*********************/

    /*************** Report Bookmarks **************/
    it.bookmarksContainer = null;
    $('button#bookmarksDialog').on('click', function() {
        it.bookmarksContainer && it.bookmarksContainer.toggle().offset({left: 10, top: 95});
    });
    /*****************************/

};

Viewer.privateMethods = {
    // bookmarks
    prepareBookmarks: function(bookmarks) {
        var it = this,
            container = $('div#jr_bookmarks'),
            body,
            html;

        // if bookmarks already in place, do nothing
        if (container.length && container.find('.jrbookmark').length > 0) {
            return;
        }

        if (!container.length) {
            container = $(
                "<div id='jr_bookmarks' class='panel dialog overlay moveable sizeable' style='width: 250px; height: 600px; z-index: 2000; display: none'>" +
                "<div class='sizer diagonal ui-resizable-handle ui-resizable-se'></div>" +
                "<div class='content hasFooter'>" +
                "<div class='header mover'>" +
                "<div class='closeIcon'></div>" +
                "<div class='title'>" + i18n['net.sf.jasperreports.components.headertoolbar.bookmarks.dialog.title'] + "</div>" +
                "</div>" +
                "<div class='body' style='top: 29px; bottom: 24px; padding: 0'></div>" +
                "<div class='footer' style='height: 24px'></div>" +
                "</div>" +
                "</div>"
            ).appendTo('body');
            container.on('click', 'a.jrbookmark', function(evt) {
                evt.preventDefault();
                var link = $(this),
                    pageIndex = link.data('pageindex'),
                    anchor = link.text();

                if (it.reportStatus.pages.current === pageIndex) {
                    window.location.hash = anchor;
                } else {
                    it.goToPage(pageIndex).then(function() {
                        window.location.hash = anchor;
                    });
                }
            });
            container.on('click', 'div.closeIcon', function() {
                container.hide();
            });
            container.on('click', 'b.icon', function() {
                var it = $(this),
                    parentLi;
                if (!it.is('.noninteractive')) {
                    parentLi = it.closest('li.subtree');
                    if (parentLi.is('.open')) {
                        parentLi.removeClass('open').addClass('closed');
                    } else {
                        parentLi.removeClass('closed').addClass('open');
                    }
                }
            });
            container.draggable({ handle: ".mover" });
            container.resizable({ handles: {se: '.sizer'} });

            it.bookmarksContainer = container;
        }
        body = container.find('.body');
        body.empty();

        html = "<ul class='list collapsible type_basic'>";
        $.each(bookmarks, function(i, bookmark) {
            html += it.exportBookmark(bookmark);
        });
        html += "</ul>";

        body.html(html);
    },
    exportBookmark: function(bookmark) {
        var it = this,
            html = "",
            href = 'JR_BKMRK_0_' + (bookmark.page - 1) + '_' + bookmark.elementAddress,
            label = bookmark.anchor || href;

        if (bookmark.bookmarks && bookmark.bookmarks.length) {
            html += "<li class='node open subtree'><p class='wrap button'><b class='icon'></b><a href='" + href + "' data-pageindex='" + bookmark.page + "' class='jrbookmark'>" + label + "</a></p>";
            html += "<ul class='list collapsible'>";
            $.each(bookmark.bookmarks, function(i, bkmk) {
                html += it.exportBookmark(bkmk);
            });
            html += "</ul>";
        } else {
            html += "<li class='leaf'><p class='wrap button'><b class='icon noninteractive'></b><a href='" + href + "' data-pageindex='" + bookmark.page + "' class='jrbookmark'>" + label + "</a></p>";
        }

        html += "</li>";

        return html;
    },
    cleanupBookmarks: function() {
        this.bookmarksContainer && this.bookmarksContainer.find('.body').empty();
    },

    // report parts
    prepareReportParts: function(parts, forceRedraw) {
        var it = this,
            partsContainer = $('ul#reportPartsContainer'),
            html = "";

        // if parts already in place, do nothing
        if (partsContainer.length && partsContainer.find('div.reportPart').length > 0 && !forceRedraw) {
            return;
        }

        if (!it.initializedPartsContainer) {
            if (partsContainer.find("li.control.search").length === 2) {
                it.tabs.navButtons = partsContainer.html();
            }

            partsContainer.parent().removeClass('hidden');
            partsContainer.on('click', 'div.reportPart', function(evt) {
                evt.preventDefault();
                var tab = $(this),
                    pageIndex = tab.data('pageindex');

                if (it._reportInstance.currentpage !== pageIndex) {
                    partsContainer.find('div.reportPart.active').removeClass('active');
                    tab.addClass('active');
                    it.goToPage(pageIndex);
                }
            });

            partsContainer.on('click', 'button#part_prev', function(evt) {
                var activeTab = partsContainer.find('li div.reportPart.active').closest('li'),
                    // prevPart,
                    prevTab = activeTab.prev('li').not('li.control.search');
                $(this).prop('disabled', true);
                prevTab.find(".button").trigger('click');
            });

            partsContainer.on('click', 'button#part_next', function(evt) {
                var activeTab = partsContainer.find('li div.reportPart.active').closest('li'),
                    // nextPart,
                    nextTab = activeTab.next('li').not('li.control.search');
                $(this).prop('disabled', true);
                nextTab.find(".button").trigger('click');
            });

            it.reportPartsContainer = partsContainer;
            it.initializedPartsContainer = true;
        }
        partsContainer.empty();
        it.partsStartIndex = [];

        $.each(parts, function(i, part) {
            if (i < it.tabs.maxCount) {
                html += it.exportPart(part);
            }
            it.partsStartIndex.push(part.page);
        });

        html += it.tabs.navButtons;
        partsContainer.html(html);
    },
    exportPart: function(part) {
        var it = this,
            name = part.name;

        if (part.name.length > it.tabs.maxLabelLength) {
            name = part.name.substring(0, it.tabs.maxLabelLength) + "...";
        }

        return "<li class='leaf'>" +
            "<div class='button reportPart' role='button' js-navtype='button' data-pageindex='"
                + part.page + "' data-title='true' aria-label='" + name + "' tabindex='-1'><span>" + name + "</span></div></li>";
    },
    getNextPart: function(tab) {
        var it = this,
            parts = it.getReportParts(),
            indexOfTab = it.partsStartIndex.indexOf(tab.data('pageindex'));

        return parts[indexOfTab + 1];
    },
    getPreviousPart: function(tab) {
        var it = this,
            parts = it.getReportParts(),
            indexOfTab = it.partsStartIndex.indexOf(tab.data('pageindex'));

        return parts[indexOfTab - 1];
    },
    getReportParts: function() {
        var it = this,
            parts = it._reportInstance.components.reportparts[0].config.parts;

        if (it._reportInstance.reportComponents && it._reportInstance.reportComponents.reportparts
            && it._reportInstance.reportComponents.reportparts[0].config.parts.length > parts.length) {
            parts = it._reportInstance.reportComponents.reportparts[0].config.parts;
        }

        return parts;
    },
    markActiveReportPart: function() {
        var it = this,
            activePartIndex = -1,
            activePartStartIndex = -1,
            btnPrev,
            btnNext,
            tabToActivate,
            parts,
            html,
            maxParts = it.tabs.maxCount;

        if (!it.reportPartsContainer) {
            return;
        }

        $.each(it.partsStartIndex, function (i, sIndex) {
            if (it.reportStatus.pages.current >= sIndex) {
                activePartIndex = i;
                activePartStartIndex = sIndex;
            }
        });

        if (activePartIndex !== -1) {
            it.reportPartsContainer.find('div.reportPart.active').removeClass('active');
            tabToActivate = it.reportPartsContainer.find("div.reportPart[data-pageindex='"+ activePartStartIndex + "']");

            if (!tabToActivate.length) {
                it.reportPartsContainer.empty();

                parts = it.getReportParts();
                html = "";

                var i = 0, j, min, partsForTabs;
                if (activePartIndex !== 0) {
                    i = parseInt(parseInt(activePartIndex/maxParts) * maxParts);
                    partsForTabs = parts.slice(i, i + maxParts); // try to select maxParts parts

                    // if we don't have enough parts try to lower the index
                    if (partsForTabs.length < maxParts && i - (maxParts - partsForTabs.length) > 0) {
                        i = i - (maxParts - partsForTabs.length);
                    }
                }

                min = Math.min(parts.length, i + maxParts);
                for (j = i; j < min; j++) {
                    html += it.exportPart(parts[j]);
                }

                html += it.tabs.navButtons;
                it.reportPartsContainer.html(html);
                tabToActivate = it.reportPartsContainer.find("div.reportPart[data-pageindex='"+ activePartStartIndex + "']");
            }

            tabToActivate.addClass('active');

            btnPrev = it.reportPartsContainer.find('#part_prev');
            btnNext = it.reportPartsContainer.find('#part_next');

            if (activePartIndex === 0) {
                btnPrev.prop('disabled', true);
                if(it.reportPartsContainer.find("li.search:first").is(".subfocus")){
                    stdnav.forceFocus("#reportPartsContainer li.search:last");
                }
            } else {
                btnPrev.prop('disabled', false);
            }

            if (activePartIndex < it.partsStartIndex.length - 1) {
                btnNext.prop('disabled', false);
            } else {
                btnNext.prop('disabled', true);
                if(it.reportPartsContainer.find("li.search:last").is(".subfocus")){
                    stdnav.forceFocus("#reportPartsContainer li.search:first");
                }
            }
        }
    },
    cleanupParts: function() {
        this.reportPartsContainer && this.reportPartsContainer.find('.body').empty();
        this.partsStartIndex = [];
    },

    // search
    enableSearchButtons: function() {
        $('button#search_next').prop('disabled', false);
        $('button#search_previous').prop('disabled', false);
    },
    disableSearchButtons: function() {
        $('button#search_next').prop('disabled', true);
        $('button#search_previous').prop('disabled', true);
    },
    resetSearch: function(bKeepSearchText) {
        var it = this;
        it.search = {
            currentIndex: 0,
            results: []
        };

        !bKeepSearchText && $('input#search_report').val('');
        $('button#search_next').prop('disabled', true);
        $('button#search_previous').prop('disabled', true);

    },
    resetReportStatus: function() {
        this.reportStatus = $.extend(true, {}, defaultReportStatus);
    },
    restoreLastActiveSearchHighlight: function(reportDom) {
        var it = this, spans;
        if (it.search.results.length && it.reportStatus.pages.current === it.search.currentPage) {
            spans = $(reportDom).find(".jr_search_result");
            spans.eq(it.search.currentIndex).addClass('highlight');
        }
    },

    getOptionsMenu: function(feature) {
        var it = this,
            featureConfig = it.features[feature],
            options = featureConfig.options,
            menu = $("div#templateElements > div#reportViewerMenuHolder > div#vwroptions > div.menu"),
            optionsContainer, i, opt, buf, once = false;

        if (menu.length === 0) {
            buf = "<div id ='reportViewerMenuHolder'><div id='vwroptions'><div class='menu vertical dropDown fitable' style='display: none; z-index: 99999'><div class='content'><ul js-navtype='toolbar'>";
            buf += "</ul></div></div></div></div>";
            menu = $("div#templateElements").append(buf).find('div#vwroptions div.menu');
            menu.on('mouseleave', function(evt) {
                $(this).hide();
            });
        }

        optionsContainer = menu.find('ul');

        // cleanup
        optionsContainer.empty();
        menu.off('click', 'p');

        for (i = 0; i < options.length; i++) {
            opt = options[i];
            if (opt.key.startsWith('fit_') && !once) {
                once = true;
                optionsContainer.append("<li class='leaf separator'></li>");
            }
            if (featureConfig.selectedKeys.length && $.inArray(opt.key, featureConfig.selectedKeys) !== -1) {
                optionsContainer.append("<li class='leaf selected'><p class='wrap toggle button down' data-val='" + opt.key + "'><span class='icon'></span>" + opt.value + "</p></li>");
            } else {
                optionsContainer.append("<li class='leaf'><p class='wrap toggle button' data-val='" + opt.key + "'><span class='icon'></span>" + opt.value + "</p></li>");
            }
        }

        menu.on('click', 'p', function(evt) {
            var p = $(this),
                val = p.attr('data-val');

            if (featureConfig.optionType === 'select') {
                p.closest('ul').find('p').removeClass('down');
                p.closest('ul').find('li').removeClass('selected');
                p.addClass('down');
                p.closest('li').addClass('selected');
            } else if (featureConfig.optionType === 'toggle') {
                p.toggleClass('down');
                p.closest('li').toggleClass('selected');
            }

            featureConfig.onClick.apply(null, [val]);
            menu.hide();
        });

        return menu;
    },
    updateZoomValues: function(scale, scaleFactor) {
        $('#zoom_value').val((scaleFactor*100).toFixed(0) + '%');

        var literalValue = null;
        // when report is first loaded, the scaleStrategy is JR strategy
        if (_.contains(_.keys(jrScaleStrategiesMapping), scale)) {
            literalValue = scale;
        }
        // when zooming locally, scaleStrategy is FAF strategy
        if (!literalValue) {
            literalValue = _.findKey(jrScaleStrategiesMapping, function(val) { return val === scale });
        }

        this.features.zoom.levels.current.value = scaleFactor;
        this.features.zoom.levels.current.literal = literalValue ? literalValue : null;

        if (literalValue) {
            if (literalValue === 'fit_actual') {
                this.features.zoom.selectedKeys = ['1', 'fit_actual'];
            } else {
                this.features.zoom.selectedKeys = [literalValue];
            }
        } else {
            if (scaleFactor === 1) {
                this.features.zoom.selectedKeys = ['1', 'fit_actual'];
            } else {
                this.features.zoom.selectedKeys = ['' + scaleFactor];
            }
        }
    },
    processZoomOption: function(val) {
        var scale;

        if (_.contains(_.keys(jrScaleStrategiesMapping), val)) {
            scale = jrScaleStrategiesMapping[val];
            this.features.zoom.levels.current.literal = val;
            // this.features.zoom.levels.current.value = null;
        } else {
            scale = parseFloat(val);
            this.features.zoom.levels.current.literal = null;
            this.features.zoom.levels.current.value = scale;
        }

        if (val === 'fit_actual' || val === '1') {
            this.features.zoom.selectedKeys = ['1', 'fit_actual'];
        } else {
            this.features.zoom.selectedKeys = [val];
        }

        this.zoomChanged(scale);
    },
    zoomIn: function() {
        var currentZoom = this.features.zoom.levels.current.value,
            amount = 0.1,
            newZoomLevel;

        newZoomLevel = parseFloat((currentZoom + amount).toFixed(2));

        if (newZoomLevel === 1) {
            this.features.zoom.selectedKeys = ['1', 'fit_actual'];
        } else {
            this.features.zoom.selectedKeys = ['' + newZoomLevel];
        }

        this.features.zoom.levels.current.literal = null;
        this.features.zoom.levels.current.value = newZoomLevel;

        this.zoomChanged(newZoomLevel);
    },
    zoomOut: function() {
        var currentZoom = this.features.zoom.levels.current.value,
            amount = 0.1,
            newZoomLevel;

        newZoomLevel = parseFloat((currentZoom - amount).toFixed(2));
        if (newZoomLevel <= 0) {
            newZoomLevel = 0.1;
        }

        if (newZoomLevel === 1) {
            this.features.zoom.selectedKeys = ['1', 'fit_actual'];
        } else {
            this.features.zoom.selectedKeys = ['' + newZoomLevel];
        }

        this.features.zoom.levels.current.literal = null;
        this.features.zoom.levels.current.value = newZoomLevel;

        this.zoomChanged(newZoomLevel);
    },
    zoomTo: function(zoomLevel) {
        this.features.zoom.levels.current.literal = null;
        this.features.zoom.levels.current.value = zoomLevel;

        if (zoomLevel === 1) {
            this.features.zoom.selectedKeys = ['1', 'fit_actual'];
        } else {
            this.features.zoom.selectedKeys = ['' + zoomLevel];
        }

        this.zoomChanged(zoomLevel);
    },
    saveCurrentLevel: function() {
        this.features.zoom.levels.previous.value = this.features.zoom.levels.current.value;
        this.features.zoom.levels.previous.literal = this.features.zoom.levels.current.literal;
    },
    zoomChanged: function(newScale) {
        var self = this,
            currentLevel = this.features.zoom.levels.current,
            prevLevel = this.features.zoom.levels.previous;

        if (currentLevel.value !== prevLevel.value || currentLevel.literal !== prevLevel.literal) {
            this._reportInstance.runZoomAction({
                zoomValue: currentLevel.literal || currentLevel.value
            }).then(function() {
                self._reportInstance
                    .scale(newScale)
                    .resize()
                    .done(function(scaleFactor) {
                        self.features.zoom.levels.current.value = scaleFactor;
                        $('#zoom_value').val((scaleFactor*100).toFixed(0) + '%');
                    });
            });
        }
    },
    onWindowResizeHandler: function() {
        var currentLevel = this.features.zoom.levels.current;

        if (currentLevel.literal) {
            var scale = currentLevel.literal;

            // switch to bi-report scale strategy
            if (_.contains(_.keys(jrScaleStrategiesMapping), scale)) {
                scale = jrScaleStrategiesMapping[scale];
            }

            this._reportInstance
                .scale(scale)
                .resize()
                .done(function(scaleFactor) {
                    $('#zoom_value').val((scaleFactor*100).toFixed(0) + '%');
                });
        }
    },
    scrollElementIntoView: function(elem) {
        elem && elem.scrollIntoView(false);
    }
};

Viewer.publicMethods = {
    loadReport: function(params, onReportCompleted) {
        var self = this;

        var pagesOption = {};
        if (this.config.anchor) {
            pagesOption.anchor = this.config.anchor;
        }
        if (this.config.page) {
            pagesOption.pages = this.config.page;
        }
        if(_.isEmpty(pagesOption)) {
            pagesOption.pages = 1;
        }

        this._reportInstance = new Report({
            server: this.config.contextPath,
            container: this.config.at,
            centerReport: true,
            useReportZoom: true,
            loadingOverlay: false,
            modalDialogs: false,
            resource: this.config.reporturi,
            autoresize: false,
            params: params || {},
            pages: pagesOption,
            reportContainerWidth: this.config.at ? $(this.config.at).width() : null,
            defaultJiveUi: {
                enabled: true,
                floatingTableHeadersEnabled: true,
                floatingCrosstabHeadersEnabled: true
            },
            events: {
                beforeAction: function(actions, shouldCancel) {
                    let canceled = false;
                    if (self.isExportRunning()) {
                        // If action is not search or zoom, show the confirmation dialog
                        if (!_.intersection(["search", "saveZoom"], actions).length) {
                            self.confirmedExportCancel = confirm(i18n["jasper.report.view.export.in.progress.confirm.continue"]);
                            if (!self.confirmedExportCancel) {
                                shouldCancel.cancel = true;
                                canceled = true;
                            }
                        }
                    }
                    if (!canceled) {
                        // reset scrollbars
                        var scrollContainer = $('div#reportViewFrame .body');
                        scrollContainer.scrollLeft(0);
                        scrollContainer.scrollTop(0);

                        if (!_.intersection(["search", "saveReport", "saveZoom"], actions).length) {
                            self.cleanupUi();
                            self.resetReportStatus();
                        }

                        // Show the loading dialog for all actions except saveReport and saveZoom
                        if (!_.intersection(["saveReport", "saveZoom"], actions).length) {
                            ReportViewRuntime.showAjaxDialog();
                        }

                        self.features.zoom.localZoomChange = false;
                    }
                },
                afterAction: function(actions) {
                    if (!_.intersection(["saveReport", "saveZoom"], actions).length) {
                        ReportViewRuntime.hideAjaxDialog();
                    }
                },
                beforeRender: function(el, isElasticChart) {
                    self.applyFixes(el);
                    if (isElasticChart) {
                        $(el).parent().css({
                            height: "100%"
                        });
                    } else {
                        self.restoreLastActiveSearchHighlight(el);
                    }
                },
                afterRender: function(info) {
                    self.updateZoomValues(info.scale, info.scaleFactor);
                    ReportViewRuntime.hideAjaxDialog();
                    ReportViewRuntime.reportRefreshed();
                },
                responsiveBreakpointChanged: function(error) {
                    if (error) {
                        self.errorHandler(error);
                    } else {
                        // reset undo/redo stack after resize to trigger reset for undo/redo/undoAll buttons
                        self._reportInstance.resetUndoRedoStack();
                        ReportViewRuntime.savedState = self.getReportStackState();

                        self.reset();
                    }
                },
                reportCompleted: function(status, error) {
                    switch(status) {
                    case reportStatuses.READY:
                        self.markActiveReportPart();
                        ReportViewRuntime.reportRefreshed();
                        break;
                    case reportStatuses.EMPTY:
                        self.reportStatus.pages.total = 0;
                        ReportViewRuntime.reportRefreshed(true);
                        break;
                    case reportStatuses.FAILED:
                        ReportViewRuntime.reportRefreshed(true);
                        if(error && error.errorDescriptor) {
                            self.errorHandler(error.errorDescriptor);
                        }
                        break;
                    }
                    onReportCompleted && onReportCompleted();
                },
                changeTotalPages: function(totalPages) {
                    self.reportStatus.pages.total = totalPages;
                    ReportViewRuntime.refreshPagination();
                },
                changeLastPartialPage: function(lastPartialPage) {
                    self.reportStatus.pages.lastPartialPage = lastPartialPage;
                },
                changePageMeta: function(pageMeta) {
                    self.reportStatus.pages.dataTimestampMessage = pageMeta.dataTimestampMessage;
                },
                changeSnapshotSaveStatus: function(snapshotSaveStatus) {
                    self.reportStatus.snapshotSaveStatus = snapshotSaveStatus;
                },
                changePagesState: function(currentPage) {
                    self.reportStatus.pages.current = currentPage;
                },
                canUndo: function(canUndo) {
                    if(canUndo) {
                        buttonManager.enable($('#undo')[0]);
                        buttonManager.enable($('#undoAll')[0]);
                    } else {
                        buttonManager.disable($('#undo')[0]);
                        buttonManager.disable($('#undoAll')[0]);
                    }
                },
                canRedo: function(canRedo) {
                    if(canRedo) {
                        buttonManager.enable($('#redo')[0]);
                    } else {
                        buttonManager.disable($('#redo')[0]);
                    }
                },
                canSave: function(canSave) {
                    self.canSave = canSave;
                    ReportViewRuntime.refreshSave();
                },
                bookmarksReady: function(bookmarks) {
                    var parentLi = $('button#bookmarksDialog').closest("li");

                    if (bookmarks && bookmarks.length) {
                        self.prepareBookmarks(bookmarks);
                        parentLi.removeClass('hidden').prev('li.divider').removeClass('hidden');
                    } else {
                        parentLi.addClass('hidden').prev("li.divider").addClass('hidden');
                        self.bookmarksContainer && self.bookmarksContainer.hide();
                    }
                },
                reportPartsReady: function(reportparts) {
                    if (reportparts && reportparts.length) {
                        var forceRedraw = true;

                        // check if we have a different number of parts than existing
                        if (self.partsStartIndex && self.partsStartIndex.length === reportparts.length) {
                            forceRedraw = false;
                        }

                        self.prepareReportParts(reportparts, forceRedraw);
                        self.reportPartsContainer && self.reportPartsContainer.removeClass('hidden');
                    }
                },
                htmlReady: function() {
                    ReportViewRuntime.refreshAsyncCancel();
                },
                error: self.errorHandler.bind(self)
            },
            linkOptions: {
                beforeRender: function(elementDataPairs) {
                    elementDataPairs.forEach(function(elemData) {
                        elemData.element && (elemData.element.style.cursor = "pointer");
                    });
                },
                events: {
                    "click": function(event, link) {
                        switch(link.type) {
                        case hyperlinkTypes.LOCAL_ANCHOR:
                        case hyperlinkTypes.LOCAL_PAGE:
                            var pagesOption = {};
                            if (link.anchor) {
                                pagesOption.anchor = link.anchor;
                            }
                            if (link.pages) {
                                pagesOption.pages = link.pages;
                            }
                            self._reportInstance.pages(pagesOption).run().fail(self.errorHandler.bind(self));
                            break;

                        case hyperlinkTypes.REMOTE_ANCHOR:
                        case hyperlinkTypes.REFERENCE:
                            window.open(link.href, link.targetValue);
                            break;

                        case hyperlinkTypes.REPORT_EXECUTION:
                            var drilldownHref = link.href;

                            // add flow specific params for allowing to go back
                            if (drilldownHref.indexOf("_flowExecutionKey") == -1) {
                                drilldownHref += "&_eventId_drillReport=";
                                drilldownHref += "&_flowExecutionKey=" + ReportViewRuntime.reportExecutionKey(self.config.reporturi);
                            }

                            window.open(drilldownHref, link.targetValue);
                            break;
                        }
                    }
                }
            }
        });

        var deferred = this._reportInstance.run();
        deferred.done(function() {
            self.boundedOnWindowResizeHandler = _.bind(self.onWindowResizeHandler, self);
            $(window).on("resize", self.boundedOnWindowResizeHandler);
        }).fail(this.errorHandler.bind(this));

        return deferred;
    },
    reset: function(options) {
        var previousParams;

        $.extend(this.config, options);
        this.cleanupUi();
        this.resetReportStatus();
        ReportViewRuntime.isLoaded = false;

        if (this._reportInstance !== null) {
            previousParams = this._reportInstance.params();
        }

        this._reportInstance.destroy();
        this.loadReport(previousParams, _.once(function() {
            ReportViewRuntime.isLoaded = true;
        }));
    },
    applyFixes: function(el) {
        // Due to the html markup of the AdHoc Chart, there is an issue when the
        // AdHoc Chart Report is rendering and there is an TopOfThePage Control there:
        // they interfere with each other because AdHoc uses absolute positioning to get as much space as it needs,
        // and this prevents controls from being visible.
        // To fix this issue, we need to move the controls inside the AdHoc report area (which uses the absolute positioning).
        var topOfThePageControls = $("#inputControlsForm.topOfPage");
        if (topOfThePageControls.length && !window.Report.hasError) {
            // detach controls at first
            topOfThePageControls.detach();
        }

        if (topOfThePageControls.length && !window.Report.hasError) {
            // and now, get controls back to the right place (just before any elements in the report area)
            topOfThePageControls.insertBefore($("#reportContainer").children()[0]);
        }
        if(window.Report.hasError) {
            window.Report.hasError = false;
        }

        // Give the table a tabindex so that Standard Navigation can work with it.
        $(el).find('table.jrPage').prop('tabindex', '8');
        // Prevent screen-readers from guessing that our table is used only for layout purposes,
        // because of all the blank cells at the edges
        $(el).find('table.jrPage').attr('role', 'grid');
        $(el).find('table.jrPage tr').attr('role', 'row');
        $(el).find('table.jrPage tbody tr').attr('role', 'row');
        $(el).find('table.jrPage tr th').attr('role', 'columnheader');
        $(el).find('table.jrPage tbody tr th').attr('role', 'columnheader');
        $(el).find('table.jrPage tr td').attr('role', 'gridcell');
        $(el).find('table.jrPage tbody tr td').attr('role', 'gridcell');
    },
    goToPage: function(page) {
        this.reportStatus.pages.current = page;
        ReportViewRuntime.refreshPagination();
        return this._reportInstance.pages(page).run().fail(this.errorHandler.bind(this));
    },
    hasReport: function() {
        return this._reportInstance !== null;
    },
    cancelReport: function() {
        return this._reportInstance.cancel();
    },
    isReportRunning: function() {
        var currentStatus = this._reportInstance.status(),
            outputFinal = this._reportInstance.htmlExportFinal(),
            isQueuedOrInExecution,
            isFailedOrEmpty;

        if (!currentStatus) {
            return true;
        } else {
            isQueuedOrInExecution = _.contains([reportStatuses.QUEUED, reportStatuses.EXECUTION], currentStatus),
            isFailedOrEmpty = _.contains([reportStatuses.FAILED, reportStatuses.EMPTY], currentStatus);

            return isQueuedOrInExecution || (!isFailedOrEmpty && outputFinal === false);
        }
    },
    isExportRunning: function() {
        return this.reportStatus.isExportRunning;
    },
    exportReport: function(outputFormat) {
        var self = this,
            exportOptions;

        if (outputFormat.endsWith("NoPag")) {
            exportOptions = {
                outputFormat: outputFormat.substring(0, outputFormat.indexOf("NoPag")),
                ignorePagination: true
            }
        } else {
            exportOptions = {
                outputFormat: outputFormat
            }
        }

        this.reportStatus.isExportRunning = true;

        return this._reportInstance
            .export(exportOptions)
            .always(function() {
                self.reportStatus.isExportRunning = false;
            })
            .fail(this.errorHandler.bind(this));
    },
    undo: function() {
        return this._reportInstance.undo();
    },
    redo: function() {
        return this._reportInstance.redo();
    },
    undoAll: function() {
        return this._reportInstance.undoAll();
    },
    cancelAsync: function() {
        return this._reportInstance.cancelAsync();
    },
    getReportStatus: function() {
        return this._reportInstance.status();
    },
    getReportStackState: function() {
        return this._reportInstance._getStackState();
    },
    saveReport: function(options) {
        return this._reportInstance.save(options);
    },
    cleanupUi: function() {
        this.resetSearch();
        this.cleanupBookmarks();
        this.cleanupParts();
    },
    refreshPage(page, params, freshData) {
        var reportParams = params || {},
            report = this._reportInstance.params(reportParams);

        report._clearExportContextCache();
        this.cleanupUi();

        if (freshData) {
            return report.refresh()
                .fail(this.errorHandler.bind(this));
        }

        return report.run()
            .fail(this.errorHandler.bind(this));
    },
    exit: function() {
        var dfd = new $.Deferred();
        if (this.boundedOnWindowResizeHandler) {
            $(window).off("resize", this.boundedOnWindowResizeHandler);
        }
        dfd.resolve();
        return dfd;
    },
    errorHandler: function(err) {
        ReportViewRuntime.hideAjaxDialog();
        ReportViewRuntime.reportRefreshed(true);
        if (err.status !== "cancelled" && !(this.confirmedExportCancel && err.message === "export.execution.rejected")) {
            dialogs.errorPopup.show(_.template(errorDialogTemplate, {
                i18n: i18n,
                errorMessage: err && err.message ? err.message : err,
                errorTrace: err && err.parameters && err.parameters.length ? err.parameters[0] : null
            }));
        }
        this.confirmedExportCancel = false;
    }
};

$.extend(Viewer.prototype, Viewer.privateMethods);
$.extend(Viewer.prototype, Viewer.publicMethods);

export default Viewer;
