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
 * @version: $Id: dynamicTree.treesupport.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery",
        "components.dynamicTree",
        "text!templates/list.htm",
        "text!templates/tree.htm"],
        function(jQuery, dynamicTree, listText, treeText) {

        describe("dynamicTree", function() {
            describe("TreeSupport", function() {
                var treeId, templateId, $ = jQuery;

                beforeEach(function() {

                    spyOn(Droppables, "add");
                    setTemplates(listText, treeText);
                    //attach object to use in test cases
                    treeId = "testTree";
                    templateId = "list_responsive_collapsible"
                });

                describe("Creation", function() {
                    it("should create tree", function() {
                        var tree = new dynamicTree.TreeSupport(treeId, {providerId: "testProvider"});

                        expect(tree).toBeDefined();
                        expect(tree instanceof dynamicTree.TreeSupport).toBeTruthy();

                        expect(tree.providerId).toEqual("testProvider");
                        expect(tree.ajaxBufferId).toEqual("ajaxbuffer");
                        expect(tree.urlGetNode).toEqual(__jrsConfigs__.contextPath + "/flow.html?_flowId=treeFlow&method=getNode");
                        expect(tree.urlGetChildren).toEqual(__jrsConfigs__.contextPath + "/flow.html?_flowId=treeFlow&method=getChildren");
                        expect(tree.urlGetMultipleChildren).toEqual(__jrsConfigs__.contextPath + "/flow.html?_flowId=treeFlow&method=getMultipleChildren");
                        expect(tree.urlGetMessage).toEqual(__jrsConfigs__.contextPath + "/flow.html?_flowId=treeFlow&method=getMessage");
                    });
                });

                describe("instance", function() {
                    var tree, okResponse, okChildResponse, treeDom;
                    beforeEach(function() {
                        tree = new dynamicTree.TreeSupport(treeId, {
                            providerId: "testProvider",
                            bShowRoot: true
                        });

                        okResponse = '<div id="treeNodeText">' + '{"id":"/","order":1,"children":[{"id":"organizations","order":1,"label":"Organizations","type":"com.jaspersoft.jasperserver.api.metadata.common.domain.Folder","uri":"/organizations"}],"label":"root","type":"com.jaspersoft.jasperserver.api.metadata.common.domain.Folder","uri":"/"}' + '</div>';
                        okChildResponse = '<div id="treeNodeText">' + '[{"id":"organization_1","order":1,"label":"Organization","type":"com.jaspersoft.jasperserver.api.metadata.common.domain.Folder","uri":"/organizations/organization_1"}]' + '</div>';

                        sinon.stub(window, "baseErrorHandler");

                        treeDom = $("#" + treeId)[0];
                    });

                    afterEach(function(){
                        window.baseErrorHandler.restore();
                    });

                    it("should fail to show tree", function() {
                        sinon.stub(window, "ajaxTargettedUpdate", function(url, options) {
                            $('#' + options.fillLocation).html("Error");
                            options.callback();
                        });
                        sinon.spy(tree, "processNode");

                        tree.showTree();

                        expect(window.ajaxTargettedUpdate.calledOnce).toBeTruthy();
                        expect(tree.processNode.called).toBeFalsy();

                        window.ajaxTargettedUpdate.restore();
                        tree.processNode.restore();
                    });

                    it("can show tree", function() {
                        sinon.stub(window, "ajaxTargettedUpdate", function(url, options) {
                            $('#' + options.fillLocation).html(okResponse);
                            options.callback();
                        });
                        sinon.spy(tree, "processNode");

                        tree.showTree();

                        expect(window.ajaxTargettedUpdate.calledOnce).toBeTruthy();
                        expect(tree.processNode.called).toBeTruthy();
                        expect(tree.rootNode).toBeDefined();
                        expect(tree.rootNode.param.id).toEqual("/");

                        window.ajaxTargettedUpdate.restore();
                        tree.processNode.restore();
                    });

                    it("can show node children", function() {
                        sinon.stub(window, "ajaxTargettedUpdate", function(url, options) {
                            $('#' + options.fillLocation).html(okResponse);
                            options.callback();
                        });

                        tree.showTree();

                        expect(tree.rootNode).toBeDefined();
                        expect(tree.rootNode.childs[0]).toBeDefined();
                        var node = tree.rootNode.childs[0];
                        expect(node.childs.length).toEqual(0);

                        window.ajaxTargettedUpdate.restore();

                        sinon.stub(window, "ajaxTargettedUpdate", function(url, options) {
                            $('#' + options.fillLocation).html(okChildResponse);
                            options.callback();
                        });
                        sinon.spy(node, "resetChilds");
                        sinon.spy(tree, "processNode");

                        tree.getTreeNodeChildren(node);

                        expect(window.ajaxTargettedUpdate.calledOnce).toBeTruthy();
                        expect(tree.processNode.called).toBeTruthy();
                        expect(node.resetChilds.called).toBeTruthy();
                        expect(node.childs.length).toEqual(1);
                        expect(node.childs[0].param.id).toEqual("organization_1");

                        window.ajaxTargettedUpdate.restore();
                        tree.processNode.restore();
                        node.resetChilds.restore();
                    });
                });

            });
        });
    });
