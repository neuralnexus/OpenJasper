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

/*global spyOn*/
import {Droppables} from 'dragdropextra';
import jQuery from 'jquery';
import dynamicTree from 'src/dynamicTree/dynamicTree.treesupport';
import listText from './test/templates/list.htm';
import treeText from './test/templates/tree.htm';
import setTemplates from 'js-sdk/test/tools/setTemplates';
import {rewire$isSupportsTouch, restore} from 'src/util/utils.common';

describe('dynamicTree', function () {
    beforeEach(function () {
        setTemplates(listText, treeText);
        rewire$isSupportsTouch(function() {
            return false;
        });
    });
    afterEach(function () {
        restore();
    });
    describe('Utils', function () {
        var propName;
        beforeEach(function () {
            propName = 'testProp';
        });
        it('can generate new node id', function () {
            var last = dynamicTree.getNextId();
            expect(dynamicTree.getNextId()).toEqual(last + 1);
            expect(dynamicTree.getNextId()).toEqual(last + 2);
            expect(dynamicTree.getNextId()).toEqual(last + 3);
        });
        it('can create cookie', function () {
            dynamicTree.setStorageVal(propName, 'setPropTest');
            expect(JSON.parse(window.localStorage.dynamicTree)[propName]).toEqual('setPropTest');
        });
        it('can get cookie', function () {
            dynamicTree.setStorageVal(propName, 'getPropTest');
            var value = dynamicTree.getStorageVal(propName);
            expect(value).toEqual('getPropTest');
        });
    });
    describe('Tree', function () {
        var treeId, templateId, $ = jQuery;
        beforeEach(function () {
            spyOn(Droppables, 'add');    //attach object to use in test cases
            //attach object to use in test cases
            treeId = 'testTree';
            templateId = 'list_responsive_collapsible';
        });
        describe('Creation', function () {
            it('should create tree', function () {
                var tree = new dynamicTree.Tree(treeId);
                expect(tree).toBeDefined();
                expect(tree instanceof dynamicTree.Tree).toBeTruthy();
                expect(dynamicTree.trees).toBeDefined();
                expect(dynamicTree.trees[treeId]).toBe(tree);
                expect(tree.id).toEqual(treeId);
                expect(tree.getId()).toEqual(treeId);
                expect(tree.templateDomId).toEqual('list_responsive_collapsible');
                expect(tree.rootNode).not.toBeDefined();
                var treeDom = $('#' + treeId);
                expect(treeDom[0].className).toEqual('list collapsible hideRoot');
                expect(Droppables.add).toHaveBeenCalled();
            });
            it('should create tree with custom styles', function () {
                var tree = new dynamicTree.Tree(treeId, {
                    treeClassName: 'newCssClass',
                    bShowRoot: true
                });
                expect(tree).toBeDefined();
                var treeDom = $('#' + treeId);
                expect(treeDom[0].className).toEqual('list collapsible newCssClass');
            });
        });
        describe('instance', function () {
            var rootNode, childNode1, childNode2, tree, treeDom;
            beforeEach(function () {
                childNode1 = new dynamicTree.TreeNode({ name: 'Child Node 1' });
                childNode2 = new dynamicTree.TreeNode({ name: 'Child Node 2' });
                rootNode = new dynamicTree.TreeNode({
                    name: 'Root',
                    param: {
                        id: 'root',
                        type: 'com.jaspersoft.jasperserver.api.metadata.common.domain.Folder'
                    }
                });
                rootNode.addChild(childNode1);
                rootNode.addChild(childNode2);
                rootNode.addChild(new dynamicTree.TreeNode({ name: 'Child Node 2' }));
                tree = new dynamicTree.Tree(treeId, {
                    root: rootNode,
                    bShowRoot: true
                });
                treeDom = $('#' + treeId)[0];
            });
            it('can have node', function () {
                expect(tree.rootNode).toEqual(rootNode);
                var node = new dynamicTree.TreeNode({
                    name: 'NewRoot',
                    param: {
                        id: 'root',
                        type: 'com.jaspersoft.jasperserver.api.metadata.common.domain.Folder'
                    }
                });
                tree.setRootNode(node);
                expect(tree.rootNode).toBeDefined();
                expect(tree.getRootNode()).toBe(node);
                expect(node.getTreeId()).toBe(tree.getId());
            });
            it('can refresh style', function () {
                expect(tree).toBeDefined();
                expect(treeDom.className).toEqual('list collapsible');
                tree.treeClassName = 'changedClass';
                tree.refreshStyle();
                expect(treeDom.className).toEqual('list collapsible changedClass');
                tree.bShowRoot = false;
                tree.refreshStyle();
                expect(treeDom.className).toEqual('list collapsible changedClass hideRoot');
            });
            it('can be rendered', function () {
                expect(tree).toBeDefined();
                spyOn(tree, 'readStates');
                spyOn(tree, 'stopWaiting');
                spyOn(tree, 'refreshStyle');
                spyOn(tree, 'writeStates');
                spyOn(tree, 'refreshScroll');
                spyOn(rootNode, 'showNode');
                spyOn(rootNode, 'render');
                tree.renderTree();
                expect(tree.readStates).toHaveBeenCalled();
                expect(tree.stopWaiting).toHaveBeenCalled();
                expect(tree.refreshStyle).toHaveBeenCalled();
                expect(tree.writeStates).toHaveBeenCalledWith(rootNode.id, dynamicTree.TreeNode.State.OPEN);
                expect(rootNode.showNode).toHaveBeenCalled();
                expect(rootNode.render).toHaveBeenCalledWith(tree._getElement());
                expect(tree.refreshScroll).toHaveBeenCalled();
            });
            it('can add to selected nodes', function () {
                expect(tree.selectedNodes.length).toEqual(0);
                tree.addNodeToSelected(childNode1);
                expect(tree.selectedNodes.length).toEqual(1);
                expect(tree.selectedNodes[0]).toBe(childNode1);
                tree.addNodeToSelected(childNode2);
                expect(tree.selectedNodes.length).toEqual(2);
                expect(tree.selectedNodes[0]).toBe(childNode1);
                expect(tree.selectedNodes[1]).toBe(childNode2);
            });
            it('can remove from selected nodes', function () {
                tree.addNodeToSelected(childNode1);
                tree.addNodeToSelected(childNode2);
                expect(tree.selectedNodes.length).toEqual(2);
                tree.removeNodeFromSelected(childNode1);
                expect(tree.selectedNodes.length).toEqual(1);
                expect(tree.selectedNodes[0]).toBe(childNode2);
            });
            it('can check is node selected', function () {
                tree.addNodeToSelected(childNode1);
                expect(tree.isNodeSelected(childNode1)).toBeTruthy();
                expect(tree.isNodeSelected(childNode2)).toBeFalsy();
            });
            it('can have selected node', function () {
                expect(tree.getSelectedNode()).toBeNull();
                tree.addNodeToSelected(childNode1);
                expect(tree.getSelectedNode()).not.toBeNull();
                expect(tree.getSelectedNode()).toBe(childNode1);
            });
            it('can reset selected nodes', function () {
                tree.addNodeToSelected(childNode1);
                tree.addNodeToSelected(childNode2);
                expect(tree.selectedNodes.length).toBeGreaterThan(0);
                tree.resetSelected();
                expect(tree.selectedNodes.length).toEqual(0);
            });
            it('can sort nodes', function () {
                spyOn(tree, 'resortSubtree');
                tree.sortNodes = false;
                tree.resortTree();
                expect(tree.resortSubtree).not.toHaveBeenCalled();
                tree.sortNodes = true;
                tree.resortTree();
                expect(tree.resortSubtree).toHaveBeenCalledWith(rootNode);
            });
            it('can sort subtree', function () {
                spyOn(rootNode, 'resortChilds');
                spyOn(childNode1, 'resortChilds');
                spyOn(childNode2, 'resortChilds');
                tree.resortSubtree(rootNode);
                expect(rootNode.resortChilds).toHaveBeenCalled();
                expect(childNode1.resortChilds).toHaveBeenCalled();
                expect(childNode2.resortChilds).toHaveBeenCalled();
            });
            it('can sort nodes by name', function () {
                expect(tree.sortByName(childNode1, childNode2)).toEqual(-1);
                expect(tree.sortByName(childNode2, childNode1)).toEqual(1);
            });
            it('can sort nodes by order', function () {
                expect(tree.sortByOrder(childNode1, childNode2)).toEqual(0);
                childNode1.orderNumber = 1;
                childNode2.orderNumber = 2;
                expect(tree.sortByOrder(childNode1, childNode2)).toEqual(-1);
                childNode1.orderNumber = 2;
                childNode2.orderNumber = 1;
                expect(tree.sortByOrder(childNode1, childNode2)).toEqual(1);
                childNode1.orderNumber = null;
                childNode2.orderNumber = 1;
                expect(tree.sortByOrder(childNode1, childNode2)).toEqual(1);
                childNode1.orderNumber = 2;
                childNode2.orderNumber = null;
                expect(tree.sortByOrder(childNode1, childNode2)).toEqual(-1);
            });
            it('can compare nodes', function () {
                expect(tree.sorters.length).toEqual(2);
                expect(tree.sorters).toContain(tree.sortByOrder);
                expect(tree.sorters).toContain(tree.sortByName);
                expect(tree.comparer(childNode1, childNode2)).toEqual(-1);
            });
            it('can check write states', function () {
                tree.stateObject = {};
                spyOn(dynamicTree, 'setStorageVal');
                tree.writeStates(1, dynamicTree.TreeNode.State.CLOSED);
                expect(tree.stateObject[1]).toEqual('closed');
                expect(dynamicTree.setStorageVal).toHaveBeenCalledWith('treetestTree', { 1: 'closed' });
                tree.writeStates(1, dynamicTree.TreeNode.State.OPEN);
                expect(tree.stateObject[1]).toEqual('open');
                expect(dynamicTree.setStorageVal.calls.mostRecent().args).toEqual([
                    'treetestTree',
                    { 1: 'open' }
                ]);
            });
            it('can reset states', function () {
                spyOn(dynamicTree, 'setStorageVal');
                tree.writeStates(1, dynamicTree.TreeNode.State.OPEN);
                tree.writeStates(2, dynamicTree.TreeNode.State.CLOSED);
                expect(tree.stateObject).toEqual({
                    1: 'open',
                    2: 'closed'
                });
                tree.resetStates();
                expect(tree.stateObject).toEqual({});
            });
            it('can check node state', function () {
                spyOn(dynamicTree, 'setStorageVal');
                tree.writeStates(1, dynamicTree.TreeNode.State.OPEN);
                tree.writeStates(2, dynamicTree.TreeNode.State.CLOSED);
                expect(tree.getState(1)).toEqual(dynamicTree.TreeNode.State.OPEN);
                expect(tree.getState(2)).toEqual(dynamicTree.TreeNode.State.CLOSED);
                expect(tree.getState(3)).toEqual(dynamicTree.TreeNode.State.CLOSED);
            });
            it('can read states', function () {
                tree.statearray = {};
                spyOn(dynamicTree, 'getStorageVal').and.returnValue({
                    1: 'open',
                    2: 'closed'
                });
                tree.readStates();
                expect(tree.stateObject).toEqual({
                    1: 'open',
                    2: 'closed'
                });
                expect(tree.getState(1)).toEqual(dynamicTree.TreeNode.State.OPEN);
                expect(tree.getState(2)).toEqual(dynamicTree.TreeNode.State.CLOSED);
            });
            it('can search node', function () {
                var searchNode = new dynamicTree.TreeNode({ name: 'Child Node 5' });
                rootNode.addChild(new dynamicTree.TreeNode({ name: 'Child Node 3' }));
                rootNode.addChild(new dynamicTree.TreeNode({ name: 'Child Node 4' }));
                rootNode.addChild(searchNode);
                rootNode.addChild(new dynamicTree.TreeNode({ name: 'Child Node 6' }));
                rootNode.addChild(new dynamicTree.TreeNode({ name: 'Child Node 7' }));
                expect(tree.binarySearchOfNode(rootNode.childs, searchNode)).toEqual(5);
                expect(tree.binarySearchOfNode(rootNode.childs, childNode2)).toEqual(2);
            });
            it('can select node', function () {
                tree.renderTree();
                var rootNodeTarget = $('#' + rootNode.NODE_ID_PREFIX + rootNode.id).children();
                var node1Target = $('#' + childNode1.NODE_ID_PREFIX + childNode1.id).children();
                var node2Target = $('#' + childNode2.NODE_ID_PREFIX + childNode2.id).children();
                expect(tree.selectOnMousedown).toBeFalsy();
                expect(tree.selectedNodes.length).toEqual(0);
                rootNodeTarget.simulate('mousedown');
                expect(tree.selectedNodes.length).toEqual(0);
                rootNodeTarget.simulate('mouseup');
                expect(tree.selectedNodes.length).toEqual(1);
                expect(tree.selectedNodes[0]).toBe(rootNode);
                node1Target.simulate('mouseup');
                expect(tree.selectedNodes.length).toEqual(1);
            });    //TODO: find out why it crashs (only on MacOs)
            //TODO: find out why it crashs (only on MacOs)
            // eslint-disable-next-line no-undef
            xit('can multiselect node', function () {
                tree.multiSelectEnabled = true;
                tree.renderTree();
                var rootNodeTarget = $('#' + rootNode.NODE_ID_PREFIX + rootNode.id).children();
                var node1Target = $('#' + childNode1.NODE_ID_PREFIX + childNode1.id).children();
                var node2Target = $('#' + childNode2.NODE_ID_PREFIX + childNode2.id).children();
                expect(tree.selectOnMousedown).toBeFalsy();
                expect(tree.selectedNodes.length).toEqual(0);
                node1Target.simulate('mouseup');
                expect(tree.selectedNodes.length).toEqual(1);
                expect(tree.selectedNodes[0]).toBe(childNode1);
                node2Target.simulate('mouseup', { ctrlKey: true });
                expect(tree.selectedNodes.length).toEqual(2);
                expect(tree.selectedNodes[0]).toBe(childNode1);
                expect(tree.selectedNodes[1]).toBe(childNode2);
                rootNodeTarget.simulate('mouseup');
                expect(tree.selectedNodes.length).toEqual(1);
                expect(tree.selectedNodes[0]).toBe(rootNode);
                node1Target.simulate('mouseup');
                expect(tree.selectedNodes[0]).toBe(childNode1);
                node2Target.simulate('mouseup', { shiftKey: true });
                expect(tree.selectedNodes.length).toEqual(2);
                expect(tree.selectedNodes[0]).toBe(childNode1);
                expect(tree.selectedNodes[1]).toBe(childNode2);
                node2Target.simulate('mouseup', { ctrlKey: true });
                expect(tree.selectedNodes.length).toEqual(1);
                expect(tree.selectedNodes[0]).toBe(childNode1);
            });
            it('can handle node by icon', function () {
                tree.renderTree();
                spyOn(rootNode, 'handleNode').and.callThrough();
                var rootNodeTarget = $('#' + rootNode.NODE_ID_PREFIX + rootNode.id + ' b');
                rootNodeTarget.simulate('click');
                expect(rootNode.handleNode).toHaveBeenCalled();
            });
            it('can handle node on dblclick by title', function () {
                tree.handleNodeOnDblclick = true;
                tree.renderTree();
                spyOn(rootNode, 'handleNode').and.callThrough();
                var rootNodeTarget = $('#' + rootNode.NODE_ID_PREFIX + rootNode.id).children();
                rootNodeTarget.simulate('dblclick');
                expect(rootNode.handleNode).toHaveBeenCalled();
            });
        });
    });
});