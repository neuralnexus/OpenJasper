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

import jQuery from 'jquery';
import dynamicTree from 'src/dynamicTree/dynamicTree.treesupport';
import listText from './test/templates/list.htm';
import treeText from './test/templates/tree.htm';
import setTemplates from 'js-sdk/test/tools/setTemplates';

describe('TreeNode', function () {
    beforeEach(function () {
        setTemplates(listText, treeText);
    });
    describe('creation', function () {
        var name;    // Set up
        // Set up
        beforeEach(function () {
            name = 'simple node';
        });
        it('should create node', function () {
            var last = dynamicTree.getNextId();
            var node = new dynamicTree.TreeNode({ name: name });
            expect(node).toBeDefined();
            expect(dynamicTree.nodes).toBeDefined();
            expect(dynamicTree.nodes[node.id]).toBe(node);
            expect(node.name).toEqual(name);
            expect(node.id).toEqual(last + 1);
            expect(node.Types).toBeDefined();
            expect(node.Types.Folder).toBeDefined();
            expect(node.isParent()).toBeFalsy();
        });
        it('should create node with param', function () {
            var node = new dynamicTree.TreeNode({
                name: name,
                param: {
                    id: 'simpleId',
                    type: 'com.jaspersoft.jasperserver.api.metadata.common.domain.Folder'
                }
            });
            expect(node).toBeDefined();
            expect(node.name).toEqual(name);
            expect(node.Types).toBeDefined();
            expect(node.Types.Folder).toBeDefined();
            expect(node.isParent()).toBeTruthy();
        });
    });
    describe('Type', function () {
        it('should create node type', function () {
            var name = 'simple.type';
            var type = new dynamicTree.TreeNode.Type(name);
            expect(type).toBeDefined();
            expect(type.name).toEqual(name);
        });
        it('should create node type with options', function () {
            var type = new dynamicTree.TreeNode.Type('simple.type', {
                cssClassName: 'button',
                templateDomId: 'leafId'
            });
            expect(type).toBeDefined();
            expect(type.cssClassName).toEqual('button');
            expect(type.templateDomId).toEqual('leafId');
        });
    });
    describe('instance', function () {
        var treeId, rootNode, childNode1, childNode2, templateId, $ = jQuery;    // Set up
        // Set up
        beforeEach(function () {
            //attach object to use in test cases
            treeId = 'testTree';
            templateId = 'list_responsive_collapsible:leaf';
            rootNode = new dynamicTree.TreeNode({
                name: 'Root',
                param: {
                    id: 'root',
                    type: 'com.jaspersoft.jasperserver.api.metadata.common.domain.Folder'
                }
            });    //            childNode1 = new dynamicTree.TreeNode({name: "Child Node 1"});
            //            childNode2 = new dynamicTree.TreeNode({name: "Child Node 2"});
            //
            //            rootNode.addChild(childNode1);
            //            rootNode.addChild(childNode2);
        });
        it('can add child', function () {
            expect(rootNode.isParent()).toBeTruthy();
            var node = new dynamicTree.TreeNode({ name: 'Simple Name' });
            rootNode.addChild(node);
            expect(rootNode.childs).toBeDefined();
            expect(rootNode.childs.length).toEqual(1);
            expect(rootNode.childs[0]).toBe(node);
            node.addChild(new dynamicTree.TreeNode({ name: 'Another Simple Name' }));
            expect(node.childs).toBeDefined();
            expect(node.childs.length).toEqual(0);
        });
        it('can remove child', function () {
            expect(rootNode.isParent()).toBeTruthy();
            var node1 = new dynamicTree.TreeNode({ name: 'Simple Name 1' });
            var node2 = new dynamicTree.TreeNode({ name: 'Simple Name 2' });
            rootNode.addChild(node1);
            rootNode.addChild(node2);
            expect(rootNode.childs).toBeDefined();
            expect(rootNode.childs.length).toEqual(2);
            rootNode.removeChild(node2);
            expect(rootNode.childs).toBeDefined();
            expect(rootNode.childs.length).toEqual(1);
            expect(rootNode.childs[0]).toBe(node1);
        });
        it('can get first child', function () {
            expect(rootNode.isParent()).toBeTruthy();
            var node1 = new dynamicTree.TreeNode({ name: 'Simple Name 1' });
            var node2 = new dynamicTree.TreeNode({ name: 'Simple Name 2' });
            rootNode.addChild(node1);
            rootNode.addChild(node2);
            expect(rootNode.getFirstChild()).toEqual(node1);
            expect(node1.getFirstChild()).toBeNull();
        });
        it('can get last child', function () {
            expect(rootNode.isParent()).toBeTruthy();
            var node1 = new dynamicTree.TreeNode({ name: 'Simple Name 1' });
            var node2 = new dynamicTree.TreeNode({ name: 'Simple Name 2' });
            rootNode.addChild(node1);
            rootNode.addChild(new dynamicTree.TreeNode({ name: 'Simple Name' }));
            rootNode.addChild(node2);
            expect(rootNode.getLastChild()).toEqual(node2);
            expect(node2.getLastChild()).toBeNull();
        });
        it('can reset childs', function () {
            expect(rootNode.isParent()).toBeTruthy();
            rootNode.addChild(new dynamicTree.TreeNode({ name: 'Simple Name 1' }));
            rootNode.addChild(new dynamicTree.TreeNode({ name: 'Simple Name 2' }));
            expect(rootNode.childs).toBeDefined();
            expect(rootNode.childs.length).toEqual(2);
            rootNode.resetChilds();
            expect(rootNode.childs).toBeDefined();
            expect(rootNode.childs.length).toEqual(0);
        });
        it('can count children', function () {
            expect(rootNode.isParent()).toBeTruthy();
            rootNode.addChild(new dynamicTree.TreeNode({ name: 'Simple Name 1' }));
            rootNode.addChild(new dynamicTree.TreeNode({ name: 'Simple Name 2' }));
            expect(rootNode.getChildCount()).toEqual(2);
        });
        it('can has children', function () {
            expect(rootNode.isParent()).toBeTruthy();
            expect(rootNode.hasChilds()).toBeFalsy();
            rootNode.setHasChilds(true);
            expect(rootNode.hasChilds()).toBeTruthy();
            rootNode.setHasChilds(false);
            rootNode.addChild(new dynamicTree.TreeNode({ name: 'Simple Name 1' }));
            rootNode.addChild(new dynamicTree.TreeNode({ name: 'Simple Name 2' }));
            expect(rootNode.hasChilds()).toBeTruthy();
        });
        it('can sort children', function () {
            expect(rootNode.isParent()).toBeTruthy();
            var node1 = new dynamicTree.TreeNode({ name: 'Simple Name 1' });
            var node2 = new dynamicTree.TreeNode({ name: 'Simple Name 2' });
            rootNode.addChild(node1);
            rootNode.addChild(node2);
            rootNode.resortChilds();
            expect(rootNode.childs[0]).toBe(node1);
            expect(rootNode.childs[1]).toBe(node2);
        });
        it('can have state', function () {
            expect(rootNode.isParent()).toBeTruthy();
            var tree = new dynamicTree.Tree(treeId, { root: rootNode });
            spyOn(tree, 'getState').and.returnValue(dynamicTree.TreeNode.State.CLOSED);
            rootNode.getState();
            expect(rootNode.getState()).toEqual(dynamicTree.TreeNode.State.CLOSED);
            expect(tree.getState).toHaveBeenCalledWith(rootNode.id);
        });
        it('can check is open', function () {
            expect(rootNode.isParent()).toBeTruthy();
            var tree = new dynamicTree.Tree(treeId, { root: rootNode });
            spyOn(tree, 'getState').and.returnValue(dynamicTree.TreeNode.State.CLOSED);
            expect(rootNode.isOpen()).toBeFalsy();
            expect(tree.getState).toHaveBeenCalledWith(rootNode.id);
            var tree = new dynamicTree.Tree(treeId, { root: rootNode });
            spyOn(tree, 'getState').and.returnValue(dynamicTree.TreeNode.State.OPEN);
            expect(rootNode.isOpen()).toBeTruthy();
            expect(tree.getState).toHaveBeenCalledWith(rootNode.id);
        });
        it('can change name', function () {
            expect(rootNode.isParent()).toBeTruthy();
            var tree = new dynamicTree.Tree(treeId, { root: rootNode });
            tree.renderTree();
            rootNode.changeName('Changed Root');
            expect(rootNode.name).toEqual('Changed Root');    //expect($(rootNode.NODE_ID_PREFIX + rootNode.id)[0].innerHTML).toEqual("Changed Root");
        });
        it('can have type', function () {
            expect(rootNode.isParent()).toBeTruthy();
            expect(rootNode.getType()).toEqual(rootNode.Types.Folder);
            var node = new dynamicTree.TreeNode({ name: 'Simple Name' });
            expect(node.getType()).toBeUndefined();
        });
        it('can check is it hidden root node', function () {
            expect(rootNode.isParent()).toBeTruthy();
            var tree = new dynamicTree.Tree(treeId, { root: rootNode });
            expect(rootNode.isHiddenRootNode()).toBeTruthy();
            var node = new dynamicTree.TreeNode({ name: 'Simple Name' });
            rootNode.addChild(node);
            expect(node.isHiddenRootNode()).toBeFalsy();
            tree.bShowRoot = true;
            expect(rootNode.isHiddenRootNode()).toBeFalsy();
        });
        it('can be selected', function () {
            expect(rootNode.isParent()).toBeTruthy();
            var node = new dynamicTree.TreeNode({
                name: 'Simple Name',
                bShowRoot: true
            });
            rootNode.addChild(node);
            var tree = new dynamicTree.Tree(treeId, { root: rootNode });
            tree.renderTree();
            spyOn(tree, 'addNodeToSelected');
            spyOn(tree, 'fireSelectEvent');
            spyOn(rootNode, 'refreshStyle');
            expect(rootNode.select()).toBeTruthy();
            expect(tree.addNodeToSelected).toHaveBeenCalledWith(rootNode);
            expect(rootNode.refreshStyle).toHaveBeenCalled();
            expect(tree.fireSelectEvent).toHaveBeenCalled();
            spyOn(tree, 'isNodeSelected').and.returnValue(true);
            expect(rootNode.select()).toBeFalsy();
        });
        it('can check if selected', function () {
            expect(rootNode.isParent()).toBeTruthy();
            var tree = new dynamicTree.Tree(treeId, {
                root: rootNode,
                bShowRoot: true
            });
            spyOn(tree, 'isNodeSelected').and.returnValue(true);
            expect(rootNode.isSelected()).toBeTruthy();
        });
        it('can be deselected', function () {
            expect(rootNode.isParent()).toBeTruthy();
            var node = new dynamicTree.TreeNode({
                name: 'Simple Name',
                bShowRoot: true
            });
            rootNode.addChild(node);
            var tree = new dynamicTree.Tree(treeId, { root: rootNode });
            tree.renderTree();
            expect(rootNode.deselect()).toBeFalsy();
            spyOn(tree, 'removeNodeFromSelected');
            spyOn(tree, 'fireUnSelectEvent');
            spyOn(rootNode, 'refreshStyle');
            spyOn(tree, 'isNodeSelected').and.returnValue(true);
            var event = {};
            expect(rootNode.deselect(event)).toBeTruthy();
            expect(tree.removeNodeFromSelected).toHaveBeenCalledWith(rootNode);
            expect(rootNode.refreshStyle).toHaveBeenCalled();
            expect(tree.fireUnSelectEvent).toHaveBeenCalled();
        });
        it('can refresh style', function () {
            expect(rootNode.isParent()).toBeTruthy();
            var node = new dynamicTree.TreeNode({
                name: 'Simple Name',
                param: { cssClass: 'server' }
            });
            rootNode.addChild(node);
            var tree = new dynamicTree.Tree(treeId, {
                root: rootNode,
                bShowRoot: true
            });
            tree.renderTree();
            var element = rootNode._getElement();
            expect(element.className).toEqual('node open');
            rootNode.refreshStyle();
            expect(element.className).toEqual('node open');
            rootNode.isWaiting = true;
            rootNode.refreshStyle();
            expect(element.className).toEqual('node loading');
            rootNode.isWaiting = false;
            rootNode.refreshStyle();
            expect(element.className).toEqual('node open');
            spyOn(rootNode, 'isOpen').and.returnValue(false);
            rootNode.refreshStyle();
            expect(element.className).toEqual('node closed');
            spyOn(rootNode, 'isSelected').and.returnValue(true);
            rootNode.refreshStyle();
            expect(element.className).toEqual('node closed selected');
            expect(element.down().className).toEqual('wrap button draggable');
            rootNode.isDropTarget = true;
            rootNode.refreshStyle();
            expect(element.down().className).toEqual('wrap button draggable dropTarget');
            rootNode.hidden = true;
            rootNode.refreshStyle();
            expect(element.className).toEqual('node closed selected hidden');
            var nodeElement = node._getElement();
            expect(nodeElement.className).toEqual('leaf server');
            spyOn(node, 'getType').and.returnValue(new dynamicTree.TreeNode.Type('custom', { cssClassName: 'type' }));
            node.refreshStyle();
            expect(nodeElement.className).toEqual('leaf server type');
        });
        it('can be rendered', function () {
            expect(rootNode.isParent()).toBeTruthy();
            var element = document.createElement('div');
            expect(jQuery(element).html()).toEqual('');
            spyOn(rootNode, '_getElement').and.returnValue(document.createElement('div'));
            rootNode.render(element);
            expect(jQuery(element).html().toLowerCase()).toEqual('<div></div>');
        });
        it('can be shown', function () {
            expect(rootNode.isParent()).toBeTruthy();
            var node = new dynamicTree.TreeNode({
                name: 'Simple Name',
                bShowRoot: true
            });
            rootNode.addChild(node);
            var tree = new dynamicTree.Tree(treeId, {
                root: rootNode,
                bShowRoot: true
            });
            tree.renderTree();
            var element = document.createElement('div');
            expect(element.children.length).toEqual(0);
            rootNode.showNode(element);
            var child = element.children[0];
            var nextId = rootNode.id + 1;
            expect(element.children.length).toEqual(1);
            expect(child.id).toEqual('node' + rootNode.id);
            expect(jQuery.trim(jQuery(child).find('#node' + nextId + ' > p').text())).toEqual('Simple Name');
        });
        it('can be refreshed', function () {
            expect(rootNode.isParent()).toBeTruthy();
            var node = new dynamicTree.TreeNode({ name: 'Simple Name' });
            rootNode.addChild(node);
            var tree = new dynamicTree.Tree(treeId, {
                root: rootNode,
                bShowRoot: true
            });
            tree.renderTree();
            rootNode.isloaded = true;
            spyOn(rootNode, 'isParent').and.returnValue(true);
            spyOn(rootNode, 'isOpen').and.returnValue(true);
            spyOn(rootNode, 'refreshStyle');
            spyOn(node, 'showNode');
            expect(rootNode.getChildCount()).toBeGreaterThan(0);
            rootNode.refreshNode();
            expect(rootNode.refreshStyle).toHaveBeenCalled();
            expect(node.showNode).toHaveBeenCalled();
        });
        it('can have wait state', function () {
            expect(rootNode.isParent()).toBeTruthy();
            expect(rootNode.isWaiting).toBeFalsy();
            spyOn(rootNode, 'refreshStyle');
            rootNode.wait();
            expect(rootNode.isWaiting).toBeTruthy();
            expect(rootNode.refreshStyle).toHaveBeenCalled();
        });
        it('can disable wait state', function () {
            expect(rootNode.isParent()).toBeTruthy();
            rootNode.isWaiting = true;
            spyOn(rootNode, 'refreshStyle');
            rootNode.stopWaiting();
            expect(rootNode.isWaiting).toBeFalsy();
            expect(rootNode.refreshStyle).toHaveBeenCalled();
        });
        it('can be open', function () {
            expect(rootNode.isParent()).toBeTruthy();
            var tree = new dynamicTree.Tree(treeId, {
                root: rootNode,
                bShowRoot: true
            });
            tree.renderTree();
            spyOn(tree, 'writeStates');
            spyOn(tree, 'fireOpenEvent');
            spyOn(rootNode, 'isOpen').and.returnValue(false);
            spyOn(rootNode, 'refreshStyle');
            rootNode.handleNode();
            expect(tree.writeStates).toHaveBeenCalledWith(rootNode.id, dynamicTree.TreeNode.State.OPEN);
            expect(tree.fireOpenEvent).toHaveBeenCalled();
            expect(rootNode.refreshStyle).toHaveBeenCalled();
        });
        it('can be closed', function () {
            expect(rootNode.isParent()).toBeTruthy();
            var tree = new dynamicTree.Tree(treeId, {
                root: rootNode,
                bShowRoot: true
            });
            tree.renderTree();
            spyOn(tree, 'writeStates');
            spyOn(rootNode, 'isOpen').and.returnValue(true);
            spyOn(rootNode, 'refreshStyle');
            rootNode.handleNode();
            expect(tree.writeStates).toHaveBeenCalledWith(rootNode.id, dynamicTree.TreeNode.State.CLOSED);
            expect(rootNode.refreshStyle).toHaveBeenCalled();
        });
    });
});