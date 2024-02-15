/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @version $Id: jasperreports-viewer.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jasperreports-loader", "jasperreports-report", "jquery.ui", "jasperreports-url-manager"], function(Loader, Report, $, UrlManager) {
    var Viewer = function(o) {
        this.config = {
            at: null,
            reporturi: null,
            async: true,
            page: 0,
            toolbar: true,
            applicationContextPath: null
        };

        $.extend(this.config, o);

        this.config.applicationContextPath && (UrlManager.applicationContextPath = this.config.applicationContextPath);

        this.reportInstance = null;
        this.container = null;
        this.undoRedoCounters = {
            undos: 0,
            redos: 0
        };
    };

    Loader.prototype._errHandler = function(jqXHR, textStatus, errorThrown) {
        var jsonMsg = $.parseJSON(jqXHR.responseText),
            errDialogId = 'errDialog',
            errDialog = $('#' + errDialogId),
            msg;
        if (errDialog.size != 1) {
            errDialog = $("<div id='" + errDialogId + "'></div>");
            $('body').append(errDialog);
        }
        msg = "<p>" + jsonMsg.msg + "</p>";
        if(jsonMsg.devmsg) {
            msg += "<p>" + jsonMsg.devmsg + "</p>";
        }
        errDialog.html(msg);
        errDialog.dialog({
            title: 'Error',
            width: 530,
            height: 200
        });
    };

    Viewer.prototype = {
        loadReport: function() {
            var it = this;

            it.reportInstance = new Report({
                reporturi: it.config.reporturi,
                async: it.config.async,
                page: it.config.page,
                container: it._getContainer()
            });

            it._setupEventsForReport(it.reportInstance);

            return it.reportInstance.init();
        },

        // internal functions
        _render: function(htmlOutput) {
            // place output into container
            this._getContainer().html(htmlOutput);
        },
        _getContainer: function() {
            if (!this.container) {
                var sel = this.config.at;
                this.container = $(sel);
                if (!this.container.length) {
                    this.container = $('#' + sel)
                    if (!this.container.length) {
                        this.container = $('.' + sel);
                    }
                }

                this.container.addClass('_jr_report_container_');
            }
            return this.container;
        },
        _setupEventsForReport: function(report) {
            var it = this,
                toolbar = $("#toolbar");

            report.on("reportHtmlReady", function() {
                it._render(this.html);
                it._updateToolbarPaginationButtons(toolbar);
            }).on("action", function() {
                    this.gotoPage(0);
                    it.undoRedoCounters.undos++;
                    it.undoRedoCounters.redos = 0;
                    it._updateUndoRedoButtons(toolbar);
                }).on("beforeAction", function() {
                    this.cancelStatusUpdates();
                }).on("undo", function() {
                    this.gotoPage(0);
                    it.undoRedoCounters.redos ++;
                    it.undoRedoCounters.undos --;
                    if (it.undoRedoCounters.undos <= 0) {
                        it.undoRedoCounters.undos = 0;
                    }
                    it._updateUndoRedoButtons(toolbar);
                }).on("redo", function() {
                    this.gotoPage(0);
                    it.undoRedoCounters.undos ++;
                    it.undoRedoCounters.redos --;
                    if (it.undoRedoCounters.redos <= 0) {
                        it.undoRedoCounters.redos = 0;
                    }
                    it._updateUndoRedoButtons(toolbar);
                }).on("search", function(data) {
                    if (data.actionResult.searchResults && data.actionResult.searchResults.length) {
                        var results = data.actionResult.searchResults;

                        results.sort(function(r1, r2) {
                            return r1.page - r2.page;
                        });

                        this.gotoPage(results[0].page).then(function() {
                            $('.jr_search_result').addClass('highlight');
                        });

                    } else if (data.actionResult.searchString) {
                        alert("No results for: " + data.actionResult.searchString);
                    }
                }).on("pageModified", function() {
                    this.refreshPage(this.currentpage);
                }).on("reportFinished", function() {
                    if (!this.status.pageFinal) {
                        this.refreshPage(this.currentpage);
                    } else {
                        it._updateToolbarPaginationButtons(toolbar);
                    }
                }).on("componentsRegistered", function() {
                    var components = it.reportInstance.components,
                        uimodules = [],
                        uimodule;

                    $.each(components, function(i, componentArray) {
                        if (componentArray.length > 0) {
                            uimodule = componentArray[0].config.uimodule;
                            if (uimodule) {
                                uimodules.push(uimodule);
                            }
                        }
                    });

                    if(uimodules.length) {
                        require(uimodules, function() {
                            $.each(arguments, function(i, thisModule) {
                                thisModule.init(it.reportInstance);
                            });
                        });
                    }

                    /*
                     If Highcharts are present render them  // FIXMEJIVE: should revert back to components being able to render themselves
                     */
                    if(components.chart) {
                        $.each(components.chart, function(){
                            var el = $('#'+this.config.hcinstancedata.renderto).length;
                            el && this.render();
                        });
                    }

                });

            toolbar.on("click", function(evt) {
                var target = $(evt.target);

                if (target.is('.disabledViewerButton')) {
                    // do nothing
                    return;
                }

                if (target.is('.pageNext')) {
                    report.gotoPage(parseInt(report.currentpage) + 1);
                } else if (target.is('.pagePrevious')) {
                    report.gotoPage(parseInt(report.currentpage) - 1);
                } else if (target.is('.pageFirst')) {
                    report.gotoPage(0);
                } else if (target.is('.pageLast')) {
                    report.gotoPage(report.status.totalPages - 1);
                } else if (target.is('.undo')) {
                    report.undo();
                } else if (target.is('.redo')) {
                    report.redo();
                }
            });
        },
        _updateUndoRedoButtons: function(jqToolbar) {
            var it = this,
                utils = it._toolbarUtils,
                counters = it.undoRedoCounters,
                btnUndo = $('.undo', jqToolbar),
                btnRedo = $('.redo', jqToolbar);

            // undo
            if (counters.undos > 0) {
                utils.enableElem(btnUndo);
            } else {
                utils.disableElem(btnUndo);
            }
            // redo
            if (counters.redos > 0) {
                utils.enableElem(btnRedo);
            } else {
                utils.disableElem(btnRedo);
            }
        },
        _updateToolbarPaginationButtons: function (jqToolbar) {
            var it = this,
                currentPage = it.reportInstance.currentpage,
                totalPages = it.reportInstance.status.totalPages,
                pageFirst = $('.pageFirst', jqToolbar),
                pagePrevious = $('.pagePrevious', jqToolbar),
                pageNext = $('.pageNext', jqToolbar),
                pageLast = $('.pageLast', jqToolbar),
                undo = $('.undo', jqToolbar),
                redo = $('.redo', jqToolbar),
                utils = it._toolbarUtils,
                classEnabled = utils.getClassEnabled(),
                classDisabled = utils.getClassDisabled();

            if (totalPages == null) {
                utils.enableElem(pageNext);
                utils.disableElem(pageLast);
            }
            else if (totalPages > 1 && currentPage < totalPages - 1) {
                utils.enablePair(pageNext, pageLast);
            } else {
                utils.disablePair(pageNext, pageLast);
            }

            if (currentPage == 0) {
                utils.disablePair(pageFirst, pagePrevious);
            } else {
                utils.enablePair(pageFirst, pagePrevious);
            }

            if (!(undo.hasClass(classEnabled) || undo.hasClass(classDisabled))) {
                utils.disableElem(undo);
            }
            if (!(redo.hasClass(classEnabled) || redo.hasClass(classDisabled))) {
                utils.disableElem(redo);
            }
        },
        _toolbarUtils: (function() {
            var classEnabled = 'enabledViewerButton',
                classDisabled = 'disabledViewerButton';

            return {
                getClassEnabled: function() {
                    return classEnabled;
                },
                getClassDisabled: function() {
                    return classDisabled;
                },
                enableElem: function(jqElem) {
                    jqElem.removeClass(classDisabled);
                    jqElem.addClass(classEnabled);
                },
                disableElem: function(jqElem) {
                    jqElem.removeClass(classEnabled);
                    jqElem.addClass(classDisabled);
                },
                enablePair: function(jqElem1, jqElem2){
                    this.enableElem(jqElem1);
                    this.enableElem(jqElem2);
                },
                disablePair: function(jqElem1, jqElem2){
                    this.disableElem(jqElem1);
                    this.disableElem(jqElem2);
                }
            };
        }())
    };

    return Viewer;
});