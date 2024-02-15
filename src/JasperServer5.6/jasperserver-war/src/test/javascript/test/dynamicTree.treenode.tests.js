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
 * @version: $Id: dynamicTree.treenode.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery",
        "components.dynamicTree",
        "text!templates/list.htm",
        "text!templates/tree.htm"],
        function(jQuery, dynamicTree, listText, treeText) {

        describe("TreeNode", function() {

            beforeEach(function() {
                setTemplates(listText, treeText);
            });

            describe("creation", function() {
                var name;

                // Set up
                beforeEach(function() {
                    name = "simple node";
                });

                xit("should fail to create node without options", function() {
                    var e;

                    if (isIE()) {
                        e = new Error("Unable to get value of the property 'name': object is null or undefined");
                    } else if (isFirefox()) {
                        e = new TypeError("options is undefined");
                    } else {
                        e = new Error("Cannot read property 'name' of undefined");
                    }

                    expect(function() {
                        new dynamicTree.TreeNode()
                    }).toThrow(e);
                });

                it("should create node", function() {
                    var last = dynamicTree.getNextId();

                    var node = new dynamicTree.TreeNode({name: name});

                    expect(node).toBeDefined();

                    expect(dynamicTree.nodes).toBeDefined();
                    expect(dynamicTree.nodes[node.id]).toBe(node);

                    expect(node.name).toEqual(name);
                    expect(node.id).toEqual(last + 1);

                    expect(node.Types).toBeDefined();
                    expect(node.Types.Folder).toBeDefined();
                    expect(node.isParent()).toBeFalsy();
                });

                it("should create node with param", function() {
                    var node = new dynamicTree.TreeNode({
                        name: name,
                        param: {id: "simpleId", type: "com.jaspersoft.jasperserver.api.metadata.common.domain.Folder"}
                    });

                    expect(node).toBeDefined();
                    expect(node.name).toEqual(name);
                    expect(node.Types).toBeDefined();
                    expect(node.Types.Folder).toBeDefined();
                    expect(node.isParent()).toBeTruthy();
                });

            });

            describe("Type", function() {
                it("should create node type", function() {
                    var name = "simple.type";
                    var type = new dynamicTree.TreeNode.Type(name);

                    expect(type).toBeDefined();
                    expect(type.name).toEqual(name);
                });

                it("should create node type with options", function() {
                    var type = new dynamicTree.TreeNode.Type("simple.type", {
                        cssClassName: "button",
                        templateDomId: "leafId"
                    });

                    expect(type).toBeDefined();
                    expect(type.cssClassName).toEqual("button");
                    expect(type.templateDomId).toEqual("leafId");
                });
            });

            describe("instance", function() {

                var treeId, rootNode, childNode1, childNode2, templateId, $ = jQuery;

                // Set up
                beforeEach(function() {
                    //attach object to use in test cases
                    treeId = "testTree";
                    templateId = "list_responsive_collapsible:leaf";
                    rootNode = new dynamicTree.TreeNode({
                        name: "Root",
                        param: {id: "root", type: "com.jaspersoft.jasperserver.api.metadata.common.domain.Folder"}
                    });

                    //            childNode1 = new dynamicTree.TreeNode({name: "Child Node 1"});
                    //            childNode2 = new dynamicTree.TreeNode({name: "Child Node 2"});
                    //
                    //            rootNode.addChild(childNode1);
                    //            rootNode.addChild(childNode2);
                });

                it("can add child", function() {
                    expect(rootNode.isParent()).toBeTruthy();

                    var node = new dynamicTree.TreeNode({name: "Simple Name"});

                    rootNode.addChild(node);
                    expect(rootNode.childs).toBeDefined();
                    expect(rootNode.childs.length).toEqual(1);
                    expect(rootNode.childs[0]).toBe(node);

                    node.addChild(new dynamicTree.TreeNode({name: "Another Simple Name"}));
                    expect(node.childs).toBeDefined();
                    expect(node.childs.length).toEqual(0);
                });

                it("can remove child", function() {
                    expect(rootNode.isParent()).toBeTruthy();

                    var node1 = new dynamicTree.TreeNode({name: "Simple Name 1"});
                    var node2 = new dynamicTree.TreeNode({name: "Simple Name 2"});

                    rootNode.addChild(node1);
                    rootNode.addChild(node2);

                    expect(rootNode.childs).toBeDefined();
                    expect(rootNode.childs.length).toEqual(2);

                    rootNode.removeChild(node2);

                    expect(rootNode.childs).toBeDefined();
                    expect(rootNode.childs.length).toEqual(1);
                    expect(rootNode.childs[0]).toBe(node1);
                });

                it("can get first child", function() {
                    expect(rootNode.isParent()).toBeTruthy();

                    var node1 = new dynamicTree.TreeNode({name: "Simple Name 1"});
                    var node2 = new dynamicTree.TreeNode({name: "Simple Name 2"});

                    rootNode.addChild(node1);
                    rootNode.addChild(node2);

                    expect(rootNode.getFirstChild()).toEqual(node1);
                    expect(node1.getFirstChild()).toBeNull();
                });

                it("can get last child", function() {
                    expect(rootNode.isParent()).toBeTruthy();

                    var node1 = new dynamicTree.TreeNode({name: "Simple Name 1"});
                    var node2 = new dynamicTree.TreeNode({name: "Simple Name 2"});

                    rootNode.addChild(node1);
                    rootNode.addChild(new dynamicTree.TreeNode({name: "Simple Name"}));
                    rootNode.addChild(node2);

                    expect(rootNode.getLastChild()).toEqual(node2);
                    expect(node2.getLastChild()).toBeNull();
                });

                it("can reset childs", function() {
                    expect(rootNode.isParent()).toBeTruthy();

                    rootNode.addChild(new dynamicTree.TreeNode({name: "Simple Name 1"}));
                    rootNode.addChild(new dynamicTree.TreeNode({name: "Simple Name 2"}));

                    expect(rootNode.childs).toBeDefined();
                    expect(rootNode.childs.length).toEqual(2);

                    rootNode.resetChilds();

                    expect(rootNode.childs).toBeDefined();
                    expect(rootNode.childs.length).toEqual(0);
                });

                it("can count children", function() {
                    expect(rootNode.isParent()).toBeTruthy();

                    rootNode.addChild(new dynamicTree.TreeNode({name: "Simple Name 1"}));
                    rootNode.addChild(new dynamicTree.TreeNode({name: "Simple Name 2"}));

                    expect(rootNode.getChildCount()).toEqual(2);
                });

                it("can has children", function() {
                    expect(rootNode.isParent()).toBeTruthy();

                    expect(rootNode.hasChilds()).toBeFalsy();

                    rootNode.setHasChilds(true);

                    expect(rootNode.hasChilds()).toBeTruthy();

                    rootNode.setHasChilds(false);
                    rootNode.addChild(new dynamicTree.TreeNode({name: "Simple Name 1"}));
                    rootNode.addChild(new dynamicTree.TreeNode({name: "Simple Name 2"}));

                    expect(rootNode.hasChilds()).toBeTruthy();
                });

                it("can sort children", function() {
                    expect(rootNode.isParent()).toBeTruthy();

                    var node1 = new dynamicTree.TreeNode({name: "Simple Name 1"});
                    var node2 = new dynamicTree.TreeNode({name: "Simple Name 2"});

                    rootNode.addChild(node1);
                    rootNode.addChild(node2);

                    rootNode.resortChilds();

                    expect(rootNode.childs[0]).toBe(node1);
                    expect(rootNode.childs[1]).toBe(node2);
                });

                it("can have state", function() {
                    expect(rootNode.isParent()).toBeTruthy();

                    var tree = new dynamicTree.Tree(treeId, {root: rootNode});
                    spyOn(tree, "getState").andReturn(dynamicTree.TreeNode.State.CLOSED);

                    rootNode.getState();
                    expect(rootNode.getState()).toEqual(dynamicTree.TreeNode.State.CLOSED);

                    expect(tree.getState).toHaveBeenCalledWith(rootNode.id);
                });

                it("can check is open", function() {
                    expect(rootNode.isParent()).toBeTruthy();

                    var tree = new dynamicTree.Tree(treeId, {root: rootNode});
                    spyOn(tree, "getState").andReturn(dynamicTree.TreeNode.State.CLOSED);

                    expect(rootNode.isOpen()).toBeFalsy();
                    expect(tree.getState).toHaveBeenCalledWith(rootNode.id);

                    var tree = new dynamicTree.Tree(treeId, {root: rootNode});
                    spyOn(tree, "getState").andReturn(dynamicTree.TreeNode.State.OPEN);

                    expect(rootNode.isOpen()).toBeTruthy();
                    expect(tree.getState).toHaveBeenCalledWith(rootNode.id);
                });

                it("can change name", function() {
                    expect(rootNode.isParent()).toBeTruthy();

                    var tree = new dynamicTree.Tree(treeId, {root: rootNode});
                    tree.renderTree();

                    rootNode.changeName("Changed Root");
                    expect(rootNode.name).toEqual("Changed Root");
                    //expect($(rootNode.NODE_ID_PREFIX + rootNode.id)[0].innerHTML).toEqual("Changed Root");
                });

                it("can have type", function() {
                    expect(rootNode.isParent()).toBeTruthy();

                    expect(rootNode.getType()).toEqual(rootNode.Types.Folder);

                    var node = new dynamicTree.TreeNode({name: "Simple Name"});
                    expect(node.getType()).toBeUndefined();
                });

                it("can check is it hidden root node", function() {
                    expect(rootNode.isParent()).toBeTruthy();

                    var tree = new dynamicTree.Tree(treeId, {root: rootNode});

                    expect(rootNode.isHiddenRootNode()).toBeTruthy();

                    var node = new dynamicTree.TreeNode({name: "Simple Name"});
                    rootNode.addChild(node);

                    expect(node.isHiddenRootNode()).toBeFalsy();

                    tree.bShowRoot = true;

                    expect(rootNode.isHiddenRootNode()).toBeFalsy();
                });

                it("can be selected", function() {
                    expect(rootNode.isParent()).toBeTruthy();

                    var node = new dynamicTree.TreeNode({name: "Simple Name", bShowRoot: true});
                    rootNode.addChild(node);

                    var tree = new dynamicTree.Tree(treeId, {root: rootNode});
                    tree.renderTree();

                    spyOn(tree, "addNodeToSelected");
                    spyOn(tree, "fireSelectEvent");
                    spyOn(rootNode, "refreshStyle");

                    expect(rootNode.select()).toBeTruthy();

                    expect(tree.addNodeToSelected).toHaveBeenCalledWith(rootNode);
                    expect(rootNode.refreshStyle).toHaveBeenCalled();
                    expect(tree.fireSelectEvent).toHaveBeenCalled();

                    spyOn(tree, "isNodeSelected").andReturn(true);

                    expect(rootNode.select()).toBeFalsy();
                });

                it("can check if selected", function() {
                    expect(rootNode.isParent()).toBeTruthy();

                    var tree = new dynamicTree.Tree(treeId, {root: rootNode, bShowRoot: true});

                    spyOn(tree, "isNodeSelected").andReturn(true);

                    expect(rootNode.isSelected()).toBeTruthy();
                });

                it("can be deselected", function() {
                    expect(rootNode.isParent()).toBeTruthy();

                    var node = new dynamicTree.TreeNode({name: "Simple Name", bShowRoot: true});
                    rootNode.addChild(node);

                    var tree = new dynamicTree.Tree(treeId, {root: rootNode});
                    tree.renderTree();

                    expect(rootNode.deselect()).toBeFalsy();

                    spyOn(tree, "removeNodeFromSelected");
                    spyOn(tree, "fireUnSelectEvent");
                    spyOn(rootNode, "refreshStyle");
                    spyOn(tree, "isNodeSelected").andReturn(true);

                    var event = {};

                    expect(rootNode.deselect(event)).toBeTruthy();

                    expect(tree.removeNodeFromSelected).toHaveBeenCalledWith(rootNode);
                    expect(rootNode.refreshStyle).toHaveBeenCalled();
                    expect(tree.fireUnSelectEvent).toHaveBeenCalled();
                });

                it("can refresh style", function() {
                    expect(rootNode.isParent()).toBeTruthy();

                    var node = new dynamicTree.TreeNode({name: "Simple Name", param: {cssClass: "server"}});
                    rootNode.addChild(node);

                    var tree = new dynamicTree.Tree(treeId, {root: rootNode, bShowRoot: true});
                    tree.renderTree();

                    var element = rootNode._getElement();

                    expect(element.className).toEqual("node open");

                    rootNode.refreshStyle();

                    expect(element.className).toEqual("node open");

                    rootNode.isWaiting = true;
                    rootNode.refreshStyle();

                    expect(element.className).toEqual("node loading");

                    rootNode.isWaiting = false;
                    rootNode.refreshStyle();

                    expect(element.className).toEqual("node open");

                    spyOn(rootNode, "isOpen").andReturn(false);
                    rootNode.refreshStyle();

                    expect(element.className).toEqual("node closed");

                    spyOn(rootNode, "isSelected").andReturn(true);
                    rootNode.refreshStyle();

                    expect(element.className).toEqual("node closed selected");

                    expect(element.down().className).toEqual("wrap button draggable");

                    rootNode.isDropTarget = true;
                    rootNode.refreshStyle();

                    expect(element.down().className).toEqual("wrap button draggable dropTarget");

                    rootNode.hidden = true;
                    rootNode.refreshStyle();

                    expect(element.className).toEqual("node closed selected hidden");

                    var nodeElement = node._getElement();

                    expect(nodeElement.className).toEqual("leaf server");

                    spyOn(node, "getType").andReturn(new dynamicTree.TreeNode.Type("custom", {cssClassName: "type"}));
                    node.refreshStyle();

                    expect(nodeElement.className).toEqual("leaf server type");
                });

                it("can be rendered", function() {
                    expect(rootNode.isParent()).toBeTruthy();

                    var element = document.createElement('div');
                    expect(element.innerHTML).toEqual('');

                    spyOn(rootNode, "_getElement").andReturn(document.createElement('div'));

                    rootNode.render(element);

                    expect(element.innerHTML.toLowerCase()).toEqual('<div></div>');
                });

                //TODO:fix it ASAP!
                xit("can be shown", function() {
                    expect(rootNode.isParent()).toBeTruthy();

                    var node = new dynamicTree.TreeNode({name: "Simple Name", bShowRoot: true});
                    rootNode.addChild(node);

                    var tree = new dynamicTree.Tree(treeId, {root: rootNode, bShowRoot: true});
                    tree.renderTree();

                    var element = document.createElement('div');
                    expect(element.innerHTML).toEqual('');

                    rootNode.showNode(element);

                    if (isIE()) {
                        if (jasmine.isOldIE()) {
                            expectedContent = '<liid=node45class="nodeopen"tabindex=-1templateid="list_responsive_collapsible:leaf"templateclassname="leaf"><pclass="wrapbuttondraggable"><bid=handler45class=icon></b>root</p><ulid=node45subclass="listcollapsible"templateid="list_responsive_collapsible"><liid=node46class=leaftabindex=-1templateid="list_responsive_collapsible:leaf"templateclassname="leaf"><pclass="wrapbuttondraggable"><bid=handler46class=icon></b>simplename</p></li></ul></li>';
                        } else {
                            expectedContent = '<li id="node' + rootNode.id + '" class="node open" tabindex="-1" >' + '<p class="wrap button draggable"><b id="handler' + rootNode.id + '" class="icon"></b>Root</p>' + '<ul id="node' + rootNode.id + 'sub" class="list collapsible">' + '<li id="node' + node.id + '" class="leaf" tabindex="-1" >' + '<p class="wrap button draggable"><b id="handler' + node.id + '" class="icon"></b>Simple Name</p>' + '</li>' + '</ul>' + '</li>';
                        }
                    } else if (isFirefox()) {
                        expectedContent = '<li tabindex="-1" id="node' + rootNode.id + '" class="node open">' + '<p class="wrap button draggable"><b id="handler' + rootNode.id + '" class="icon"></b>Root</p>' + '<ul id="node' + rootNode.id + 'sub" class="list collapsible">' + '<li tabindex="-1" id="node' + node.id + '" class="leaf">' + '<p class="wrap button draggable"><b id="handler' + node.id + '" class="icon"></b>Simple Name</p>' + '</li>' + '</ul>' + '</li>';
                    } else {
                        expectedContent = '<li id="node' + rootNode.id + '" class="node open" tabindex="-1">' + '<p class="wrap button draggable"><b class="icon" id="handler' + rootNode.id + '"></b>Root</p>' + '<ul id="node' + rootNode.id + 'sub" class="list collapsible">' + '<liid="node' + node.id + '" class="leaf" tabindex="-1" >' + '<p class="wrap button draggable"><b class="icon" id="handler' + node.id + '"></b>Simple Name</p>' + '</li>' + '</ul>' + '</li>';
                    }

                    var result = element.innerHTML.replace(/(\n|\s+)/g, '').toLowerCase();

                    expect(result).toEqual(expectedContent.replace(/(\n|\s+)/g, '').toLowerCase());
                });

                it("can be refreshed", function() {
                    expect(rootNode.isParent()).toBeTruthy();

                    var node = new dynamicTree.TreeNode({name: "Simple Name"});
                    rootNode.addChild(node);

                    var tree = new dynamicTree.Tree(treeId, {root: rootNode, bShowRoot: true});
                    tree.renderTree();

                    rootNode.isloaded = true;
                    spyOn(rootNode, "isParent").andReturn(true);
                    spyOn(rootNode, "isOpen").andReturn(true);

                    spyOn(rootNode, "refreshStyle");
                    spyOn(node, "showNode");

                    expect(rootNode.getChildCount()).toBeGreaterThan(0);

                    rootNode.refreshNode();

                    expect(rootNode.refreshStyle).toHaveBeenCalled();
                    expect(node.showNode).toHaveBeenCalled();
                });

                it("can have wait state", function() {
                    expect(rootNode.isParent()).toBeTruthy();
                    expect(rootNode.isWaiting).toBeFalsy();

                    spyOn(rootNode, "refreshStyle");

                    rootNode.wait();

                    expect(rootNode.isWaiting).toBeTruthy();
                    expect(rootNode.refreshStyle).toHaveBeenCalled();

                });

                it("can disable wait state", function() {
                    expect(rootNode.isParent()).toBeTruthy();

                    rootNode.isWaiting = true;

                    spyOn(rootNode, "refreshStyle");

                    rootNode.stopWaiting();

                    expect(rootNode.isWaiting).toBeFalsy();
                    expect(rootNode.refreshStyle).toHaveBeenCalled();

                });

                it("can be open", function() {
                    expect(rootNode.isParent()).toBeTruthy();

                    var tree = new dynamicTree.Tree(treeId, {root: rootNode, bShowRoot: true});
                    tree.renderTree();

                    spyOn(tree, "writeStates");
                    spyOn(tree, "fireOpenEvent");
                    spyOn(rootNode, "isOpen").andReturn(false);
                    spyOn(rootNode, "refreshStyle");

                    rootNode.handleNode();

                    expect(tree.writeStates).toHaveBeenCalledWith(rootNode.id, dynamicTree.TreeNode.State.OPEN);
                    expect(tree.fireOpenEvent).toHaveBeenCalled();
                    expect(rootNode.refreshStyle).toHaveBeenCalled();
                });

                it("can be closed", function() {
                    expect(rootNode.isParent()).toBeTruthy();

                    var tree = new dynamicTree.Tree(treeId, {root: rootNode, bShowRoot: true});
                    tree.renderTree();

                    spyOn(tree, "writeStates");
                    spyOn(rootNode, "isOpen").andReturn(true);
                    spyOn(rootNode, "refreshStyle");

                    rootNode.handleNode();

                    expect(tree.writeStates).toHaveBeenCalledWith(rootNode.id, dynamicTree.TreeNode.State.CLOSED);
                    expect(rootNode.refreshStyle).toHaveBeenCalled();
                });
            });

        });
    });

