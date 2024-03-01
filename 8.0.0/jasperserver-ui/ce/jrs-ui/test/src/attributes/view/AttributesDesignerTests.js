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

import sinon from 'sinon';
import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import Marionette from 'backbone.marionette';
import AttributesDesigner from 'src/attributes/view/AttributesDesigner';
import attributesDesignerFactory from 'src/attributes/factory/attributesDesignerFactory';
import confirmDialogTypesEnum from 'src/serverSettingsCommon/enum/confirmDialogTypesEnum';
import designerButtonsTrait from 'src/serverSettingsCommon/view/traits/buttonsTrait';
import designerFilterTrait from 'src/attributes/view/trait/designerFilterTrait';
import attributesTypesEnum from 'src/attributes/enum/attributesTypesEnum';
import setTemplates from 'js-sdk/test/tools/setTemplates';

describe('AttributesDesigner Tests', function () {
    var attributesDesigner, createInstance = function (level) {
        var model = Backbone.Model.extend({}), AttributesCollection = Backbone.Collection.extend({
            addNew: function (attribute) {
                attribute = attribute || {};
                var model = new this.model(attribute);
                this.add(model);
                return model;
            }
        });
        return new AttributesDesigner({
            type: level || attributesTypesEnum.SERVER,
            collection: new AttributesCollection([
                {first: 'first'},
                {second: 'second'}
            ], {model: model}),
            childView: Marionette.ItemView.extend({
                template: _.template('<div><span></span></div>'),
                tagName: 'p',
                slideDown: function () {
                },
                toggleMode: function () {
                    return new $.Deferred();
                },
                toggleActive: function () {
                    return new $.Deferred();
                },
                validateModel: function () {
                    return this.model.isValid(true);
                },
                isStateConfirmed: function () {
                },
                invokeFiltration: function () {
                }
            }),
            childViewContainer: '.tbody',
            emptyView: Marionette.ItemView.extend({
                template: _.template('<div><span></span></div>'),
                tagName: 'p'
            }),
            buttons: [
                {
                    label: 'Save',
                    action: 'save',
                    primary: true
                },
                {
                    label: 'Cancel',
                    action: 'cancel',
                    primary: false
                }
            ],
            buttonsContainer: '.buttonsContainer',
            tooltip: {
                template: '<p></p>',
                i18n: {}
            }
        });
    };
    beforeEach(function () {
        setTemplates('<div class=\'container\'></div>');
        attributesDesigner = createInstance();
    });
    afterEach(function () {
        attributesDesigner && attributesDesigner.remove();
        $('.container').remove();
    });
    it('should be initialized at tenant level', function () {
        attributesDesigner && attributesDesigner.remove();
        var _initConfirmationDialogsStub = sinon.stub(AttributesDesigner.prototype, '_initConfirmationDialogs'),
            _initEventsStub = sinon.stub(AttributesDesigner.prototype, '_initEvents'),
            _initFiltersStub = sinon.stub(designerFilterTrait, '_initFilters'),
            _initButtonsStub = sinon.stub(designerButtonsTrait, '_initButtons');
        attributesDesigner = attributesDesignerFactory(attributesTypesEnum.TENANT, createInstance('tenant'));
        expect(_initConfirmationDialogsStub).toHaveBeenCalled();
        expect(_initEventsStub).toHaveBeenCalled();
        expect(_initFiltersStub).toHaveBeenCalled();
        expect(_initButtonsStub).not.toHaveBeenCalled();
        expect(attributesDesigner.notification).toBeDefined();
        expect(attributesDesigner.alertDialog).toBeDefined();
        expect(attributesDesigner.model).toBeDefined();
        expect(attributesDesigner.changedModels).toEqual([]);
        expect(attributesDesigner.childViewOptions).toBeDefined();
        expect(attributesDesigner.emptyViewOptions).toBeDefined();
        _initConfirmationDialogsStub.restore();
        _initEventsStub.restore();
        _initFiltersStub.restore();
        _initButtonsStub.restore();
    });
    it('should set childView to be current', function () {
        var view = new Marionette.ItemView({
            model: new Backbone.Model(),
            template: '<td></td>'
        });
        attributesDesigner._setCurrentChildView(view);
        expect(attributesDesigner.currentChildView).toBe(view);
    });
    it('should change childView (new view case)', function () {
        var _setCurrentChildViewSpy = sinon.spy(attributesDesigner, '_setCurrentChildView');
        attributesDesigner.render();
        var view_1 = attributesDesigner.children.findByIndex(0);
        attributesDesigner._setCurrentChildView(view_1);
        expect(attributesDesigner.currentChildView).toBe(view_1);
        attributesDesigner._addNewChildView();
        var view_2 = attributesDesigner.children.findByIndex(2);
        attributesDesigner._activeChildView(view_2, true);
        expect(_setCurrentChildViewSpy).toHaveBeenCalled();
        expect(attributesDesigner.currentChildView).toBe(view_2);
        _setCurrentChildViewSpy.restore();
    });
    it('should change childView (not new view case)', function () {
        var _setCurrentChildViewSpy = sinon.spy(attributesDesigner, '_setCurrentChildView');
        var model_1 = new Backbone.Model(), model_2 = new Backbone.Model();
        model_1.set('id', 1);
        var View_1 = Marionette.ItemView.extend({
                model: model_1,
                template: '<td></td>',
                toggleMode: function () {
                },
                validateModel: function () {
                    return this.model.isValid(true);
                }
            }), view_2 = new Marionette.ItemView({
                model: model_2,
                template: '<td></td>',
                validateModel: function () {
                    return this.model.isValid(true);
                }
            });
        var view_1 = new View_1();
        attributesDesigner._setCurrentChildView(view_1);
        expect(attributesDesigner.currentChildView).toBe(view_1);
        attributesDesigner._activeChildView(view_2, true);
        expect(_setCurrentChildViewSpy).toHaveBeenCalled();
        expect(attributesDesigner.currentChildView).toBe(view_2);
        _setCurrentChildViewSpy.restore();
    });
    it('should add new child to collection', function () {
        attributesDesigner.render();
        var _saveChildViewToChangedListSpy = sinon.spy(attributesDesigner, '_saveChildViewToChangedList');
        attributesDesigner._addNewChildView();
        expect(_saveChildViewToChangedListSpy).toHaveBeenCalled();
        expect(attributesDesigner.collection.length).toEqual(3);
        _saveChildViewToChangedListSpy.restore();
    });
    it('should init tooltip', function () {
        attributesDesigner.render();
        expect(attributesDesigner.tooltip).toBeDefined();
    });
    it('should show tooltip', function () {
        attributesDesigner && attributesDesigner.remove();
        var _onChildViewMouseOverStub = sinon.stub(AttributesDesigner.prototype, '_onChildViewMouseOver');
        attributesDesigner = createInstance();
        attributesDesigner.render();
        $('.container').append(attributesDesigner.$el);
        var tooltip = attributesDesigner.tooltip, tooltipHideStub = sinon.stub(tooltip, 'hide'),
            model = attributesDesigner.collection.models[0], view = attributesDesigner.children.findByModel(model);
        view.trigger('mouseover');
        expect(_onChildViewMouseOverStub).toHaveBeenCalled();
        view.trigger('mouseout');
        expect(tooltipHideStub).toHaveBeenCalled();
        tooltipHideStub.restore();
        _onChildViewMouseOverStub.restore();
        tooltipHideStub.restore();
    });
    it('should init, render and reset filters', function () {
        attributesDesigner && attributesDesigner.remove();
        var _initFiltersStub = sinon.stub(designerFilterTrait, '_initFilters'),
            _renderFiltersStub = sinon.stub(designerFilterTrait, '_renderFilters'),
            _resetFiltersStub = sinon.stub(designerFilterTrait, '_resetFilters');
        attributesDesigner = attributesDesignerFactory(attributesTypesEnum.TENANT, createInstance('tenant'));
        attributesDesigner.render();
        expect(_initFiltersStub).toHaveBeenCalled();
        expect(_renderFiltersStub).toHaveBeenCalled();
        attributesDesigner.hide();
        expect(_resetFiltersStub).toHaveBeenCalled();
        _initFiltersStub.restore();
        _renderFiltersStub.restore();
        _resetFiltersStub.restore();
    });
    it('should not render filters for server level', function () {
        attributesDesigner && attributesDesigner.remove();
        var _renderFiltersStub = sinon.stub(designerFilterTrait, '_renderFilters');
        attributesDesigner = createInstance();
        attributesDesigner.render();
        expect(_renderFiltersStub).not.toHaveBeenCalled();
        _renderFiltersStub.restore();
    });
    it('should call _setCurrentChildView method', function () {
        attributesDesigner.render();
        var _setCurrentChildViewSpy = sinon.spy(attributesDesigner, '_setCurrentChildView');
        var view = attributesDesigner.children.findByModel(attributesDesigner.collection.models[0]);
        view.trigger('active', true);
        expect(_setCurrentChildViewSpy).toHaveBeenCalled();
        _setCurrentChildViewSpy.restore();
    });
    it('should init confirm dialogs', function () {
        attributesDesigner._initConfirmationDialogs();
        expect(attributesDesigner.confirmationDialogs[confirmDialogTypesEnum.DELETE_CONFIRM]).toBeDefined();
        expect(attributesDesigner.confirmationDialogs[confirmDialogTypesEnum.NAME_CONFIRM]).toBeDefined();
    });
    it('should scrollToChildView', function () {
        var childView = {
            $el: {
                position: function () {
                    return {top: 400};
                },
                height: function () {
                    return 200;
                }
            }
        };
        var $parentElement = {
            height: function () {
                return 500;
            },
            animate: function () {
            },
            scrollTop: function () {
                return 200;
            },
            parent: function () {
                return this;
            },
            first: function () {
                return this;
            }
        };
        var parentAnimationSpy = sinon.spy($parentElement, 'animate');
        var parentStub = sinon.stub(attributesDesigner.$el, 'closest').returns($parentElement);
        attributesDesigner._scrollToChildView(childView);
        expect(parentAnimationSpy).toHaveBeenCalledWith({scrollTop: 200 + (400 + 200 - 500)}, 900);
        parentStub.restore();
        parentAnimationSpy.restore();
    });
    it('should show permission confirmation dialog', function () {
        attributesDesigner && attributesDesigner.remove();
        var openConfirmStub = sinon.stub(AttributesDesigner.prototype, '_openConfirm');
        attributesDesigner = createInstance();
        attributesDesigner.render();
        var childView = attributesDesigner.children.findByModel(attributesDesigner.collection.models[0]);
        childView.trigger('open:confirm', confirmDialogTypesEnum.PERMISSION_CONFIRM, {});
        expect(openConfirmStub).toHaveBeenCalled();
        openConfirmStub.restore();
    });
    it('should hide \'Add New Item\' button on edit/add new child view', function () {
        attributesDesigner && attributesDesigner.remove();
        var _toggleAddNewItemButtonStub = sinon.stub(AttributesDesigner.prototype, '_toggleAddNewItemButton');
        attributesDesigner = createInstance();
        attributesDesigner.render();
        var view = attributesDesigner.children.findByModel(attributesDesigner.collection.models[1]);
        attributesDesigner._activeChildView(view);
        expect(_toggleAddNewItemButtonStub).toHaveBeenCalled();
        _toggleAddNewItemButtonStub.restore();
    });
    it('should remove child view', function () {
        var model = attributesDesigner.collection.models[0],
            _saveChildViewToChangedListStub = sinon.stub(attributesDesigner, '_saveChildViewToChangedList'),
            _removeModelStub = sinon.stub(attributesDesigner, '_removeModel');
        attributesDesigner.removeView(model);
        expect(_saveChildViewToChangedListStub).toHaveBeenCalled();
        expect(_removeModelStub).toHaveBeenCalled();
        expect(model.isDeleted).toEqual({index: 0});
        _saveChildViewToChangedListStub.restore();
        _removeModelStub.restore();
    });
    it('should be removed', function () {
        var _removeConfirmationDialogsStub = sinon.stub(attributesDesigner, '_removeConfirmationDialogs'),
            removeNotificationStub = sinon.stub(attributesDesigner.notification, 'remove'),
            removeAlertDialogStub = sinon.stub(attributesDesigner.alertDialog, 'remove');
        attributesDesigner.remove();
        expect(_removeConfirmationDialogsStub).toHaveBeenCalled();
        expect(removeNotificationStub).toHaveBeenCalled();
        expect(removeAlertDialogStub).toHaveBeenCalled();
        _removeConfirmationDialogsStub.restore();
        removeNotificationStub.restore();
        removeAlertDialogStub.restore();
    });
});