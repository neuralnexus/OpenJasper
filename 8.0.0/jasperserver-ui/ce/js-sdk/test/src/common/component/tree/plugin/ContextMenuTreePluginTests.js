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
import ContextMenu from 'src/common/component/menu/ContextMenu';
import ContextMenuTreePlugin from 'src/common/component/tree/plugin/ContextMenuTreePlugin';

describe('Tree component plugin: ContextMenuTreePlugin', function(){
    var contextMenuPlugin,
        contextMenu;

    beforeEach(function() {
        contextMenu = new ContextMenu([
            { label: "label1", action: "add"},
            { label: "label2", action: "remove", default: true}
        ], {toggle: true});

        var options = {
            contextMenus: {filterContextMenu: contextMenu},
            showContextMenuCondition: function(item){
                if(item.cssClass==="someClass"){
                    return this.contextMenus.filterContextMenu;
                }
            },
            defaultSelectedItems: function(item, actions){
                if(item.url === "path"){
                    return actions[0];
                }
            }
        };
        contextMenuPlugin = new ContextMenuTreePlugin(options);
    });

    afterEach(function(){
        contextMenu && contextMenu.remove();
        contextMenuPlugin && contextMenuPlugin.remove();
        $(".menu.vertical.dropDown.fitable").remove();
    });

    it("should be properly initialized and have proper functions", function(){
        expect(contextMenuPlugin).toBeDefined();
        expect(contextMenuPlugin.itemAction).toBeDefined();
        expect(contextMenuPlugin.contextMenus).toBeDefined();
        expect(contextMenuPlugin.showContextMenuCondition).toBeDefined();
        expect(contextMenuPlugin.itemsRendered).toBeDefined();
    });

    it("should define showContextMenuCondition if no options where passed", function(){
        contextMenu = new ContextMenu([
            { label: "label1", action: "add"},
            { label: "label2", action: "remove", default: true}
        ]);

        contextMenuPlugin = new ContextMenuTreePlugin({
            contextMenu: contextMenu
        });

        expect(contextMenuPlugin.showContextMenuCondition).toBeDefined();
    });

    it("should throw error if options where not specified", function(){
        expect(function(){new ContextMenuTreePlugin();}).toThrowError("Initialization error. Options required.");
    });

    it("should throw error if contextMenu of menus where not specified", function(){
        expect(function(){new ContextMenuTreePlugin({});}).toThrowError("contextMenu or contextMenus must be specified.");
    });

    it("should throw error if contextMenus specified without condition function", function(){
        expect(function(){new ContextMenuTreePlugin({
            contextMenus: {
                filterContextMenu: contextMenu
            }
        });}).toThrowError("contextMenu must be specified for default behaviour");
    });

    it("should call initContextMenu on 'list:item:contextmenu' event", function(){

        var showContextMenuConditionSpy  = sinon.spy(contextMenuPlugin, "showContextMenuCondition");

        var model = new Backbone.Model();
        var list = new Backbone.View({model: model});

        contextMenuPlugin.itemsRendered(model, list);

        var event = $.Event();
        event.pageX = 10;
        event.pageY = 10;

        var item = {cssClass: "someClass"};

        list.trigger("list:item:contextmenu", item, event);

        expect(showContextMenuConditionSpy).toHaveBeenCalledWith(item);
        expect(contextMenuPlugin.currentContextMenu.treeItem).toBeDefined();
        expect(contextMenuPlugin.lastContextMenu).toBe(contextMenuPlugin.currentContextMenu);

        showContextMenuConditionSpy.restore();
    });

    it("should call itemAction on 'select' event", function(){

        var showContextMenuConditionSpy  = sinon.spy(contextMenuPlugin, "showContextMenuCondition");
        var itemActionSpy  = sinon.spy(contextMenuPlugin, "itemAction");
        var defaultSelectedItemsSpy = sinon.spy(contextMenuPlugin, "defaultSelectedItems");

        var model = new Backbone.Model();
        var optionModel = new Backbone.Model({action: "remove"});
        var optionView = new Backbone.View();
        optionView.addSelection = function(){};
        var list = new Backbone.View({model: model});

        contextMenuPlugin.itemsRendered(model, list);

        var event = $.Event();
        event.pageX = 10;
        event.pageY = 10;

        var item = {cssClass: "someClass", url: "path"};

        list.trigger("list:item:contextmenu", item, event);

        expect(showContextMenuConditionSpy).toHaveBeenCalledWith(item);
        expect(defaultSelectedItemsSpy).toHaveBeenCalled(item, ["add", "remove"]);
        expect(contextMenuPlugin.currentContextMenu.treeItem).toBeDefined();
        expect(contextMenuPlugin.lastContextMenu).toBe(contextMenuPlugin.currentContextMenu);

        contextMenuPlugin.currentContextMenu.collection.trigger("select", optionView, optionModel);

        expect(itemActionSpy).toHaveBeenCalled();
        expect(contextMenuPlugin.currentContextMenu.treeItem.action).toEqual("remove");

        showContextMenuConditionSpy.restore();
        itemActionSpy.restore();
    });
});