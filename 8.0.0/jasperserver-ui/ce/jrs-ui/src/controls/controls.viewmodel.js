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
 * @author: afomin, inesterenko
 * @version: $Id$
 */
/* global Report */
import {JRS} from "../namespace/namespace";
import _ from 'underscore';
import jQuery from 'jquery';
import browserDetection from 'js-sdk/src/common/util/browserDetection';

import './controls.core';
import './controls.basecontrol';

import 'jquery-ui/ui/widgets/sortable';

import RestParamsEnum from './rest/enum/restParamsEnum';
import isControlInCascade from './predicate/isControlInCascade';

JRS.Controls = function (jQuery, _, Controls) {
    //module:
    //
    //  controls.viewmodel
    //
    //main types:
    //
    // ViewModel      - initialize, draw, update controls, no any communication with server
    //
    //dependencies:
    //
    //
    //  jQuery          - v1.7.1
    //  _,              - underscore.js 1.3.1
    //  Controls,       - controls.components
    var isIE = browserDetection.isIE();

    // JS-33242
    var scrollStartStopCallback = (function() {
        var lastScrollAt = (new Date()).getTime(),
            scrollTimeout;

        return function() {
            var now = (new Date()).getTime();
            var self = this;

            if (now - lastScrollAt > 400) {
                lastScrollAt = now;

                jQuery(this).trigger("scrollstart");
            }

            clearTimeout(scrollTimeout);

            scrollTimeout = setTimeout(function(){
                jQuery(self).trigger("scrollstop");
            }, 300);
        }
    })();

    // ViewModel initialize, updates and adds controls to the DOM
    var ViewModel = Controls.Base.extend({

        constructor:function (args) {
            this.containerSelector = (args && args.containerSelector) || Controls.ViewModel.DEFAULT_CONTAINER;
            this.controls = {};

            //save reference to initialized manager object
            Controls.getViewModel = _.bind(function () {
                return this;
            }, this);

            _.bindAll(this,
                "registerEvents",
                "onControlChange"
            );

            this.registerEvents();
        },

        registerEvents : function() {
            Controls.ignore("changed:control", this.onControlChange);
            Controls.listen({
                "changed:control":this.onControlChange
            });
        },

        /* Listens for controls change and triggers controls update */
        onControlChange:function (event, options) {
            const {
                control,
                selection
            } = options;

            const controlInCascade = isControlInCascade(control);

            jQuery(document).trigger(Controls.ViewModel.CHANGE_SELECTION, [
                control.id,
                selection,
                controlInCascade
            ]);

            if (!controlInCascade) {
                //TODO: update state before sending it to subscribers
                jQuery(document).trigger(Controls.ViewModel.CHANGE_VALUES, [this.state]);
            }
        }
    }, {
        //Static props
        DEFAULT_CONTAINER: '#inputControlsContainer',
        CHANGE_VALUES: 'viewmodel:values:changed',
        CHANGE_SELECTION: 'viewmodel:selection:changed',
        CHANGE_ORDER: 'viewmodel:order:changed',
        //check for changes between two objects
        isSelectionChanged: function (previous, next) {
            var previousControlIds = previous ? _(previous).keys() : [];
            var nextControlIds = next ? _(next).keys() : [];
            var controlsIdUnion = _.union(previousControlIds, nextControlIds);
            if (controlsIdUnion.length === previousControlIds.length) {
                var previousFlattenValues = previous ? _.flatten(_(previous).values()) : [];
                var nextFlattenValues = next ? _.flatten(_(next).values()) : [];
                return _.difference(previousFlattenValues, nextFlattenValues).length > 0 || _.difference(nextFlattenValues, previousFlattenValues).length > 0;
            }
            return true;
        }
    });

    var Model = {

        //Get array of all instantiated controls
        getControls: function () {
            return this.controls;
        },

        // Creates instances of all controls. Not adding them to DOM
        instantiate: function (controlsStructure, options) {
            this.controls = {};

            const {
                dataUri,
                inputControlsService,
                initialSelectedValues,
                paginatedValuesOptions,
            } = options;

            controlsStructure.forEach((controlStructure) => {
                const controlId = controlStructure.id;

                this.createControl(controlStructure,{
                    dataUri,
                    inputControlsService,
                    initialSelectedValues: initialSelectedValues[controlId] || [],
                    paginatedValuesOptions: paginatedValuesOptions[controlId] || []
                });
            });
        },

        // Create control and add it to controls but do not add to the DOM
        createControl: function (controlStructure, options) {
            // Find instantiated control by it's type
            var Control = function (type) {
                for (var property in Controls) {
                    if (type.toLowerCase() === property.toLowerCase()) {
                        return Controls[property];
                    }
                }
            }(controlStructure.type);    // Is such control implementation present?

            if (Control) {
                this.controls[controlStructure.id] = new Control(controlStructure, options);
            } else {
                throw new Error('Can not find implementation of the control type: ' + controlStructure.type);
            }
            return this.controls[controlStructure.id];
        },

        // Remove control from state
        removeControl: function (controlId) {
            delete this.controls[controlId];
        },

        // reorders controls in state
        reorderControl: function (oldIndex, newIndex) {
            if (this.structure.length == 1) {
                // nothing to reorder
                return;
            }

            var shiftDirection = oldIndex < newIndex ? 1 : -1;
            var start = oldIndex < newIndex ? Math.min(oldIndex, newIndex) : Math.max(oldIndex, newIndex);
            var end = oldIndex < newIndex ? Math.max(oldIndex, newIndex) : Math.min(oldIndex, newIndex);
            var t;
            for (var i = start; i != end; i = i + shiftDirection) {
                t = this.structure[i];
                this.structure[i] = this.structure[i + shiftDirection];
                this.structure[i + shiftDirection] = t;
            }
            jQuery(document).trigger(Controls.ViewModel.CHANGE_ORDER, [this.structure]);
        },

        // Invoke specific actions according specified attributes
        set: function (attributes, preventNotification) {
            if (attributes) {
                _.extend(this, attributes);

                const {
                    structure,
                } = attributes;

                const {
                    initialSelectedValues,
                } = attributes.controlsOptions || {};

                if (structure) {
                    this.instantiate(structure, attributes.controlsOptions);
                    this.draw(structure);
                }

                if (initialSelectedValues) {
                    this.update(initialSelectedValues);

                    !preventNotification && jQuery(document)
                        .trigger(Controls.ViewModel.CHANGE_VALUES, initialSelectedValues);
                }
            }
        },
        get: function (attribute) {
            if (!attribute) {
                return;
            }
            if (attribute === 'selection') {
                var result = {};
                _.each(this.getControls(), function (control) {
                    var selectedData = control.get('selection');    // Should be array of values, so check it.
                    // Also check if value is undefined then do not pass it to the result array.
                    // Should be array of values, so check it.
                    // Also check if value is undefined then do not pass it to the result array.
                    if (selectedData instanceof Array) {
                        result[control.id] = result[control.id] = selectedData.length > 0 ? selectedData : control.mandatory ? [] : [RestParamsEnum.NOTHING_SUBSTITUTION_VALUE];
                    } else if (!_.isUndefined(selectedData)) {
                        result[control.id] = [selectedData];
                    }

                });
                return result;
            }
            return this[attribute];
        },
        // Find control by specified attribute
        findControl: function (attribute) {
            var key = _.keys(attribute)[0];
            var value = _.values(attribute)[0];
            return _.find(this.getControls(), function (control) {
                return control[key] === value;
            });
        },

        // Returns specified properties from all controls
        pluck: function (property) {
            return _.pluck(this.getControls(), property);
        }
    };

    var View = {
        // Draw structure of all controls. Put them to already selected container
        draw: function (jsonStructure) {
            const container = this.getContainer();

            container.empty();

            if (isIE) {
                // https://jira.tibco.com/browse/JS-32279 - Scroll jumps up on any click inside control aria (item select, title, tab)
                // Solution is to skip stdnav handling on root container of this component.
                container.attr('js-stdnav', 'false');
                container.attr('js-navtype', 'none');
            }

            jsonStructure.forEach((controlJson) => {
                if (controlJson.visible) {
                    const control = this.findControl({ id: controlJson.id });

                    control.render();

                    container.append(control.getElem());
                }
            });

            if (window.Report && window.Report.icReorderEnabled) {
                const horizontalLayout = window.Controls.layouts.LAYOUT_TOP_OF_PAGE === Report.reportControlsLayout;

                if(!(/Android|webOS|iPhone|iPad|iPod|BlackBerry/i.test(navigator.userAgent))) {
                    this._initSortable({
                        container: container,
                        horizontalLayout: horizontalLayout
                    });
                }
            }
        },

        // Fill content of all controls
        update: function (initialSelectedValues) {
            _.each(initialSelectedValues, (controlSelection, controlId) => {
                const control = this.findControl({ id: controlId });

                control.set({
                    values: Array.isArray(controlSelection) ? controlSelection.map(option => option.value) : controlSelection,
                    error: null
                });
            });
        },

        // Getter for IC container.
        // Fetch DOM object only once.
        getContainer: function () {
            if (this.container && this.container.length == 1) {
                return this.container;
            } else {
                this.container = jQuery(this.containerSelector);
                return this.container;
            }
        },

        // Clear all warning messages
        removeValidationMessages: function () {
            _.each(this.getControls(), function (control) {
                control.set({ error: null });
            });
        },

        // check valid
        areAllControlsValid: function () {
            return _.isEmpty(_.filter(this.getControls(), function (control) {
                return !control.isValid();
            }));
        },
        // Setter for IC container.
        setContainer: function (containerSelector) {
            this.container = jQuery(containerSelector);
        },

        // Reload container, used in case when DOM has been changed.
        reloadContainer: function () {
            this.container = jQuery(this.containerSelector);
            return this.container;
        },

        // Prevent user action on control
        disable: function () {
            _.each(this.getControls(), function (control) {
                control.set({ disabled: true });
            });
        },

        // Allow user action on control
        enable: function () {
            _.each(this.getControls(), function (control) {
                control.set({ disabled: false });
            });
        },

        _initSortable: function(options) {
            var container = options.container,
                horizontalLayout = options.horizontalLayout,
                keepSortableAlive = false;

            var self = this;

            var sortableContainer = jQuery(container).sortable({
                delay: 200,
                placeholder: horizontalLayout ? "verticalPlaceholder" : "horizontalPlaceholder",
                axis: horizontalLayout ? "x" : "y",
                items: "> .leaf",
                cancel: "input,textarea,button,select,option,.list.inputSet,.jr-mSizer",
                start: function(event, ui) {
                    ui.item.data('oldIndex', ui.item.index());
                    keepSortableAlive = true;
                },
                stop: _.bind(function(event, ui) {
                    var oldIndex = ui.item.data('oldIndex');
                    var newIndex = ui.item.index();
                    oldIndex != newIndex && this.reorderControl(oldIndex, newIndex);
                    keepSortableAlive = false;
                }, this)
            });

            // JS-33242
            if (isIE) {
                var parent = options.parent || sortableContainer.parent();

                parent.off("scroll", scrollStartStopCallback);
                parent.off("scrollstart");
                parent.off("scrollstop");

                parent.on("scroll", scrollStartStopCallback);

                parent.on("scrollstart", function () {
                    if (!keepSortableAlive) {
                        try {
                            sortableContainer.sortable("destroy");
                        } catch (e) {}
                    }
                });

                parent.on("scrollstop", function () {
                    !keepSortableAlive && self._initSortable({
                        parent: parent,
                        container: container,
                        horizontalLayout: horizontalLayout
                    });
                });
            }
        }
    };
    _.extend(ViewModel.prototype, Model, View);
    return _.extend(Controls, { ViewModel: ViewModel });
}(jQuery, _, JRS.Controls);

export default JRS.Controls;
