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
import BaseDataSourceModel from 'src/dataSource/model/BaseDataSourceModel';
import ResourceModel from 'bi-repository/src/bi/repository/model/RepositoryResourceModel';
import dialogs from 'src/components/components.dialogs';

describe('Testing BaseDataSourceModel', function () {
    var baseDataSourceModel, stub = {};
    it('BaseDataSourceModel should be defined', function () {
        expect(BaseDataSourceModel).toBeDefined();
        expect(typeof BaseDataSourceModel).toEqual("function");
    });
    it('BaseDataSourceModel should have initialize method', function () {
        expect(BaseDataSourceModel.prototype.initialize).toBeDefined();
        expect(typeof BaseDataSourceModel.prototype.initialize).toEqual("function");
    });
    it('BaseDataSourceModel should have testConnection method', function () {
        expect(BaseDataSourceModel.prototype.testConnection).toBeDefined();
        expect(typeof BaseDataSourceModel.prototype.testConnection).toEqual("function");
    });
    it('BaseDataSourceModel initialize method should call its parent', function () {
        stub = sinon.stub(ResourceModel.prototype, 'initialize');
        baseDataSourceModel = new BaseDataSourceModel({ parentFolderUri: '/aaa/bbb/ccc' });
        expect(stub).toHaveBeenCalled();
        ResourceModel.prototype.initialize.restore();
    });
    describe('BaseDataSourceModel testing testConnection() method', function () {
        var validationResult;
        beforeEach(function () {
            stub.initialize = sinon.stub(ResourceModel.prototype, 'initialize');
            validationResult = false;
            stub.validate = sinon.stub(ResourceModel.prototype, 'validate').callsFake(function () {
                baseDataSourceModel._isValid = validationResult;
            });
            baseDataSourceModel = new BaseDataSourceModel({ parentFolderUri: '/aaa/bbb/ccc' });
        });
        afterEach(function () {
            ResourceModel.prototype.validate.restore();
            ResourceModel.prototype.initialize.restore();
        });
        it('BaseDataSourceModel testConnection method should call validate method', function () {
            baseDataSourceModel.testConnection();
            expect(stub.validate).toHaveBeenCalled();
        });
        it('BaseDataSourceModel testConnection method should launch the ajax loading timer and stop it', function () {
            var doRemove = {};
            if (!window.AjaxRequester) {
                window.AjaxRequester = { prototype: { MAX_WAIT_TIME: 10 } };
                doRemove.AjaxRequester = true;
            }
            if (!window.ajax) {
                window.ajax = { LOADING_ID: '' };
                doRemove.ajax = true;
            }
            if (!window.dialogs) {
                window.dialogs = {
                    popup: {
                        show: function () {
                        },
                        hide: function () {
                        }
                    }
                };
                doRemove.dialogs = true;
            }
            var drd = $.Deferred();
            stub.ajax = sinon.stub($, 'ajax').callsFake(function () {
                return drd;
            });
            stub.show = sinon.stub(dialogs.popup, 'show');
            stub.hide = sinon.stub(dialogs.popup, 'hide');
            sinon.stub(window, 'setTimeout').callsFake(function (fun) {
                fun();
            });
            sinon.stub(window, 'clearTimeout');    // launch the connection testing...
            // launch the connection testing...
            validationResult = true;
            baseDataSourceModel.testConnection();    // check expectations...
            // check expectations...
            expect(stub.validate).toHaveBeenCalled();
            expect(stub.show).toHaveBeenCalled();
            expect(stub.ajax).toHaveBeenCalled();    // resolve the ajax
            // resolve the ajax
            drd.resolve();    // check expectations....
            // check expectations....
            expect(stub.hide).toHaveBeenCalled();
            $.ajax.restore();
            dialogs.popup.show.restore();
            dialogs.popup.hide.restore();
            window.setTimeout.restore();
            window.clearTimeout.restore();
            if (doRemove.AjaxRequester) {
                delete window.AjaxRequester;
            }
            if (doRemove.ajax) {
                delete window.ajax;
            }
            if (doRemove.dialogs) {
                delete window.dialogs;
            }
        });
    });
});