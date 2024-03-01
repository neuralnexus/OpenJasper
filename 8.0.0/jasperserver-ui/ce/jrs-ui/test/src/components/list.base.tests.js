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
import jQuery from 'jquery';
import items from 'src/data/list.data';
import {dynamicList, baseList} from 'src/components/list.base';
import listText from './test/templates/list.htm';
import setTemplates from 'js-sdk/test/tools/setTemplates';
import {rewire$matchAny, restore as restoreUtilsCommon} from 'src/util/utils.common';
import layoutModule from 'src/core/core.layout';
import buttonManager from 'src/core/core.events.bis';
import * as dragndrop from 'dragdropextra';

describe('lists', function () {
    let sandbox;
    // in webpack 5 sinon can not stub variable which
    // uses star import: import * as ns from 'library'
    const Draggable = dragndrop.Draggable;

    beforeEach(function () {
        sandbox = sinon.createSandbox();
        setTemplates(listText);
    });
    afterEach(function () {
        sandbox.restore();
        dragndrop.Draggable = Draggable;
    });
    function addListItemsToTheBody(items) {
        var body = jQuery('body');
        var itemElements = items.map(function (item) {
            return item._getElement();
        });
        itemElements.forEach(function (itemElement) {
            body.append(itemElement);
        });
    }
    function removeListItemsFromBody(items) {
        var itemElements = items.map(function (item) {
            return item._getElement();
        });
        itemElements.forEach(function (itemElement) {
            itemElement.remove();
        });
    }
    describe('base list', function () {
        var list, item;
        beforeEach(function () {
            list = jQuery('#defaultListTemplate');
            item = list.find('.leaf');
            jQuery(item[0]);    // IE 7 magic
            // IE 7 magic
            item.removeClass(layoutModule.SELECTED_CLASS);
            item.removeClass(layoutModule.DISABLED_CLASS);
            item.removeClass(layoutModule.OPEN_CLASS);
            item.removeClass(layoutModule.CLOSED_CLASS);
            if (typeof item[0].isOpen !== 'undefined') {
                delete item[0].isOpen;
            }
            item.attr(layoutModule.DISABLED_ATTR_NAME, false);
        });
        it('should be able to check, if list is responsive ', function () {
            list.addClass(layoutModule.RESPONSIVE_CLASS);
            expect(baseList.isResponsive(item[0])).toBeTruthy();
            list.removeClass(layoutModule.RESPONSIVE_CLASS);
            expect(baseList.isResponsive(item[0])).toBeFalsy();
        });
        it('should be able to check, if list is collapsible ', function () {
            list.addClass(layoutModule.COLLAPSIBLE_CLASS);
            expect(baseList.isCollapsible(item[0])).toBeTruthy();
            list.removeClass(layoutModule.COLLAPSIBLE_CLASS);
            expect(baseList.isCollapsible(item[0])).toBeFalsy();
        });
        it('should be able to select item (fails, see comments)', function () {
            baseList._allowSelections = true;
            baseList.selectItem(item[0]);
            expect(item.hasClass(layoutModule.SELECTED_CLASS)).toBeTruthy();
        });
        it('should be able to deselect item ', function () {
            item.addClass(layoutModule.SELECTED_CLASS);
            baseList.deselectItem(item[0]);
            expect(item.hasClass(layoutModule.SELECTED_CLASS)).toBeFalsy();
        });
        it('should be able to check, if item is selected', function () {
            baseList._allowSelections = true;
            baseList.selectItem(item[0]);
            expect(baseList.isItemSelected(item[0])).toBeTruthy();
            baseList.deselectItem(item[0]);
            expect(baseList.isItemSelected(item[0])).toBeFalsy();
        });
        it('should be able to check, if item is disabled', function () {
            sinon.stub(buttonManager, 'isDisabled').returns(true);
            var result = baseList.isItemDisabled(item[0]);
            expect(buttonManager.isDisabled).toHaveBeenCalledWith(item[0]);
            expect(result).toBeTruthy();
            buttonManager.isDisabled.restore();
        });
        it('should be able to disable item', function () {
            spyOn(buttonManager, 'disable');
            baseList.disableItem(item[0]);
            expect(buttonManager.disable).toHaveBeenCalledWith(item[0]);
        });
        it('should be able to enable item', function () {
            spyOn(buttonManager, 'enable');
            baseList.enableItem(item[0]);
            expect(buttonManager.enable).toHaveBeenCalledWith(item[0]);
        });
        it('should be able to open item', function () {
            item.addClass(layoutModule.CLOSED_CLASS);
            baseList.openItem(item[0]);
            expect(item.hasClass(layoutModule.CLOSED_CLASS)).toBeFalsy();
            expect(item.hasClass(layoutModule.OPEN_CLASS)).toBeTruthy();
            expect(item[0].isOpen).toBeTruthy();
        });
        it('should be able to close item', function () {
            item.addClass(layoutModule.OPEN_CLASS);
            item[0].isOpen = true;
            baseList.closeItem(jQuery(item[0]));
            expect(item.hasClass(layoutModule.CLOSED_CLASS)).toBeTruthy();
            expect(item.hasClass(layoutModule.OPEN_CLASS)).toBeFalsy();
            expect(item[0].isOpen).toBeFalsy();
        });
        it('should be able to find, if is open', function () {
            expect(baseList.isItemOpen(jQuery(item[0]))).toBeFalsy();
            baseList.openItem(item[0]);
            expect(baseList.isItemOpen(jQuery(item[0]))).toBeTruthy();
            baseList.closeItem(item[0]);
            expect(baseList.isItemOpen(jQuery(item[0]))).toBeFalsy();
        });
    });
    describe('dynamic list List', function () {
        var el = {
            identify: function () {
                return 0;
            },
            pass: true
        };
        var event = {
            element: function () {
                return el;
            }
        };
        it('should be able add items', function () {
            var list = new dynamicList.List('defaultListTemplate', { listTemplateDomId: 'defaultListTemplate' });
            var itemsWas = list._items.length;
            spyOn(list, '_prepareListItem');
            spyOn(list._items, 'sort').and.callFake(function () {
                return list._items;
            });
            list._comparator = true;
            list.addItems(items);
            expect(list._items.length).toEqual(items.length + itemsWas);
            expect(list._prepareListItem).toHaveBeenCalled();
            expect(list._items.sort).toHaveBeenCalled();
        });
        it('should be able insert items', function () {
            var list = new dynamicList.List('defaultListTemplate', { listTemplateDomId: 'defaultListTemplate' });
            var base = [
                1,
                2,
                4,
                5
            ];
            var insert = [
                'a',
                'b'
            ];
            spyOn(list, '_prepareListItem');
            list.addItems(base);
            list.insertItems(0, insert);
            expect(list._items.length).toEqual(base.length + insert.length);
            expect(list.getItems()).toEqual([
                'a',
                'b',
                1,
                2,
                4,
                5
            ]);
        });
        it('should be able insert items in wrong position', function () {
            var list = new dynamicList.List('defaultListTemplate', { listTemplateDomId: 'defaultListTemplate' });
            var base = [
                1,
                2,
                4,
                5
            ];
            var insert = [
                'a',
                'b'
            ];
            spyOn(list, '_prepareListItem');
            list.addItems(base);
            list.insertItems(base.length + 1, insert);
            expect(list._items.length).toEqual(base.length + insert.length);
            expect(list.getItems()).toEqual([
                1,
                2,
                4,
                5,
                'a',
                'b'
            ]);
        });
        it('should be able insert items if position set as string', function () {
            var list = new dynamicList.List('defaultListTemplate', { listTemplateDomId: 'defaultListTemplate' });
            var base = [
                1,
                2,
                4,
                5
            ];
            var insert = [
                'a',
                'b'
            ];
            spyOn(list, '_prepareListItem');
            list.addItems(base);
            list.insertItems('1', insert);
            expect(list._items.length).toEqual(base.length + insert.length);
            expect(list.getItems()).toEqual([
                1,
                'a',
                'b',
                2,
                4,
                5
            ]);
        });
        it('should be able insert items if position is negative', function () {
            var list = new dynamicList.List('defaultListTemplate', { listTemplateDomId: 'defaultListTemplate' });
            var base = [
                1,
                2,
                4,
                5
            ];
            var insert = [
                'a',
                'b'
            ];
            spyOn(list, '_prepareListItem');
            list.addItems(base);
            list.insertItems(-base.length - 1, insert);
            expect(list._items.length).toEqual(base.length + insert.length);
            expect(list.getItems()).toEqual([
                'a',
                'b',
                1,
                2,
                4,
                5
            ]);
        });
        it('should be able insert items if items not an array', function () {
            var list = new dynamicList.List('defaultListTemplate', { listTemplateDomId: 'defaultListTemplate' });
            spyOn(list, '_prepareListItem');
            var base = [
                1,
                2,
                4,
                5
            ];
            var insert = 3;
            list.addItems(base);
            list.insertItems(-base.length - 1, insert);
            expect(list._items.length).toEqual(base.length + [insert].length);
            expect(list.getItems()).toEqual([
                3,
                1,
                2,
                4,
                5
            ]);
        });
        it('should be able to remove items', function () {
            var firstItem = {
                getList: function () {
                },
                _getElement: function () {
                }
            };
            var list = new dynamicList.List('defaultListTemplate', { listTemplateDomId: 'defaultListTemplate' });
            var base = [
                firstItem,
                2,
                4,
                5
            ];
            var remove = [
                4,
                5
            ];
            spyOn(remove, 'each');
            spyOn(list, '_prepareListItem');
            list.addItems(base);
            list.removeItems(remove);
            expect(list._items.length).toEqual(base.length - remove.length);
            expect(list.getItems()).toEqual([
                firstItem,
                2
            ]);
        });
        it('should do nothing if some items not exists', function () {
            var firstItem = {
                getList: function () {
                },
                _getElement: function () {
                }
            };
            var list = new dynamicList.List('defaultListTemplate', { listTemplateDomId: 'defaultListTemplate' });
            var base = [
                firstItem,
                2,
                4,
                5
            ];
            var remove = [
                3,
                4
            ];
            spyOn(remove, 'each');
            spyOn(list, '_prepareListItem');
            list.addItems(base);
            list.removeItems(remove);
            expect(list.getItems()).toEqual([
                firstItem,
                2,
                5
            ]);
        });
        it('should\'t fail when you try to remove more items than it has', function () {
            var firstItem = {
                getList: function () {
                },
                _getElement: function () {
                }
            };
            var list = new dynamicList.List('defaultListTemplate', { listTemplateDomId: 'defaultListTemplate' });
            var base = [
                firstItem,
                2
            ];
            var remove = [
                firstItem,
                2,
                3,
                4
            ];
            spyOn(remove, 'each');
            spyOn(list, '_prepareListItem');
            list.addItems(base);
            list.removeItems(remove);
            expect(list.getItems()).toEqual([]);
        });
        it('should\'t fail if remove items are undefined', function () {
            var list = new dynamicList.List('defaultListTemplate', { listTemplateDomId: 'defaultListTemplate' });
            var base = [
                1,
                2
            ];
            spyOn(list, '_prepareListItem');
            list.addItems(base);
            list.removeItems();
            expect(list.getItems()).toEqual([
                1,
                2
            ]);
        });
        it('should sort items ', function () {
            var list = new dynamicList.List('defaultListTemplate', { listTemplateDomId: 'defaultListTemplate' });
            spyOn(list._items, 'sort');
            list.sort('val');
            expect(list._items.sort).toHaveBeenCalledWith('val');
        });
        it('should select items', function () {
            var list = new dynamicList.List('defaultListTemplate', { listTemplateDomId: 'defaultListTemplate' });
            list.addItems(items);
            list.selectItem(items[0]);
            expect(list.getSelectedItems()).toEqual([items[0]]);
            expect(list.isItemSelected(items[0])).toBeTruthy();
        });
        it('should select just one item', function () {
            var list = new dynamicList.List('defaultListTemplate', { listTemplateDomId: 'defaultListTemplate' });
            list.addItems(items);
            list.selectItem(items[0]);
            expect(list.getSelectedItems()).toEqual([items[0]]);
            list.selectItem(items[1]);
            expect(list.getSelectedItems()).toEqual([items[1]]);
            list.selectItem(items[2]);
            expect(list.getSelectedItems()).toEqual([items[2]]);
        });
        it('should select multiple items, including items, which are between first selected and second (Shift held)', function () {
            var list = new dynamicList.List('defaultListTemplate', {
                listTemplateDomId: 'defaultListTemplate',
                multiSelect: true
            });
            list.addItems(items);
            list.selectItem(items[0]);
            list.selectItem(items[2], false, true, false);
            expect(list.getSelectedItems()).toEqual(items);
        });
        it('should select multiple items, excluding items, which are between first selected and second (Ctrl held)', function () {
            var list = new dynamicList.List('defaultListTemplate', {
                listTemplateDomId: 'defaultListTemplate',
                multiSelect: true
            });
            list.addItems(items);
            list.selectItem(items[0]);
            list.selectItem(items[2], true, false, false);
            expect(list.getSelectedItems()).toEqual([
                items[0],
                items[2]
            ]);
        });
        it('should not change selection if context menu opened on selected item', function () {
            var list = new dynamicList.List('defaultListTemplate', {
                listTemplateDomId: 'defaultListTemplate',
                multiSelect: true
            });
            list.addItems(items);
            list.selectItem(items[0]);
            list.selectItem(items[2], false, true, false);
            list.selectItem(items[1], false, false, true);
            expect(list.getSelectedItems()).toEqual(items);
        });
        it('should deselect selected items and select one if context menu opened on not selected item', function () {
            var list = new dynamicList.List('defaultListTemplate', {
                listTemplateDomId: 'defaultListTemplate',
                multiSelect: true
            });
            list.addItems(items);
            list.selectItem(items[0]);
            list.selectItem(items[2], true, false, false);
            list.selectItem(items[1], false, false, true);
            expect(list.getSelectedItems()).toEqual([items[1]]);
        });
        it('should deselect selected items except given selected item', function () {
            var list = new dynamicList.List('defaultListTemplate', {
                listTemplateDomId: 'defaultListTemplate',
                multiSelect: true
            });
            list.addItems(items);
            list.selectItem(items[0]);
            list.selectItem(items[2], false, true, false);
            list.deselectOthers(items[1]);
            expect(list.getSelectedItems()).toEqual([items[1]]);
        });
        it('should deselect all items', function () {
            var list = new dynamicList.List('defaultListTemplate', {
                listTemplateDomId: 'defaultListTemplate',
                multiSelect: true
            });
            list.addItems(items);
            list.selectItem(items[0]);
            list.selectItem(items[2], false, true, false);
            list.resetSelected();
            expect(list.getSelectedItems()).toEqual([]);
        });
        it('should deselect all items and in parent list too', function () {
            var list = new dynamicList.List('defaultListTemplate', {
                listTemplateDomId: 'defaultListTemplate',
                multiSelect: true
            });
            list._parentList = {
                resetSelected: function () {
                },
                _addItemToSelected: function () {
                }
            };
            spyOn(list._parentList, 'resetSelected');
            list.addItems(items);
            list.selectItem(items[0]);
            list.selectItem(items[2], false, true, false);
            list.resetSelected(false);
            expect(list.getSelectedItems()).toEqual([]);
            expect(list._parentList.resetSelected).toHaveBeenCalled();
        });
        it('should get next item', function () {
            var list = new dynamicList.List('defaultListTemplate', {
                listTemplateDomId: 'defaultListTemplate',
                multiSelect: true
            });
            list.addItems(items);
            expect(list.getNextItem(items[0])).toEqual(items[1]);
            expect(list.getNextItem(items[1])).toEqual(items[2]);
            expect(list.getNextItem(items[2])).toEqual(undefined);
        });
        it('should get previous item', function () {
            var list = new dynamicList.List('defaultListTemplate', {
                listTemplateDomId: 'defaultListTemplate',
                multiSelect: true
            });
            list.addItems(items);
            expect(list.getPreviousItem(items[0])).toEqual(undefined);
            expect(list.getPreviousItem(items[1])).toEqual(items[0]);
            expect(list.getPreviousItem(items[2])).toEqual(items[1]);
        });
        it('should select next item on corresponding event', function () {
            addListItemsToTheBody(items);
            var list = new dynamicList.List('defaultListTemplate', {
                listTemplateDomId: 'defaultListTemplate',
                multiSelect: true
            });
            list.addItems(items);
            list.selectItem(items[0]);
            var event = {
                memo: {
                    targetEvent: {
                        shiftKey: false,
                        preventDefault: function () {
                        },
                        stopPropagation: function () {
                        }
                    }
                }
            };
            list.selectNext(event);
            expect(list.getSelectedItems()).toEqual([items[1]]);
            list.selectNext(event);
            expect(list.getSelectedItems()).toEqual([items[2]]);    //end reached
            //end reached
            list.selectNext(event);
            expect(list.getSelectedItems()).toEqual([items[2]]);
            removeListItemsFromBody(items);
        });
        it('should select next item on corresponding event (multiselect)', function () {
            addListItemsToTheBody(items);
            var list = new dynamicList.List('defaultListTemplate', {
                listTemplateDomId: 'defaultListTemplate',
                multiSelect: true
            });
            list.addItems(items);
            list.selectItem(items[0]);
            var event = {
                memo: {
                    targetEvent: {
                        shiftKey: true,
                        preventDefault: function () {
                        },
                        stopPropagation: function () {
                        }
                    }
                }
            };
            list.selectNext(event);
            expect(list.getSelectedItems()).toEqual([
                items[0],
                items[1]
            ]);
            list.selectNext(event);
            expect(list.getSelectedItems()).toEqual(items);    //end reached
            //end reached
            list.selectNext(event);
            expect(list.getSelectedItems()).toEqual(items);
            removeListItemsFromBody(items);
        });
        it('should select previous item on corresponding event', function () {
            addListItemsToTheBody(items);
            var list = new dynamicList.List('defaultListTemplate', {
                listTemplateDomId: 'defaultListTemplate',
                multiSelect: true
            });
            list.addItems(items);
            list.selectItem(items[2]);
            var event = {
                memo: {
                    targetEvent: {
                        shiftKey: false,
                        preventDefault: function () {
                        },
                        stopPropagation: function () {
                        }
                    }
                }
            };
            list.selectPrevious(event);
            expect(list.getSelectedItems()).toEqual([items[1]]);
            list.selectPrevious(event);
            expect(list.getSelectedItems()).toEqual([items[0]]);    //end reached
            //end reached
            list.selectPrevious(event);
            expect(list.getSelectedItems()).toEqual([items[0]]);
            removeListItemsFromBody(items);
        });
        it('should select previous item on corresponding event (multiselect)', function () {
            addListItemsToTheBody(items);
            var list = new dynamicList.List('defaultListTemplate', {
                listTemplateDomId: 'defaultListTemplate',
                multiSelect: true
            });
            list.addItems(items);
            list.selectItem(items[2]);
            var event = {
                memo: {
                    targetEvent: {
                        shiftKey: true,
                        preventDefault: function () {
                        },
                        stopPropagation: function () {
                        }
                    }
                }
            };
            list.selectPrevious(event);
            expect(list.getSelectedItems()).toEqual([
                items[2],
                items[1]
            ]);
            list.selectPrevious(event);
            expect(list.getSelectedItems()).toEqual([
                items[2],
                items[1],
                items[0]
            ]);    //end reached
            //end reached
            list.selectPrevious(event);
            expect(list.getSelectedItems()).toEqual([
                items[2],
                items[1],
                items[0]
            ]);
            removeListItemsFromBody(items);
        });
        it('should select inwards items if item already opened', function () {
            var list = new dynamicList.List('defaultListTemplate', {
                listTemplateDomId: 'defaultListTemplate',
                multiSelect: true
            });
            list.addItems(items);
            list.selectItem(items[0]);
            var stub = sinon.stub(list, 'getSelectedItems');
            stub.onCall(0).returns([]);
            stub.onCall(1).returns([]);
            stub.onCall(2).returns([]);
            stub.onCall(3).returns([]);
            stub.returns([items[1]]);
            sinon.spy(list, 'fire');
            spyOn(items[0], 'deselect');
            spyOn(baseList, 'isItemOpen').and.returnValue(true);
            list.selectInwards();
            expect(items[0].deselect).toHaveBeenCalled();
            expect(list.fire).toHaveBeenCalledWith(list.Event.ITEM_SELECTED, { item: items[1] });
            stub.restore();
            list.fire.restore();
        });
        it('should open sublist if it is closed', function () {
            var list = new dynamicList.List('defaultListTemplate', {
                listTemplateDomId: 'defaultListTemplate',
                multiSelect: true
            });
            list.addItems(items);
            list.selectItem(items[0]);
            spyOn(baseList, 'isItemOpen').and.returnValue(false);
            spyOn(baseList, 'openItem');
            list.selectInwards();
            expect(baseList.openItem).toHaveBeenCalled();
        });
        it('should select outwards items if item already opened and item from sublist is selected', function () {
            var list = new dynamicList.List('defaultListTemplate', {
                listTemplateDomId: 'defaultListTemplate',
                multiSelect: true
            });
            list.addItems(items);
            items[1].parentItem = items[0];
            list.selectItem(items[1]);
            spyOn(items[0], 'select');
            spyOn(items[1], 'deselect');
            spyOn(baseList, 'isItemOpen').and.returnValue(false);
            list.selectOutwards();
            expect(items[1].deselect).toHaveBeenCalled();
            expect(items[0].select).toHaveBeenCalled();
        });
        it('should close sublist parent item is selected', function () {
            var list = new dynamicList.List('defaultListTemplate', {
                listTemplateDomId: 'defaultListTemplate',
                multiSelect: true
            });
            list.addItems(items);
            list.selectItem(items[0]);
            spyOn(baseList, 'isItemOpen').and.returnValue(true);
            spyOn(baseList, 'closeItem');
            list.selectOutwards();
            expect(baseList.closeItem).toHaveBeenCalled();
        });
        it('should have possibility to show and set proper flags', function () {
            var list = new dynamicList.List('defaultListTemplate', {
                listTemplateDomId: 'defaultListTemplate',
                multiSelect: true
            });
            list.addItems(items);
            spyOn(items[0], 'show');
            spyOn(items[1], 'show');
            spyOn(items[2], 'show');
            spyOn(list, '_initEvents');
            list.show();
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
        });    /* DEFECTIVE TEST
                 * See: http://bugzilla.jaspersoft.com/show_bug.cgi?id=41581
                */
        /* DEFECTIVE TEST
                 * See: http://bugzilla.jaspersoft.com/show_bug.cgi?id=41581
                */
        // eslint-disable-next-line no-undef
        xit('should have possibility to refresh items', function () {
            var list = new dynamicList.List('defaultListTemplate', {
                listTemplateDomId: 'defaultListTemplate',
                multiSelect: true
            });
            list.addItems(items);
            var i = 0;
            list._element = {
                // This object is invalid for this purpose!
                childElements: function () {
                    return {
                        indexOf: function () {
                            return i++;
                        },
                        each: function () {
                        }
                    };
                }
            };
            spyOn(items[0], 'isRendered').and.returnValue(true);
            spyOn(items[1], 'isRendered').and.returnValue(true);
            spyOn(items[2], 'isRendered').and.returnValue(true);
            spyOn(items[0], 'refresh');
            spyOn(items[1], 'refresh');
            spyOn(items[2], 'refresh');
            list.refresh();
            expect(items[0].refresh).toHaveBeenCalled();
            expect(items[1].refresh).toHaveBeenCalled();
            expect(items[2].refresh).toHaveBeenCalled();
        });    /* DEFECTIVE TEST
                 * See: http://bugzilla.jaspersoft.com/show_bug.cgi?id=41581
                */
        /* DEFECTIVE TEST
                 * See: http://bugzilla.jaspersoft.com/show_bug.cgi?id=41581
                */
        // eslint-disable-next-line no-undef
        xit('should have possibility to refresh items', function () {
            var list = new dynamicList.List('defaultListTemplate', {
                listTemplateDomId: 'defaultListTemplate',
                multiSelect: true
            });
            list.addItems(items);
            var i = 0;
            list._element = {
                // This object is invalid for this purpose!
                childElements: function () {
                    return {
                        indexOf: function () {
                            return i++;
                        },
                        each: function () {
                        }
                    };
                }
            };
            spyOn(items[0], 'isRendered').and.returnValue(true);
            spyOn(items[1], 'isRendered').and.returnValue(true);
            spyOn(items[2], 'isRendered').and.returnValue(true);
            spyOn(items[0], 'refresh');
            spyOn(items[1], 'refresh');
            spyOn(items[2], 'refresh');
            list.refresh();
            expect(items[0].refresh).toHaveBeenCalled();
            expect(items[1].refresh).toHaveBeenCalled();
            expect(items[2].refresh).toHaveBeenCalled();
        });    /* DEFECTIVE TEST
                 * See: http://bugzilla.jaspersoft.com/show_bug.cgi?id=41581
                */
        /* DEFECTIVE TEST
                 * See: http://bugzilla.jaspersoft.com/show_bug.cgi?id=41581
                */
        // eslint-disable-next-line no-undef
        xit('should have possibility to refresh items and change their order if needed', function () {
            sinon.stub(baseList, 'isItemOpen').returns(false);
            var list = new dynamicList.List('defaultListTemplate', {
                listTemplateDomId: 'defaultListTemplate',
                multiSelect: true
            });
            list.addItems([
                items[0],
                items[2],
                items[1]
            ]);
            var i = 0;
            list._element = {
                childElements: function () {
                    return {
                        indexOf: function () {
                            return i++;
                        },
                        each: function () {
                        }
                    };
                }
            };
            spyOn(items[0], 'isRendered').and.returnValue(true);
            spyOn(items[1], 'isRendered').and.returnValue(true);
            spyOn(items[2], 'isRendered').and.returnValue(true);
            spyOn(items[0], 'refresh');
            spyOn(items[1], 'show');
            spyOn(items[2], 'show');
            list.refresh();
            expect(items[0].refresh).toHaveBeenCalled();
            expect(items[1].show).toHaveBeenCalled();
            expect(items[2].show).toHaveBeenCalled();
            baseList.isItemOpen.restore();
        });
        it('should have possibility to refresh items and render items, if they not rendered yet', function () {
            var list = new dynamicList.List('defaultListTemplate', {
                listTemplateDomId: 'defaultListTemplate',
                multiSelect: true
            });
            list.addItems(items);
            spyOn(items[0], 'isRendered').and.returnValue(false);
            spyOn(items[1], 'isRendered').and.returnValue(false);
            spyOn(items[2], 'isRendered').and.returnValue(false);
            spyOn(items[0], 'show');
            spyOn(items[1], 'show');
            spyOn(items[2], 'show');
            list.refresh();
            expect(items[0].show).toHaveBeenCalled();
            expect(items[1].show).toHaveBeenCalled();
            expect(items[2].show).toHaveBeenCalled();
        });
        it('should have possibility make item draggable and set callbacks properly', function () {
            var list = new dynamicList.List('defaultListTemplate', {
                listTemplateDomId: 'defaultListTemplate',
                dragPattern: true
            });
            list.addItems(items);

            rewire$matchAny(function() {
                return el;
            });

            spyOn(list, 'getItemByEvent').and.returnValue(false);

            dragndrop.Draggable = sandbox.stub();

            sinon.stub(Element, 'makePositioned');
            sinon.stub(Event, 'observe');
            list.createDraggableIfNeeded(event);
            expect(list.draggables[0]).toBeDefined();
            expect(dragndrop.Draggable.args[0][0]).toBe(el);
            Element.makePositioned.restore();
            Event.observe.restore();

            restoreUtilsCommon();
        });
        it('should not create draggables if draggable pattern not set', function () {
            var list = new dynamicList.List('defaultListTemplate', { listTemplateDomId: 'defaultListTemplate' });
            list.addItems(items);

            rewire$matchAny(function() {
                return {
                    identify: function () {
                        return 0;
                    }
                }
            });

            spyOn(list, 'getItemByEvent').and.returnValue(false);
            list.createDraggableIfNeeded(event);
            expect(list.draggables.length).toEqual(0);

            restoreUtilsCommon();
        });
        it('should not create draggables element already draggable', function () {
            var list = new dynamicList.List('defaultListTemplate', { listTemplateDomId: 'defaultListTemplate' });
            list.addItems(items);
            list.draggables[el.identify()] = true;
            dragndrop.Draggable = sandbox.stub();
            list.createDraggableIfNeeded(event);
            expect(dragndrop.Draggable).not.toHaveBeenCalled();
        });
        it('should not create draggables element hasn\'t draggable pattern', function () {
            var list = new dynamicList.List('defaultListTemplate', { listTemplateDomId: 'defaultListTemplate' });
            list.addItems(items);

            rewire$matchAny(function() {
                return false;
            });

            dragndrop.Draggable = sandbox.stub();

            list.createDraggableIfNeeded(event);
            expect(dragndrop.Draggable).not.toHaveBeenCalled();

            restoreUtilsCommon();
        });
    });
    describe('list item', function () {
        var items = [];
        for (var i = 0; i < 3; i++) {
            items.push(new dynamicList.ListItem({
                label: 'item' + i,
                templateDomId: dynamicList.ListItem.prototype.DEFAULT_TEMPLATE_DOM_ID
            }));
        }
        it('should not show if container not specified', function () {
            var item = new dynamicList.ListItem();
            spyOn(item, 'processTemplate');
            item.show();
            expect(item.processTemplate).not.toHaveBeenCalled();
        });
        it('should show items when list show method called', function () {
            var list = new dynamicList.List('list_control_path', { listTemplateDomId: 'list_control_path' });
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
        it('should refresh item if it is assigned to list', function () {
            var list = new dynamicList.List('list_control_path', { listTemplateDomId: 'list_control_path' });
            list.addItems(items);
            spyOn(items[0], 'refreshStyle');
            spyOn(items[0], 'processTemplate');
            items[0].refresh();
            expect(items[0].refreshStyle).toHaveBeenCalled();
            expect(items[0].processTemplate).toHaveBeenCalled();
        });
        it('should remove element on refresh, if item removed from list', function () {
            var list = new dynamicList.List('list_control_path', { listTemplateDomId: 'list_control_path' });
            list.addItems(items);
            list.show();
            var children = list._getElement().childElements().length;
            spyOn(items[0], 'getList').and.returnValue(false);
            items[0].refresh();
            expect(list._getElement().childElements().length).toEqual(children - 1);
            expect(items[0]._element).toBeFalsy();
        });
        it('should not change state of item if element not defined', function () {
            var item = new dynamicList.ListItem();
            spyOn(item, 'processTemplate');
            spyOn(item, '_getElement');    // spy return undefined
            // spy return undefined
            item.refresh();
            expect(item.processTemplate).not.toHaveBeenCalled();
            expect(item._getElement).toHaveBeenCalled();
        });
        it('should refresh styles set specific class for first item', function () {
            var list = new dynamicList.List('list_control_path', { listTemplateDomId: 'list_control_path' });
            list.addItems(items);
            list.show();
            var it = items[1];
            expect(it._getElement()).not.toHaveClass(layoutModule.FIRST_CLASS);
            it.first = true;
            it.refreshStyle();
            expect(it._getElement()).toHaveClass(layoutModule.FIRST_CLASS);
            it.first = false;
            it.refreshStyle();
            expect(it._getElement()).not.toHaveClass(layoutModule.FIRST_CLASS);
        });
        it('should refresh styles set specific class for last item', function () {
            var list = new dynamicList.List('list_control_path', { listTemplateDomId: 'list_control_path' });
            list.addItems(items);
            list.show();
            var it = items[1];
            expect(it._getElement()).not.toHaveClass(layoutModule.LAST_CLASS);
            it.last = true;
            it.refreshStyle();
            expect(it._getElement()).toHaveClass(layoutModule.LAST_CLASS);
            it.last = false;
            it.refreshStyle();
            expect(it._getElement()).not.toHaveClass(layoutModule.LAST_CLASS);
        });    //TODO see previous to do (44,16) can't describe if item is disabled or not
        //TODO see previous to do (44,16) can't describe if item is disabled or not
        // eslint-disable-next-line no-undef
        xit('should refresh styles set specific class for disabled item', function () {
            var list = new dynamicList.List('list_control_path', { listTemplateDomId: 'list_control_path' });
            list.addItems(items);
            list.show();
            var it = items[1];
            expect(it._getElement()).not.toHaveClass(layoutModule.DISABLED_CLASS);
            buttonManager.disable(it._getElement());
            it.refreshStyle();
            expect(it._getElement()).toHaveClass(layoutModule.DISABLED_CLASS);
            buttonManager.enable(it._getElement());
            it.refreshStyle();
            expect(it._getElement()).not.toHaveClass(layoutModule.DISABLED_CLASS);
        });    /* FIXME */
        /* FIXME */
        // eslint-disable-next-line no-undef
        xit('should refresh styles set specific class for disabled item', function () {
            var list = new dynamicList.List('list_control_path', { listTemplateDomId: 'list_control_path' });
            list.addItems(items);
            list.show();
            var it = items[1];
            expect(it._getElement()).not.toHaveClass(layoutModule.SELECTED_CLASS);
            list.selectItem(it);
            it.refreshStyle();
            expect(it._getElement()).toHaveClass(layoutModule.SELECTED_CLASS);
            list.deselectItem(it);
            it.refreshStyle();
            expect(it._getElement()).not.toHaveClass(layoutModule.SELECTED_CLASS);
        });
        it('should process template', function () {
            var elToProcess = (jQuery('#' + dynamicList.ListItem.prototype.DEFAULT_TEMPLATE_DOM_ID).get(0));
            var textToProcess = 'testTest';
            var item = new dynamicList.ListItem({ label: textToProcess });
            elToProcess = item.processTemplate(elToProcess);
            expect(jQuery(elToProcess).text()).toEqual(textToProcess);
        });
    });
    describe('composite list item', function () {
        var items = [];
        for (var i = 0; i < 3; i++) {
            items.push(new dynamicList.ListItem({
                label: 'item' + i,
                templateDomId: dynamicList.ListItem.prototype.DEFAULT_TEMPLATE_DOM_ID
            }));
        }
        var composite, list;
        beforeEach(function () {
            composite = new dynamicList.CompositeItem({
                items: items,
                openUp: false,
                templateDomId: dynamicList.ListItem.prototype.DEFAULT_TEMPLATE_DOM_ID
            });
            list = new dynamicList.List('list_control_path', { listTemplateDomId: 'list_control_path' });
        });
        it('should show itself and contain sublist', function () {
            list.addItems([composite]);
            spyOn(dynamicList.ListItem.prototype, 'show');
            spyOn(dynamicList.CompositeItem.prototype, '_showSubList');
            spyOn(baseList, 'closeItem');
            list.show();
            expect(dynamicList.ListItem.prototype.show).toHaveBeenCalled();
            expect(dynamicList.CompositeItem.prototype._showSubList).toHaveBeenCalled();
            expect(baseList.closeItem).toHaveBeenCalledWith(composite._getElement());
        });
        it('should show itself and not render sublist id there no subitens', function () {
            delete composite._items;
            list.addItems([composite]);
            spyOn(dynamicList.ListItem.prototype, 'show');
            spyOn(dynamicList.CompositeItem.prototype, '_showSubList');
            spyOn(baseList, 'closeItem');
            list.show();
            expect(dynamicList.ListItem.prototype.show).toHaveBeenCalled();
            expect(dynamicList.CompositeItem.prototype._showSubList).not.toHaveBeenCalled();
            expect(baseList.closeItem).toHaveBeenCalledWith(composite._getElement());
        });    // FIXME - the mocked objects below are egregiously incomplete and do not work,
        /* */
        // FIXME - the mocked objects below are egregiously incomplete and do not work,
        /* */
        // eslint-disable-next-line no-undef
        xit('should create sublist properly', function () {
            list.addItems([composite]);
            var fakeSubList = {
                show: function () {
                },
                items: [],
                getItems: function () {
                    if (!this.items.length) {
                        for (var i = 0; i < 3; i++) {
                            items.push(new dynamicList.ListItem({
                                label: 'item' + i,
                                templateDomId: dynamicList.ListItem.prototype.DEFAULT_TEMPLATE_DOM_ID
                            }));
                        }
                    }
                    return items;
                }
            };
            spyOn(dynamicList, 'List').and.returnValue(fakeSubList);
            spyOn(fakeSubList, 'show');
            spyOn(baseList, 'closeItem');
            list.show();
            expect(fakeSubList.show).toHaveBeenCalled();
            expect(composite._subList).toBe(fakeSubList);
            jQuery(fakeSubList.items).each(function (el) {
                expect(el.parentItem).toBe(composite);
            });
        });
        it('should refresh itself', function () {
            spyOn(dynamicList.ListItem.prototype, 'refresh');
            delete composite._items;
            list.addItems([composite]);
            composite.refresh();
            expect(dynamicList.ListItem.prototype.refresh).toHaveBeenCalled();
        });
        it('should refresh and if sublist not shown, show it', function () {
            list.addItems([composite]);
            spyOn(composite, '_showSubList');
            composite.refresh();
            expect(composite._showSubList).toHaveBeenCalled();
        });
        it('should refresh sublist', function () {
            list.addItems([composite]);
            list.show();
            spyOn(composite._subList, 'refresh');
            composite.refresh();
            expect(composite._subList.refresh).toHaveBeenCalled();
        });
        it('should get first child', function () {
            list.addItems([composite]);
            list.show();
            expect(composite.getFirstChild()).toBe(items[0]);
        });
        it('should refresh style itself', function () {
            list.addItems([composite]);
            list.show();
            spyOn(dynamicList.ListItem.prototype, 'refreshStyle');
            composite.refreshStyle();
            expect(dynamicList.ListItem.prototype.refreshStyle).toHaveBeenCalled();
        });
        it('should refresh style for sublist', function () {
            list.addItems([composite]);
            list.show();
            spyOn(composite._subList, 'refreshStyle');
            composite.refreshStyle();
            expect(composite._subList.refreshStyle).toHaveBeenCalled();
        });
        it('should update style of element depending on item\'s state(open)', function () {

        });
        it('should update style of element depending on item\'s state(closed)', function () {
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