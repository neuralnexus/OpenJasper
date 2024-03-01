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
import tabbedPanelTrait from 'src/common/component/panel/trait/tabbedPanelTrait';
import Backbone from 'backbone';
import OptionContainer from 'src/common/component/base/OptionContainer';
import $ from 'jquery';
describe('tabbedPanelTrait', function () {
    it('should throw error if no \'tabs\' option is passed in \'onConstructor\' method', function () {
        var obj = {};
        expect(function () {
            tabbedPanelTrait.onConstructor.call(obj, {});
        }).toThrow(new Error('Tabbed panel should have at least one tab'));
        expect(function () {
            tabbedPanelTrait.onConstructor.call(obj, { tabs: {} });
        }).toThrow(new Error('Tabbed panel should have at least one tab'));
        expect(function () {
            tabbedPanelTrait.onConstructor.call(obj, { tabs: [] });
        }).toThrow(new Error('Tabbed panel should have at least one tab'));
    });
    it('should assign default tabContainerClass, tabHeaderContainerSelector, tabHeaderContainerClass and tabbedPanelClass in \'onConstructor\' method', function () {
        var obj = {};
        tabbedPanelTrait.onConstructor.call(obj, { tabs: [{ action: 'test' }] });
        expect(obj.tabContainerClass).toBeDefined();
        expect(obj.tabHeaderContainerSelector).toBeDefined();
        expect(obj.tabHeaderContainerClass).toBeDefined();
        expect(obj.tabbedPanelClass).toBeDefined();
    });
    it('should assign passed through options tabContainerClass, tabHeaderContainerSelector, tabHeaderContainerClass and tabbedPanelClass in \'onConstructor\' method', function () {
        var obj = {};
        tabbedPanelTrait.onConstructor.call(obj, {
            tabs: [{ action: 'test' }],
            tabContainerClass: '.tabContainer',
            tabHeaderContainerSelector: '.tabHeaderContainer',
            tabHeaderContainerClass: 'tabHeaderContainer',
            tabbedPanelClass: 'panel'
        });
        expect(obj.tabContainerClass).toBe('.tabContainer');
        expect(obj.tabHeaderContainerSelector).toBe('.tabHeaderContainer');
        expect(obj.tabHeaderContainerClass).toBe('tabHeaderContainer');
        expect(obj.tabbedPanelClass).toBe('panel');
    });
    it('should create \'tabs\' object with content in \'onConstructor\' method', function () {
        var obj = {};
        tabbedPanelTrait.onConstructor.call(obj, {
            tabs: [{
                action: 'test',
                content: 'test'
            }]
        });
        expect(obj.tabs).toBeDefined();
        expect(obj.tabs['test']).toBe('test');
    });
    it('should add class to element, create $tabHeaderContainer and $tabs DOM elements in \'afterSetElement\' method', function () {
        var view = new Backbone.View({
            el: function () {
                return $('<div><div class=\'header\'></div><div class=\'subcontainer\'></div></div>');
            }
        });
        tabbedPanelTrait.onConstructor.call(view, {
            tabs: [
                {
                    action: 'test',
                    content: 'test'
                },
                {
                    action: 'test2',
                    content: 'test2'
                }
            ]
        });
        view.$contentContainer = view.$('.subcontainer');
        tabbedPanelTrait.afterSetElement.call(view);
        expect(view.$tabHeaderContainer).toBeDefined();
        expect(view.$tabHeaderContainer.length).toBe(1);
        expect(view.$tabHeaderContainer.hasClass(view.tabHeaderContainerClass)).toBe(true);
        expect(view.$tabHeaderContainer.parent().hasClass('header')).toBe(true);
        expect(view.$tabs).toBeDefined();
        expect(view.$tabs.length).toBe(2);
        expect($(view.$tabs[0]).data('tab')).toBe('test');
        expect($(view.$tabs[0]).html()).toBe('test');
        expect($(view.$tabs[1]).data('tab')).toBe('test2');
        expect($(view.$tabs[1]).html()).toBe('test2');
        expect(view.$tabs.hasClass(view.tabContainerClass)).toBe(true);
        expect(view.$tabs.parent().hasClass('subcontainer')).toBe(true);
        view.remove();
    });
    it('should create $tabHeaderContainer passed selector in \'afterSetElement\' method', function () {
        var view = new Backbone.View({
            el: function () {
                return $('<div><div class=\'tabHeaderContainer\'></div><div class=\'subcontainer\'></div></div>');
            }
        });
        view.$contentContainer = view.$('.subcontainer');
        tabbedPanelTrait.onConstructor.call(view, {
            tabs: [
                {
                    action: 'test',
                    content: 'test'
                },
                {
                    action: 'test2',
                    content: 'test2'
                }
            ],
            tabHeaderContainerSelector: '> .tabHeaderContainer'
        });
        tabbedPanelTrait.afterSetElement.call(view);
        expect(view.$tabHeaderContainer).toBeDefined();
        expect(view.$tabHeaderContainer.length).toBe(1);
        expect(view.$tabHeaderContainer.hasClass(view.tabHeaderContainerClass)).toBe(true);
        expect(view.$tabHeaderContainer.parent().hasClass(view.tabbedPanelClass)).toBe(true);
        view.remove();
    });
    it('should remove content of tabs and tabs itself in \'onRemove\' method', function () {
        var obj = {};
        obj.tabs = {
            'test': {
                remove: function () {
                }
            }
        };
        obj.tabHeaderContainer = {
            remove: function () {
            }
        };
        var removeSpy1 = sinon.spy(obj.tabs.test, 'remove'), removeSpy2 = sinon.spy(obj.tabHeaderContainer, 'remove');
        tabbedPanelTrait.onRemove.call(obj);
        expect(removeSpy1).toHaveBeenCalled();
        expect(removeSpy2).toHaveBeenCalled();
        removeSpy1.restore();
        removeSpy2.restore();
    });
    it('should create OptionContainer and open primary tab in \'afterInitialize\' method', function () {
        var view = new Backbone.View();
        view.$tabHeaderContainer = $('<div></div>');
        view.openTab = function () {
        };
        var openTabSpy = sinon.spy(view, 'openTab');
        tabbedPanelTrait.afterInitialize.call(view, {
            tabs: [{
                action: 'test',
                label: 'Test',
                primary: true
            }]
        });
        expect(view.tabHeaderContainer).toBeDefined();
        expect(view.tabHeaderContainer instanceof OptionContainer).toBe(true);
        expect(view.tabHeaderContainer.collection.at(0).get('action')).toBe('test');
        expect(view.tabHeaderContainer.contextName).toBe('tab');
        expect(view.tabHeaderContainer.toggleClass).toBe('active');
        expect(view.tabHeaderContainer.toggle).toBe(true);
        expect(openTabSpy).toHaveBeenCalledWith('test');
        openTabSpy.restore();
        view.remove();
    });
    it('should show selected tab', function () {
        var contentView = new Backbone.View(), view = new Backbone.View({
                el: function () {
                    return $('<div><div class=\'header\'></div><div class=\'subcontainer\'></div></div>');
                }
            }), options = {
                tabs: [{
                    action: 'test',
                    label: 'Test',
                    content: contentView
                }]
            }, renderSpy = sinon.spy(contentView, 'render'), triggerSpy = sinon.spy(view, 'trigger');
        view.$contentContainer = view.$('.subcontainer');
        tabbedPanelTrait.onConstructor.call(view, options);
        tabbedPanelTrait.afterSetElement.call(view);
        tabbedPanelTrait.afterInitialize.call(view, options);
        $('body').append(view.$el);
        view.tabHeaderContainer.trigger('tab:test', view.tabHeaderContainer.options[0], view.tabHeaderContainer.options[0].model);
        expect(renderSpy).toHaveBeenCalled();
        expect(triggerSpy).toHaveBeenCalledWith('tab:test', view.tabHeaderContainer.options[0], view.tabHeaderContainer.options[0].model);
        expect(view._contentRendered['test']).toBe(true);
        expect(view.$tabs.filter(function () {
            return $(this).data('tab') === 'test';
        }).is(':visible')).toBe(true);
        expect(view.selectedTab).toBe('test');
        view.remove();
    });
    it('should have \'openTab\' extension method', function () {
        expect(tabbedPanelTrait.extension).toBeDefined();
        expect(tabbedPanelTrait.extension.openTab).toBeDefined();
        var obj = {}, optionView = {
            select: function () {
            }
        };
        obj.tabHeaderContainer = {
            getOptionView: function () {
            }
        };
        var getOptionViewStub = sinon.stub(obj.tabHeaderContainer, 'getOptionView').returns(optionView), selectSpy = sinon.spy(optionView, 'select');
        tabbedPanelTrait.extension.openTab.call(obj, 'test');
        expect(getOptionViewStub).toHaveBeenCalledWith('test');
        expect(selectSpy).toHaveBeenCalled();
        getOptionViewStub.restore();
        selectSpy.restore();
    });
    it('should have \'showTab\' extension method', function () {
        expect(tabbedPanelTrait.extension).toBeDefined();
        expect(tabbedPanelTrait.extension.showTab).toBeDefined();
        var obj = {}, optionView = {
            show: function () {
            }
        };
        obj.tabHeaderContainer = {
            getOptionView: function () {
            }
        };
        var getOptionViewStub = sinon.stub(obj.tabHeaderContainer, 'getOptionView').returns(optionView), showSpy = sinon.spy(optionView, 'show');
        tabbedPanelTrait.extension.showTab.call(obj, 'test');
        expect(getOptionViewStub).toHaveBeenCalledWith('test');
        expect(showSpy).toHaveBeenCalled();
        getOptionViewStub.restore();
        showSpy.restore();
    });
    it('should have \'hideTab\' extension method', function () {
        expect(tabbedPanelTrait.extension).toBeDefined();
        expect(tabbedPanelTrait.extension.hideTab).toBeDefined();
        var obj = {}, optionView = {
            hide: function () {
            }
        };
        obj.tabHeaderContainer = {
            getOptionView: function () {
            }
        };
        var getOptionViewStub = sinon.stub(obj.tabHeaderContainer, 'getOptionView').returns(optionView), hideSpy = sinon.spy(optionView, 'hide');
        tabbedPanelTrait.extension.hideTab.call(obj, 'test');
        expect(getOptionViewStub).toHaveBeenCalledWith('test');
        expect(hideSpy).toHaveBeenCalled();
        getOptionViewStub.restore();
        hideSpy.restore();
    });
});