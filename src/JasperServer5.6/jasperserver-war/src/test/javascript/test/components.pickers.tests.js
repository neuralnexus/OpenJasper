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
 * @version: $Id: components.pickers.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

var Droppables = {
    add:function(){}
}

var dialogs = {
    popup:jasmine.createSpyObj('popup', ['show', 'hide'])
}

beforeEach(function(){
    loadTemplates("tree.htm", "pickers.htm" );

    dialogs.popup.show.reset();
    dialogs.popup.hide.reset();
});

describe("components pickers", function(){
    describe("File selector", function(){
        var fileURI = '/folder/file';
        var folderURI = '/folder';

        var mockTree = {
            _uri:fileURI,
            openAndSelectNode: function(uri){
                 this._uri = uri;
            },
            getSelectedNode:function(){
                return {
                    FOLDER_TYPE_NAME:true,
                    param:{
                      type: mockTree._uri === folderURI,
                      uri:mockTree._uri
                    }
                }
            }
        }

        var options  = {
            uriTextboxId:'input',
            browseButtonId: 'button',
            treeId: 'tree',
            providerId:'dsTreeDataProvider',
            title: 'testtest',
            onOk: jasmine.createSpy('ok'),
            onCancel: jasmine.createSpy('cancel')

        };

        beforeEach(function(){
            jQuery('#input').attr('disabled', false).val("");
            jQuery('#button').attr('disabled', false);
            options.onOk.reset();
            options.onCancel.reset();
        });

        it("should initialize",function(){
            var selector = new picker.FileSelector(options);

            expect(selector).toBeDefined();
            expect(selector._uriTextbox).toBeDefined();
            expect(selector._browseButtonId).toBeDefined();
            expect(selector._treeDomId).toBeDefined();
            expect(selector._suffix).toBeDefined();
            expect(selector._tree).toBeDefined();
            expect(selector._okButton).toBeDefined();
            expect(selector._cancelButton).toBeDefined();

            expect(selector.visible).toBeFalsy();

            expect(jQuery('#'+selector._id+selector._suffix).length).toEqual(1);
            expect(jQuery('div.title').text().indexOf('testtest')).not.toEqual(-1);
        });

        it("should disable inputs if has option 'disabled'",function(){
            expect(jQuery('#input')).not.toBeDisabled();
            expect(jQuery('#button')).not.toBeDisabled();

            options.disabled = true;
            var selector = new picker.FileSelector(options);

            expect(jQuery('#input')).toBeDisabled();
            expect(jQuery('#button')).toBeDisabled();

            options.disabled = false;
        });

        it("should open dialog on click",function(){
            var selector = new picker.FileSelector(options);
            selector._tree = mockTree;
            selector._tree.openAndSelectNode(folderURI)

            selector._browseClickHandler({stop:function(){}});

            expect(dialogs.popup.show).toHaveBeenCalled();
            expect(selector._visible).toBeTruthy();
        });

        it("should open and select node with uri, specified in input if any",function(){
            var selector = new picker.FileSelector(options);
            selector._tree = mockTree;

            spyOn(selector._tree, 'openAndSelectNode');
            jQuery('#input').val(fileURI);
            selector._browseClickHandler({stop:function(){}});
            expect(selector._tree.openAndSelectNode).toHaveBeenCalledWith(fileURI);

            selector._tree.openAndSelectNode.reset();
            jQuery('#input').val("");
            selector._browseClickHandler({stop:function(){}});
            expect(selector._tree.openAndSelectNode).not.toHaveBeenCalled();
        });

        it("should disable ok button if none selected",function(){
            var selector = new picker.FileSelector(options);
            spyOn(mockTree, 'getSelectedNode').andReturn(null);
            selector._tree = mockTree;

            jQuery('#input').val("");
            selector._browseClickHandler({stop:function(){}});
            expect(jQuery('#'+selector.OK_BUTTON_ID+selector._suffix)).toBeDisabled();
        });

         it("should enable ok button if file selected",function(){
            var selector = new picker.FileSelector(options);
            selector._tree = mockTree;

            jQuery('#input').val(fileURI);
            selector._browseClickHandler({stop:function(){}});
            expect(jQuery('#'+selector.OK_BUTTON_ID+selector._suffix)).not.toBeDisabled();
        });

        it("should enable ok button if folder selected by default",function(){
            var selector = new picker.FileSelector(options);
            selector._tree = mockTree;

            jQuery('#input').val(folderURI);
            selector._browseClickHandler({stop:function(){}});
            expect(jQuery('#'+selector.OK_BUTTON_ID+selector._suffix)).not.toBeDisabled();
        });

        it("should disable ok button if folder selected and only files can be accepted",function(){
            options.selectLeavesOnly = true;
            var selector = new picker.FileSelector(options);
            selector._tree = mockTree;

            jQuery('#input').val(folderURI);
            selector._browseClickHandler({stop:function(){}});
            expect(jQuery('#'+selector.OK_BUTTON_ID+selector._suffix)).toBeDisabled();
            options.selectLeavesOnly = false;
        });

        it("should put URI of selected resource in the input, close dialog and run callback if any after ok button was clicked",function(){
            var selector = new picker.FileSelector(options);
            selector._tree = mockTree;
            selector._show();

            selector._dialogClickHandler({stop:function(){}, element:function(){return jQuery('#'+selector.OK_BUTTON_ID+selector._suffix)[0]}});

            expect(jQuery('#input').val()).toEqual(folderURI);
            expect(selector._visible).toBeFalsy();
            expect(dialogs.popup.hide).toHaveBeenCalled();
            expect(options.onOk).toHaveBeenCalled();
            expect(options.onCancel).not.toHaveBeenCalled();
        });

         it("should close dialog and run callback if any after cancel button was clicked",function(){
            var selector = new picker.FileSelector(options);
            selector._tree = mockTree;
            selector._show();

            selector._dialogClickHandler({stop:function(){}, element:function(){return jQuery('#'+selector.CANCEL_BUTTON_ID+selector._suffix)[0]}});

            expect(jQuery('#input').val()).toEqual('');
            expect(selector._visible).toBeFalsy();
            expect(dialogs.popup.hide).toHaveBeenCalled();
            expect(options.onOk).not.toHaveBeenCalled();
            expect(options.onCancel).toHaveBeenCalled();
        });

        it("should respond on double click on tree as if it was click on ok button (ok enabled)",function(){
            var selector = new picker.FileSelector(options);
            selector._tree = mockTree;
            selector._show();

            selector._treeClickHandler({stop:function(){}, element:function(){return jQuery('#'+selector.OK_BUTTON_ID+selector._suffix)[0]}});

            expect(jQuery('#input').val()).toEqual(folderURI);
            expect(selector._visible).toBeFalsy();
            expect(dialogs.popup.hide).toHaveBeenCalled();
            expect(options.onOk).toHaveBeenCalled();
            expect(options.onCancel).not.toHaveBeenCalled();
        });

        it("should respond on double click as if it was click on ok button (ok disabled)",function(){
            options.selectLeavesOnly = true;
            var selector = new picker.FileSelector(options);
            selector._tree = mockTree;
            selector._show();

            selector._treeClickHandler({stop:function(){}, element:function(){return jQuery('#'+selector.OK_BUTTON_ID+selector._suffix)[0]}});

            expect(jQuery('#input').val()).toEqual('');
            expect(selector._visible).toBeTruthy();
            expect(dialogs.popup.hide).not.toHaveBeenCalled();
            expect(options.onOk).not.toHaveBeenCalled();
            expect(options.onCancel).not.toHaveBeenCalled();

            options.selectLeavesOnly = false;
        });

        it("should select node in the tree if URI specified in input after nodes are loaded",function(){
            var selector = new picker.FileSelector(options);
            selector._tree = mockTree;
            jQuery('#input').val(folderURI);
            selector._show();

            selector._treeLoadHandler({stop:function(){}, element:function(){return jQuery('#'+selector.OK_BUTTON_ID+selector._suffix)[0]}});

            expect(selector._tree.getSelectedNode().param.uri).toEqual(folderURI);
        });

    });
});