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
 * @version: $Id: list.base.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery",
        "dragdrop",
        "tests/data/list.data",
        "components.list",
        "text!templates/list.htm"],
        function(jQuery, Draggable, items, dynamicList, listText) {

        describe("lists", function() {

            beforeEach(function() {
                setTemplates(listText);
            });

            describe("base list", function() {
                var list, item;

                beforeEach(function() {
                    list = jQuery('#defaultListTemplate');
                    item = list.find('.leaf');
                    $(item[0]); // IE 7 magic

                    item.removeClass(layoutModule.SELECTED_CLASS);
                    item.removeClass(layoutModule.DISABLED_CLASS);
                    item.removeClass(layoutModule.OPEN_CLASS);
                    item.removeClass(layoutModule.CLOSED_CLASS);
                    if (typeof(item[0].isOpen) !== 'undefined') {
                        delete item[0].isOpen;
                    }
                    item.attr(layoutModule.DISABLED_ATTR_NAME, false);
                });

                it('should be able to check, if list is responsive ', function() {
                    list.addClass(layoutModule.RESPONSIVE_CLASS);
                    expect(baseList.isResponsive(item[0])).toBeTruthy();

                    list.removeClass(layoutModule.RESPONSIVE_CLASS);
                    expect(baseList.isResponsive(item[0])).toBeFalsy();
                });

                it('should be able to check, if list is collapsible ', function() {
                    list.addClass(layoutModule.COLLAPSIBLE_CLASS);
                    expect(baseList.isCollapsible(item[0])).toBeTruthy();

                    list.removeClass(layoutModule.COLLAPSIBLE_CLASS);
                    expect(baseList.isCollapsible(item[0])).toBeFalsy();
                });

                it('should be able to select item (fails, see comments)', function() {
                    item.addClass(layoutModule.DISABLED_CLASS);

                    baseList.selectItem(item[0]);

                    // expect(item.hasClass(layoutModule.SELECTED_CLASS)).toBeFalsy();

                    //TODO fix it in the code
                    // return omitted   list.base.js:44
                    // isItemDisabled: function(item) { buttonManager.isDisabled(item); },

                    item.removeClass(layoutModule.DISABLED_CLASS);

                    baseList.selectItem(item[0]);
                    expect(item.hasClass(layoutModule.SELECTED_CLASS)).toBeTruthy();
                });

                it('should be able to deselect item ', function() {
                    item.addClass(layoutModule.SELECTED_CLASS);
                    baseList.deselectItem(item[0]);
                    expect(item.hasClass(layoutModule.SELECTED_CLASS)).toBeFalsy();
                });

                it('should be able to check, if item is selected', function() {
                    expect(baseList.isItemSelected(item[0])).toBeFalsy();

                    baseList.selectItem(item[0]);

                    expect(baseList.isItemSelected(item[0])).toBeTruthy();

                    baseList.deselectItem(item[0]);

                    expect(baseList.isItemSelected(item[0])).toBeFalsy();
                });

                // TODO Based on deprecated function and do not return proper result
                // It's functionality moved to extended Prototype
                // remove baseList.isItemDisabled or make it proper in case of dispose of Prototype

                /*   it('should be able to check, if item is disabled', function(){
                 expect(baseList.isItemDisabled(item[0])).toBeFalsy();

                 item.addClass(layoutModule.DISABLED_CLASS);
                 expect(baseList.isItemDisabled(item[0])).toBeTruthy();

                 item.removeClass(layoutModule.DISABLED_CLASS);
                 expect(baseList.isItemDisabled(item[0])).toBeFalsy();

                 item.attr(layoutModule.DISABLED_ATTR_NAME, true);
                 expect(baseList.isItemDisabled(item[0])).toBeTruthy();

                 item.attr(layoutModule.DISABLED_ATTR_NAME, false);
                 expect(baseList.isItemDisabled(item[0])).toBeFalsy();
                 }); */

                it('should be able to disable item', function() {
                    spyOn(buttonManager, 'disable');
                    baseList.disableItem(item[0]);
                    expect(buttonManager.disable).toHaveBeenCalledWith(item[0]);
                });

                it('should be able to enable item', function() {
                    spyOn(buttonManager, 'enable');
                    baseList.enableItem(item[0]);
                    expect(buttonManager.enable).toHaveBeenCalledWith(item[0]);
                });

                it('should be able to open item', function() {
                    item.addClass(layoutModule.CLOSED_CLASS);

                    baseList.openItem(item[0]);

                    expect(item.hasClass(layoutModule.CLOSED_CLASS)).toBeFalsy();
                    expect(item.hasClass(layoutModule.OPEN_CLASS)).toBeTruthy();
                    expect(item[0].isOpen).toBeTruthy();
                });

                it('should be able to close item', function() {
                    item.addClass(layoutModule.OPEN_CLASS);
                    item[0].isOpen = true;

                    baseList.closeItem($(item[0]));

                    expect(item.hasClass(layoutModule.CLOSED_CLASS)).toBeTruthy()
                    expect(item.hasClass(layoutModule.OPEN_CLASS)).toBeFalsy()
                    expect(item[0].isOpen).toBeFalsy();
                });

                it('should be able to find, if is open', function() {
                    expect(baseList.isItemOpen($(item[0]))).toBeFalsy();

                    baseList.openItem(item[0]);
                    expect(baseList.isItemOpen($(item[0]))).toBeTruthy();

                    baseList.closeItem(item[0]);
                    expect(baseList.isItemOpen($(item[0]))).toBeFalsy();
                });
            });

            describe("dynamic list List", function() {
                var el = {
                    identify: function() {
                        return 0;
                    },
                    pass : true
                }
                var event = {
                    element: function() {
                        return el;
                    }
                }
                it('should be able add items', function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate"});
                    var itemsWas = list._items.length;
                    spyOn(list, '_prepareListItem');
                    spyOn(list._items, 'sort').andCallFake(function() {
                        return list._items
                    });
                    list._comparator = true;

                    list.addItems(items);

                    expect(list._items.length).toEqual(items.length + itemsWas);
                    expect(list._prepareListItem).toHaveBeenCalled();
                    expect(list._items.sort).toHaveBeenCalled();

                });

                it('should be able insert items', function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate"});
                    var base = [1, 2, 4, 5];
                    var insert = ['a', 'b'];
                    spyOn(list, '_prepareListItem');

                    list.addItems(base);
                    list.insertItems(0, insert);

                    expect(list._items.length).toEqual(base.length + insert.length);
                    expect(list.getItems()).toEqual(['a', 'b', 1, 2, 4, 5]);
                });

                it('should be able insert items in wrong position', function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate"});
                    var base = [1, 2, 4, 5];
                    var insert = ['a', 'b'];
                    spyOn(list, '_prepareListItem');

                    list.addItems(base);
                    list.insertItems(base.length + 1, insert);

                    expect(list._items.length).toEqual(base.length + insert.length);
                    expect(list.getItems()).toEqual([1, 2, 4, 5, 'a', 'b']);
                });

                it('should be able insert items if position set as string', function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate"});
                    var base = [1, 2, 4, 5];
                    var insert = ['a', 'b'];
                    spyOn(list, '_prepareListItem');

                    list.addItems(base);
                    list.insertItems('1', insert);

                    expect(list._items.length).toEqual(base.length + insert.length);
                    expect(list.getItems()).toEqual([1, 'a', 'b', 2, 4, 5]);
                });

                it('should be able insert items if position is negative', function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate"});
                    var base = [1, 2, 4, 5];
                    var insert = ['a', 'b'];
                    spyOn(list, '_prepareListItem');

                    list.addItems(base);
                    list.insertItems(-base.length - 1, insert);

                    expect(list._items.length).toEqual(base.length + insert.length);
                    expect(list.getItems()).toEqual(['a', 'b', 1, 2, 4, 5]);
                });

                // TODO If we try to add just 1 item, which is not array, it fails
                // Should fix it?

                /*   it('should be able insert items if items not an array', function(){
                 var list = new dynamicList.List("defaultListTemplate",{listTemplateDomId:"defaultListTemplate"});
                 spyOn(list,'_prepareListItem');
                 var base = [1,2,4,5];
                 var insert = 3;

                 list.addItems(base);
                 list.insertItems(-base.length-1,insert);

                 expect(list._items.length).toEqual(base.length+insert.length);
                 expect(list.getItems()).toEqual(['a','b',1,2,4,5]);
                 });   */

                it('should be able to remove items', function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate"});
                    var base = [1, 2, 4, 5];
                    var remove = [4, 5];
                    spyOn(remove, 'each');
                    spyOn(list, '_prepareListItem');

                    list.addItems(base);
                    list.removeItems(remove);

                    expect(list._items.length).toEqual(base.length - remove.length);
                    expect(list.getItems()).toEqual([1, 2]);
                });

                it('should do nothing if some items not exists', function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate"});
                    var base = [1, 2, 4, 5];
                    var remove = [3, 4];
                    spyOn(remove, 'each');
                    spyOn(list, '_prepareListItem');

                    list.addItems(base);
                    list.removeItems(remove);

                    expect(list.getItems()).toEqual([1, 2, 5]);
                });

                it("should't fail when you try to remove more items than it has", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate"});
                    var base = [1, 2];
                    var remove = [1, 2, 3, 4];
                    spyOn(remove, 'each');
                    spyOn(list, '_prepareListItem');

                    list.addItems(base);
                    list.removeItems(remove);

                    expect(list.getItems()).toEqual([]);
                });

                it("should't fail if remove items are undefined", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate"});
                    var base = [1, 2];
                    spyOn(list, '_prepareListItem');

                    list.addItems(base);
                    list.removeItems();

                    expect(list.getItems()).toEqual([1, 2]);
                });

                it("should sort items ", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate"});

                    spyOn(list._items, 'sort');

                    list.sort('val');

                    expect(list._items.sort).toHaveBeenCalledWith('val');
                });

                it("should select items", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate"});
                    list.addItems(items);

                    list.selectItem(items[0]);

                    expect(list.getSelectedItems()).toEqual([items[0]]);
                    expect(list.isItemSelected(items[0])).toBeTruthy();

                });

                it("should select just one item", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate"});
                    list.addItems(items);

                    list.selectItem(items[0]);
                    expect(list.getSelectedItems()).toEqual([items[0]]);

                    list.selectItem(items[1]);
                    expect(list.getSelectedItems()).toEqual([items[1]]);

                    list.selectItem(items[2]);
                    expect(list.getSelectedItems()).toEqual([items[2]]);

                });

                // TODO in this case multiselect works even if it forbidden
                it("should select multiple items, including items, which are between first selected and second (Shift held)", function() {
                    // var list = new dynamicList.List("defaultListTemplate",{listTemplateDomId:"defaultListTemplate",multiSelect:true});
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate", multiSelect: false});
                    list.addItems(items);

                    list.selectItem(items[0]);

                    list.selectItem(items[2], false, true, false);
                    expect(list.getSelectedItems()).toEqual(items);

                });

                it("should select multiple items, excluding items, which are between first selected and second (Ctrl held)", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate", multiSelect: true});
                    list.addItems(items);

                    list.selectItem(items[0]);

                    list.selectItem(items[2], true, false, false);

                    expect(list.getSelectedItems()).toEqual([items[0], items[2]]);

                });

                it("should not change selection if context menu opened on selected item", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate", multiSelect: true});
                    list.addItems(items);

                    list.selectItem(items[0]);

                    list.selectItem(items[2], false, true, false);

                    list.selectItem(items[1], false, false, true);

                    expect(list.getSelectedItems()).toEqual(items);

                });

                it("should deselect selected items and select one if context menu opened on not selected item", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate", multiSelect: true});
                    list.addItems(items);

                    list.selectItem(items[0]);

                    list.selectItem(items[2], true, false, false);

                    list.selectItem(items[1], false, false, true);

                    expect(list.getSelectedItems()).toEqual([items[1]]);

                });

                it("should deselect selected items except given selected item", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate", multiSelect: true});
                    list.addItems(items);

                    list.selectItem(items[0]);

                    list.selectItem(items[2], false, true, false);

                    list.deselectOthers(items[1]);

                    expect(list.getSelectedItems()).toEqual([items[1]]);
                });

                it("should deselect all items", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate", multiSelect: true});
                    list.addItems(items);

                    list.selectItem(items[0]);

                    list.selectItem(items[2], false, true, false);

                    list.resetSelected();

                    expect(list.getSelectedItems()).toEqual([]);
                });

                it("should deselect all items and in parent list too", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate", multiSelect: true});
                    list._parentList = {
                        resetSelected: function() {
                        },
                        _addItemToSelected: function() {
                        }
                    }
                    spyOn(list._parentList, 'resetSelected');
                    list.addItems(items);

                    list.selectItem(items[0]);

                    list.selectItem(items[2], false, true, false);

                    list.resetSelected(false);

                    expect(list.getSelectedItems()).toEqual([]);
                    expect(list._parentList.resetSelected).toHaveBeenCalled();
                });

                it("should get next item", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate", multiSelect: true});
                    list.addItems(items);

                    expect(list.getNextItem(items[0])).toEqual(items[1]);
                    expect(list.getNextItem(items[1])).toEqual(items[2]);
                    expect(list.getNextItem(items[2])).toEqual(null);
                });

                it("should get previous item", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate", multiSelect: true});
                    list.addItems(items);

                    expect(list.getPreviousItem(items[0])).toEqual(null);
                    expect(list.getPreviousItem(items[1])).toEqual(items[0]);
                    expect(list.getPreviousItem(items[2])).toEqual(items[1]);
                });

                it("should select next item on corresponding event", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate", multiSelect: true});
                    list.addItems(items);
                    list.selectItem(items[0]);

                    var event = {
                        memo: {
                            targetEvent: {
                                shiftKey: false
                            }
                        }
                    }

                    list.selectNext(event);
                    expect(list.getSelectedItems()).toEqual([items[1]]);

                    list.selectNext(event);
                    expect(list.getSelectedItems()).toEqual([items[2]]);

                    //end reached
                    list.selectNext(event);
                    expect(list.getSelectedItems()).toEqual([items[2]]);
                });

                it("should select next item on corresponding event (multiselect)", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate", multiSelect: true});
                    list.addItems(items);
                    list.selectItem(items[0]);

                    var event = {
                        memo: {
                            targetEvent: {
                                shiftKey: true
                            }
                        }
                    }

                    list.selectNext(event);
                    expect(list.getSelectedItems()).toEqual([items[0], items[1]]);

                    list.selectNext(event);
                    expect(list.getSelectedItems()).toEqual(items);

                    //end reached
                    list.selectNext(event);
                    expect(list.getSelectedItems()).toEqual(items);
                });

                it("should select previous item on corresponding event", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate", multiSelect: true});
                    list.addItems(items);
                    list.selectItem(items[2]);

                    var event = {
                        memo: {
                            targetEvent: {
                                shiftKey: false
                            }
                        }
                    }

                    list.selectPrevious(event);
                    expect(list.getSelectedItems()).toEqual([items[1]]);

                    list.selectPrevious(event);
                    expect(list.getSelectedItems()).toEqual([items[0]]);

                    //end reached
                    list.selectPrevious(event);
                    expect(list.getSelectedItems()).toEqual([items[0]]);
                });

                it("should select previous item on corresponding event (multiselect)", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate", multiSelect: true});
                    list.addItems(items);
                    list.selectItem(items[2]);

                    var event = {
                        memo: {
                            targetEvent: {
                                shiftKey: true
                            }
                        }
                    }

                    list.selectPrevious(event);
                    expect(list.getSelectedItems()).toEqual([items[2], items[1]]);

                    list.selectPrevious(event);
                    expect(list.getSelectedItems()).toEqual([items[2], items[1], items[0]]);
                    //end reached
                    list.selectPrevious(event);
                    expect(list.getSelectedItems()).toEqual([items[2], items[1], items[0]]);
                });

                it("should select inwards items if item already opened", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate", multiSelect: true});
                    list.addItems(items);
                    list.selectItem(items[0]);

                    spyOn(items[0], 'deselect');
                    spyOn(items[1], 'select');
                    spyOn(baseList, 'isItemOpen').andReturn(true);

                    list.selectInwards();

                    expect(items[0].deselect).toHaveBeenCalled();
                    expect(items[1].select).toHaveBeenCalled();
                });

                it("should open sublist if it is closed", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate", multiSelect: true});
                    list.addItems(items);
                    list.selectItem(items[0]);

                    spyOn(baseList, 'isItemOpen').andReturn(false);
                    spyOn(baseList, 'openItem');

                    list.selectInwards();

                    expect(baseList.openItem).toHaveBeenCalled();
                });

                it("should select outwards items if item already opened and item from sublist is selected", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate", multiSelect: true});
                    list.addItems(items);
                    items[1].parentItem = items[0];
                    list.selectItem(items[1]);

                    spyOn(items[0], 'select');
                    spyOn(items[1], 'deselect');
                    spyOn(baseList, 'isItemOpen').andReturn(false);

                    list.selectOutwards();

                    expect(items[1].deselect).toHaveBeenCalled();
                    expect(items[0].select).toHaveBeenCalled();
                });

                it("should close sublist parent item is selected", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate", multiSelect: true});
                    list.addItems(items);
                    list.selectItem(items[0]);

                    spyOn(baseList, 'isItemOpen').andReturn(true);
                    spyOn(baseList, 'closeItem');

                    list.selectOutwards();

                    expect(baseList.closeItem).toHaveBeenCalled();
                });

                it("should have possibility to show and set proper flags", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate", multiSelect: true});
                    list.addItems(items);

                    spyOn(items[0], 'show');
                    spyOn(items[1], 'show');
                    spyOn(items[2], 'show');
                    spyOn(list, '_initEvents');

                    list.show()

                    expect(items[0].show).toHaveBeenCalled();
                    expect(items[1].show).toHaveBeenCalled();
                    expect(items[2].show).toHaveBeenCalled();
                    expect(list._initEvents).toHaveBeenCalled();

                    expect(items[0].first).toBeTruthy();
                    expect(items[0].last).toBeFalsy();
                    expect(items[1].first).toBeFalsy();
                    expect(items[1].last).toBeFalsy();
                    expect(items[2].first).toBeFalsy();
                    expect(items[2].last).toBeTruthy();
                });

                it("should have possibility to refresh items", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate", multiSelect: true});
                    list.addItems(items);
                    var i = 0;
                    list._element = {
                        childElements: function() {
                            return {
                                indexOf: function() {
                                    return i++;
                                },
                                each: function() {
                                }
                            }
                        }
                    }

                    spyOn(items[0], 'isRendered').andReturn(true);
                    spyOn(items[1], 'isRendered').andReturn(true);
                    spyOn(items[2], 'isRendered').andReturn(true);

                    spyOn(items[0], 'refresh');
                    spyOn(items[1], 'refresh');
                    spyOn(items[2], 'refresh');

                    list.refresh();

                    expect(items[0].refresh).toHaveBeenCalled();
                    expect(items[1].refresh).toHaveBeenCalled();
                    expect(items[2].refresh).toHaveBeenCalled();
                });

                it("should have possibility to refresh items", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate", multiSelect: true});
                    list.addItems(items);
                    var i = 0;
                    list._element = {
                        childElements: function() {
                            return {
                                indexOf: function() {
                                    return i++;
                                },
                                each: function() {
                                }
                            }
                        }
                    }

                    spyOn(items[0], 'isRendered').andReturn(true);
                    spyOn(items[1], 'isRendered').andReturn(true);
                    spyOn(items[2], 'isRendered').andReturn(true);

                    spyOn(items[0], 'refresh');
                    spyOn(items[1], 'refresh');
                    spyOn(items[2], 'refresh');

                    list.refresh();

                    expect(items[0].refresh).toHaveBeenCalled();
                    expect(items[1].refresh).toHaveBeenCalled();
                    expect(items[2].refresh).toHaveBeenCalled();
                });

                it("should have possibility to refresh items and change their order if needed", function() {
                    sinon.stub(baseList, "isItemOpen").returns(false);
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate", multiSelect: true});
                    list.addItems([items[0], items[2], items[1]]);
                    var i = 0;
                    list._element = {
                        childElements: function() {
                            return {
                                indexOf: function() {
                                    return i++;
                                },
                                each: function() {
                                }
                            }
                        }
                    }

                    spyOn(items[0], 'isRendered').andReturn(true);
                    spyOn(items[1], 'isRendered').andReturn(true);
                    spyOn(items[2], 'isRendered').andReturn(true);

                    spyOn(items[0], 'refresh');
                    spyOn(items[1], 'show');
                    spyOn(items[2], 'show');

                    list.refresh();

                    expect(items[0].refresh).toHaveBeenCalled();
                    expect(items[1].show).toHaveBeenCalled();
                    expect(items[2].show).toHaveBeenCalled();

                    baseList.isItemOpen.restore();
                });

                it("should have possibility to refresh items and render items, if they not rendered yet", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate", multiSelect: true});
                    list.addItems(items);

                    spyOn(items[0], 'isRendered').andReturn(false);
                    spyOn(items[1], 'isRendered').andReturn(false);
                    spyOn(items[2], 'isRendered').andReturn(false);

                    spyOn(items[0], 'show');
                    spyOn(items[1], 'show');
                    spyOn(items[2], 'show');

                    list.refresh();

                    expect(items[0].show).toHaveBeenCalled();
                    expect(items[1].show).toHaveBeenCalled();
                    expect(items[2].show).toHaveBeenCalled();
                });

                it("should have possibility make item draggable and set callbacks properly", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate", dragPattern: true});
                    list.addItems(items);

                    spyOn(window, 'matchAny').andReturn(el);
                    spyOn(list, 'getItemByEvent').andReturn(false);
                    sinon.spy(window, 'Draggable');
                    sinon.stub(Element, "makePositioned");
                    sinon.stub(Event, "observe");

                    list.createDraggableIfNeeded(event);

                    expect(list.draggables[0]).toBeDefined();

                    expect(window.Draggable.args[0][0]).toBe(el);

                    Element.makePositioned.restore();
                    Event.observe.restore();
                });

                it("should not create draggables if draggable pattern not set", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate"});
                    list.addItems(items);

                    spyOn(window, 'matchAny').andReturn({
                        identify: function() {
                            return 0;
                        }
                    });
                    spyOn(list, 'getItemByEvent').andReturn(false);

                    list.createDraggableIfNeeded(event);

                    expect(list.draggables.length).toEqual(0);

                });

                it("should not create draggables element already draggable", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate"});
                    list.addItems(items);

                    list.draggables[el.identify()] = true;
                    spyOn(window, 'Draggable');

                    list.createDraggableIfNeeded(event);

                    expect(window.Draggable).not.toHaveBeenCalled();
                });

                it("should not create draggables element hasn't draggable pattern", function() {
                    var list = new dynamicList.List("defaultListTemplate", {listTemplateDomId: "defaultListTemplate"});
                    list.addItems(items);
                    spyOn(window, 'matchAny').andReturn(false);
                    spyOn(window, 'Draggable');

                    list.createDraggableIfNeeded(event);

                    expect(window.Draggable).not.toHaveBeenCalled();
                });
            });

            describe('list item', function() {
                var items = [];

                for (var i = 0; i < 3; i++) {
                    items.push(new dynamicList.ListItem({label: 'item' + i, templateDomId: dynamicList.ListItem.prototype.DEFAULT_TEMPLATE_DOM_ID}));
                }

                it("should not show if container not specified", function() {
                    var item = new dynamicList.ListItem();
                    spyOn(item, 'processTemplate');

                    item.show();

                    expect(item.processTemplate).not.toHaveBeenCalled();
                });

                it("should show items when list show method called", function() {
                    var list = new dynamicList.List("list_control_path", {listTemplateDomId: "list_control_path"});
                    list.addItems(items);

                    var children = list._getElement().childElements().length;

                    spyOn(items[0], 'refreshStyle');
                    spyOn(items[1], 'refreshStyle');
                    spyOn(items[2], 'refreshStyle');

                    list.show();

                    expect(items[0].refreshStyle).toHaveBeenCalled();
                    expect(items[1].refreshStyle).toHaveBeenCalled();
                    expect(items[2].refreshStyle).toHaveBeenCalled();
                    expect(list._getElement().childElements().length).toEqual(children + items.length);

                });

                it("should refresh item if it is assigned to list", function() {
                    var list = new dynamicList.List("list_control_path", {listTemplateDomId: "list_control_path"});
                    list.addItems(items);

                    spyOn(items[0], 'refreshStyle');
                    spyOn(items[0], 'processTemplate');

                    items[0].refresh();

                    expect(items[0].refreshStyle).toHaveBeenCalled();
                    expect(items[0].processTemplate).toHaveBeenCalled();
                });

                it("should remove element on refresh, if item removed from list", function() {
                    var list = new dynamicList.List("list_control_path", {listTemplateDomId: "list_control_path"});
                    list.addItems(items);
                    list.show();

                    var children = list._getElement().childElements().length;

                    spyOn(items[0], 'getList').andReturn(false);

                    items[0].refresh();

                    expect(list._getElement().childElements().length).toEqual(children - 1);
                    expect(items[0]._element).toBeFalsy();
                });

                it("should not change state of item if element not defined", function() {
                    var item = new dynamicList.ListItem();
                    spyOn(item, 'processTemplate');
                    spyOn(item, '_getElement');// spy return undefined

                    item.refresh();

                    expect(item.processTemplate).not.toHaveBeenCalled();
                    expect(item._getElement).toHaveBeenCalled();
                });

                it("should refresh styles set specific class for first item", function() {
                    var list = new dynamicList.List("list_control_path", {listTemplateDomId: "list_control_path"});
                    list.addItems(items);
                    list.show();

                    var it = items[1];

                    expect(it._getElement()).not.toHasClass(layoutModule.FIRST_CLASS);
                    it.first = true;
                    it.refreshStyle();
                    expect(it._getElement()).toHasClass(layoutModule.FIRST_CLASS);
                    it.first = false;
                    it.refreshStyle();
                    expect(it._getElement()).not.toHasClass(layoutModule.FIRST_CLASS);
                });

                it("should refresh styles set specific class for last item", function() {
                    var list = new dynamicList.List("list_control_path", {listTemplateDomId: "list_control_path"});
                    list.addItems(items);
                    list.show();

                    var it = items[1];

                    expect(it._getElement()).not.toHasClass(layoutModule.LAST_CLASS);
                    it.last = true;
                    it.refreshStyle();
                    expect(it._getElement()).toHasClass(layoutModule.LAST_CLASS);
                    it.last = false;
                    it.refreshStyle();
                    expect(it._getElement()).not.toHasClass(layoutModule.LAST_CLASS);
                });

                //TODO see previous to do (44,16) can't describe if item is disabled or not
                /*  it("should refresh styles set specific class for disabled item", function(){
                 var list = new dynamicList.List("list_control_path",{listTemplateDomId:"list_control_path"});
                 list.addItems(items);
                 list.show();

                 var it = items[1];

                 expect(it._getElement()).not.toHasClass(layoutModule.DISABLED_CLASS);
                 buttonManager.disable(it._getElement());
                 it.refreshStyle();
                 expect(it._getElement()).toHasClass(layoutModule.DISABLED_CLASS);
                 buttonManager.enable(it._getElement());
                 it.refreshStyle();
                 expect(it._getElement()).not.toHasClass(layoutModule.DISABLED_CLASS);
                 });  */

                it("should refresh styles set specific class for disabled item", function() {
                    var list = new dynamicList.List("list_control_path", {listTemplateDomId: "list_control_path"});
                    list.addItems(items);
                    list.show();

                    var it = items[1];

                    expect(it._getElement()).not.toHasClass(layoutModule.SELECTED_CLASS);
                    list.selectItem(it);
                    it.refreshStyle();
                    expect(it._getElement()).toHasClass(layoutModule.SELECTED_CLASS);
                    list.deselectItem(it);
                    it.refreshStyle();
                    expect(it._getElement()).not.toHasClass(layoutModule.SELECTED_CLASS);
                });

                it("should process template", function() {
                    var elToProcess = $(jQuery('#' + dynamicList.ListItem.prototype.DEFAULT_TEMPLATE_DOM_ID).get(0));
                    var textToProcess = 'testTest';

                    var item = new dynamicList.ListItem({label: textToProcess});

                    elToProcess = item.processTemplate(elToProcess);

                    expect(jQuery(elToProcess).text()).toEqual(textToProcess)
                });
            });

            describe('composite list item', function() {
                var items = [];
                for (var i = 0; i < 3; i++) {
                    items.push(new dynamicList.ListItem({label: 'item' + i, templateDomId: dynamicList.ListItem.prototype.DEFAULT_TEMPLATE_DOM_ID}));
                }
                var composite, list;

                beforeEach(function() {
                    composite = new dynamicList.CompositeItem({items: items, openUp: false, templateDomId: dynamicList.ListItem.prototype.DEFAULT_TEMPLATE_DOM_ID});
                    list = new dynamicList.List("list_control_path", {listTemplateDomId: "list_control_path"});
                });

                it("should show itself and contain sublist", function() {
                    list.addItems([composite]);

                    spyOn(dynamicList.ListItem.prototype, 'show');
                    spyOn(dynamicList.CompositeItem.prototype, '_showSubList');
                    spyOn(baseList, 'closeItem');

                    list.show();

                    expect(dynamicList.ListItem.prototype.show).toHaveBeenCalled();
                    expect(dynamicList.CompositeItem.prototype._showSubList).toHaveBeenCalled();
                    expect(baseList.closeItem).toHaveBeenCalledWith(composite._getElement());
                });

                it("should show itself and not render sublist id there no subitens", function() {
                    delete composite._items;
                    list.addItems([composite]);

                    spyOn(dynamicList.ListItem.prototype, 'show');
                    spyOn(dynamicList.CompositeItem.prototype, '_showSubList');
                    spyOn(baseList, 'closeItem');

                    list.show();

                    expect(dynamicList.ListItem.prototype.show).toHaveBeenCalled();
                    expect(dynamicList.CompositeItem.prototype._showSubList).not.toHaveBeenCalled();
                    expect(baseList.closeItem).toHaveBeenCalledWith(composite._getElement());
                });

                it("should create sublist properly", function() {
                    list.addItems([composite]);

                    var fakeSubList = {
                        show: function() {
                        },
                        items: [],
                        getItems: function() {
                            if (!this.items.length) {
                                for (var i = 0; i < 3; i++) {
                                    items.push(new dynamicList.ListItem({label: 'item' + i, templateDomId: dynamicList.ListItem.prototype.DEFAULT_TEMPLATE_DOM_ID}));
                                }
                            }
                            return items;
                        }
                    }
                    spyOn(dynamicList, 'List').andReturn(fakeSubList);
                    spyOn(fakeSubList, 'show');
                    spyOn(baseList, 'closeItem');

                    list.show();

                    expect(fakeSubList.show).toHaveBeenCalled();
                    expect(composite._subList).toBe(fakeSubList);

                    jQuery(fakeSubList.items).each(function(el) {
                        expect(el.parentItem).toBe(composite);
                    });
                });

                it("should refresh itself", function() {
                    spyOn(dynamicList.ListItem.prototype, 'refresh');

                    delete composite._items;
                    list.addItems([composite]);

                    composite.refresh();

                    expect(dynamicList.ListItem.prototype.refresh).toHaveBeenCalled();
                });

                it("should refresh and if sublist not shown, show it", function() {
                    list.addItems([composite]);

                    spyOn(composite, '_showSubList');

                    composite.refresh();

                    expect(composite._showSubList).toHaveBeenCalled();
                });

                it("should refresh sublist", function() {
                    list.addItems([composite]);
                    list.show();

                    spyOn(composite._subList, 'refresh');

                    composite.refresh();

                    expect(composite._subList.refresh).toHaveBeenCalled();
                });

                //TODO sublist created only when show or refresh methods are called. Before it any access to subLit fails

                it("should get first child", function() {
                    list.addItems([composite]);
                    list.show();

                    expect(composite.getFirstChild()).toBe(items[0]);
                });

                it("should refresh style itself", function() {
                    list.addItems([composite]);
                    list.show();
                    spyOn(dynamicList.ListItem.prototype, 'refreshStyle');

                    composite.refreshStyle();

                    expect(dynamicList.ListItem.prototype.refreshStyle).toHaveBeenCalled();

                });

                it("should refresh style for sublist", function() {
                    list.addItems([composite]);
                    list.show();

                    spyOn(composite._subList, 'refreshStyle');

                    composite.refreshStyle();

                    expect(composite._subList.refreshStyle).toHaveBeenCalled();

                });

                it("should update style of element depending on item's state(open)", function() {
                    list.addItems([composite]);
                    list.show();

                    spyOn(baseList, 'openItem');
                    spyOn(baseList, 'closeItem');

                    composite._element.isOpen = true;
                    composite.refreshStyle();

                    expect(baseList.openItem).toHaveBeenCalled();
                    expect(baseList.closeItem).not.toHaveBeenCalled();
                });
                it("should update style of element depending on item's state(closed)", function() {
                    list.addItems([composite]);
                    list.show();

                    spyOn(baseList, 'openItem');
                    spyOn(baseList, 'closeItem');

                    composite._element.isOpen = false;
                    composite.refreshStyle();

                    expect(baseList.openItem).not.toHaveBeenCalled();
                    expect(baseList.closeItem).toHaveBeenCalled();
                });
            });
        });
    });
