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
import {rewire$copyTable, restore as restoreUtilsCommon} from 'src/util/utils.common';
import {baseErrorHandler} from 'src/core/core.ajax.utils';

import {
    ajax,
    targettedResponseHandler,
    getXMLHTTP,
    AjaxRequester,
    invokeCallbacks,
    handleResponse,
    cumulativeResponseHandler,
    rowCopyResponseHandler,
    evalJSONResponseHandler,
    ajaxTargettedUpdate,
    ajaxNonReturningUpdate,
    ajaxTargettedFormSubmit,
    ajaxUpdate,
    checkForErrors,
    getPostData,
    ajaxRequestStarted,
    ajaxRequestEnded,

    rewire$getPostData,
    rewire$appendPostData,
    rewire$ajaxRequestStarted,
    rewire$targettedResponseHandler,
    rewire$evalJSONResponseHandler,
    rewire$rowCopyResponseHandler,
    rewire$cumulativeResponseHandler,
    rewire$ajaxUpdate,
    rewire$invokeCallbacks,
    rewire$updateUsingResponseSubset,
    rewire$getXMLHTTP,
    rewire$handleResponse,
    rewire$ajaxRequestEnded,
    restore as restoreCoreAjax
} from '../../../src/core/core.ajax';
import jQuery from 'jquery';
import dialogs from 'src/components/components.dialogs';
import ajaxText from './test/templates/ajax.htm';
import setTemplates from 'js-sdk/test/tools/setTemplates';
import {isIE} from 'src/util/utils.common';
import {JRS} from 'src/namespace/namespace';

describe('core ajax', function () {
    var clock,
        getXmlHttpStub,
        sandbox;

    beforeEach(function () {
        sandbox = sinon.createSandbox();
        sandbox.useFakeTimers();


        getXmlHttpStub = sandbox.stub().returns({
            open: sandbox.stub(),
            send: sandbox.stub(),
            setRequestHeader: sandbox.stub()
        });

        setTemplates(ajaxText);
        clock = sandbox.clock;
    });
    afterEach(function () {
        sandbox.restore();
        restoreCoreAjax();
        restoreUtilsCommon();
    });
    describe('requester', function () {
        it('should obtain XHR', function () {
            var xnr = getXMLHTTP();
            expect(xnr).toBeDefined();
            if (!isIE()) {
                expect(typeof xnr.open).toEqual("function");
                expect(typeof xnr.send).toEqual("function");
                expect(typeof xnr.setRequestHeader).toEqual("function");
            }
        });
        it('should do GET request', function () {
            rewire$getXMLHTTP(getXmlHttpStub);

            var requester = new AjaxRequester('some/uri', [], '');
            spyOn(requester.xmlhttp, 'open');
            spyOn(requester.xmlhttp, 'send');
            spyOn(requester.xmlhttp, 'setRequestHeader');
            expect(requester.doGet()).toBeTruthy();
            expect(requester.xmlhttp.open).toHaveBeenCalledWith('GET', 'some/uri', true);
            expect(requester.xmlhttp.send).toHaveBeenCalledWith(null);
        });
        it('should do POST request', function () {
            rewire$getXMLHTTP(getXmlHttpStub);
            var data = '!@#$%^&';
            var requester = new AjaxRequester('some/uri', [], data);
            spyOn(requester.xmlhttp, 'open');
            spyOn(requester.xmlhttp, 'send');
            spyOn(requester.xmlhttp, 'setRequestHeader');
            expect(requester.doPost()).toBeTruthy();
            expect(requester.xmlhttp.open).toHaveBeenCalledWith('POST', 'some/uri', true);
            expect(requester.xmlhttp.send).toHaveBeenCalledWith(data);
        });
        it('should return false if XNR not initialized', function () {
            var requester = new AjaxRequester('', [], '');
            delete requester.xmlhttp;
            expect(requester.doPost()).toBeFalsy();
            expect(requester.doGet()).toBeFalsy();
        });
        it('should provide callback function for XNR', function () {
            var requester = new AjaxRequester('', [], '');
            var callback = requester.processResponse();
            expect(typeof callback).toEqual("function");
        });
        it('callback should respond on state 4', function () {
            var requester = new AjaxRequester('', [], '');
            requester.xmlhttp = { readyState: 4 };

            let handleReponseStub = sandbox.stub();
            rewire$handleResponse(handleReponseStub);

            let ajaxRequestEndedStub = sandbox.stub();
            rewire$ajaxRequestEnded(ajaxRequestEndedStub);

            var callback = requester.processResponse(requester);
            callback();
            expect(handleReponseStub).toHaveBeenCalled();
            expect(ajaxRequestEndedStub).not.toHaveBeenCalled();
        });
        it('callback should not do anything if state is not 4', function () {
            var requester = new AjaxRequester('', [], '');
            requester.xmlhttp = { readyState: 3 };

            let handleReponseStub = sandbox.stub();
            rewire$handleResponse(handleReponseStub);

            let ajaxRequestEndedStub = sandbox.stub();
            rewire$ajaxRequestEnded(ajaxRequestEndedStub);

            var callback = requester.processResponse(requester);
            callback();
            expect(handleReponseStub).not.toHaveBeenCalled();
            expect(ajaxRequestEndedStub).not.toHaveBeenCalled();
        });
        it('callback should not do anything if state is not 4', function () {
            var requester = new AjaxRequester('', [], '');
            requester.xmlhttp = { readyState: 3 };

            let handleReponseStub = sandbox.stub();
            rewire$handleResponse(handleReponseStub);

            let ajaxRequestEndedStub = sandbox.stub();
            rewire$ajaxRequestEnded(ajaxRequestEndedStub);

            var callback = requester.processResponse(requester);
            callback();
            expect(handleReponseStub).not.toHaveBeenCalled();
            expect(ajaxRequestEndedStub).not.toHaveBeenCalled();
        });    // TODO (core.ajax.js:133) ajax.cancelRequestsBefore and requester.requestTime really not used,ajax.cancelRequestsBefore always undefined, ajaxRequestEnded never run from here. Remove ?
        // TODO verifyAjaxResponse & confirmContinue seems to be deprecated (never called, uses undefined variables) Remove?
        // TODO (core.ajax.js:133) ajax.cancelRequestsBefore and requester.requestTime really not used,ajax.cancelRequestsBefore always undefined, ajaxRequestEnded never run from here. Remove ?
        // TODO verifyAjaxResponse & confirmContinue seems to be deprecated (never called, uses undefined variables) Remove?
        it('callback should not do anything request time less than cancelRequestsBefore', function () {
            var requester = new AjaxRequester('', [], '');
            requester.xmlhttp = { readyState: 4 };

            let handleReponseStub = sandbox.stub();
            rewire$handleResponse(handleReponseStub);

            let ajaxRequestEndedStub = sandbox.stub();
            rewire$ajaxRequestEnded(ajaxRequestEndedStub);

            ajax.cancelRequestsBefore = 111;
            requester.requestTime = 90;
            var callback = requester.processResponse(requester);
            callback();
            expect(handleReponseStub).not.toHaveBeenCalled();
            expect(ajaxRequestEndedStub).toHaveBeenCalled();
            ajax.cancelRequestsBefore = 0;
        });
        it('should have MAX_WAIT_TIME defined', function () {
            var requester = new AjaxRequester('', [], '');
            expect(requester.MAX_WAIT_TIME).toBeDefined();
        });
        it('should open Loading popup after some time if request not finished ', function () {
            var requester = new AjaxRequester('', [], '');
            sandbox.stub(dialogs.popup, 'show');
            requester.startResponseTimer();
            clock.tick(requester.MAX_WAIT_TIME);
            expect(dialogs.popup.show.called).toBeTruthy();
        });
        it('should be able to cancel showing Loading popup', function () {
            var requester = new AjaxRequester('', [], '');
            sandbox.stub(window, 'clearTimeout');
            requester.startResponseTimer();
            requester.cancelResponseTimer();
            expect(window.clearTimeout.calledWith(requester.responseTimer)).toBeTruthy();
        });
    });
    describe('handlers', function () {
        var predefinedText = 'wwwa';
        beforeEach(function () {
            jQuery('#destination').text(predefinedText);
            jQuery('#table tr').remove();
        });    // TODO (core.ajax.js:152) wrong name of parameter ( setErrorHandler <-> errorHandler)
        // TODO (core.ajax.js:152) wrong name of parameter ( setErrorHandler <-> errorHandler)
        it('should call handler if check for errors passed', function () {
            var requester = new AjaxRequester('', [], '');

            requester.responseHandler = sandbox.stub();

            let ajaxRequestEndedStub = sandbox.stub();
            rewire$ajaxRequestEnded(ajaxRequestEndedStub);

            //requester.setErrorHandler(function(){return false});
            //requester.setErrorHandler(function(){return false});
            requester.errorHandler = sandbox.stub();

            handleResponse(requester);
            expect(requester.responseHandler).toHaveBeenCalled();
            expect(ajaxRequestEndedStub).toHaveBeenCalled();    // find out, why responseHandler called before ajaxRequestEnded
            //  expect(requester.responseHandler).toHaveBeenCalledBefore(window.ajaxRequestEnded);
            // find out, why responseHandler called before ajaxRequestEnded
            //  expect(requester.responseHandler).toHaveBeenCalledBefore(window.ajaxRequestEnded);
            expect(JRS.vars.ajax_in_progress).toBeFalsy();
        });
        it('should not call handler if check for errors not passed', function () {
            var requester = new AjaxRequester('', [], '');
            requester.responseHandler = sandbox.stub();

            let ajaxRequestEndedStub = sandbox.stub();
            rewire$ajaxRequestEnded(ajaxRequestEndedStub);

            //requester.setErrorHandler(function(){return true});
            //requester.setErrorHandler(function(){return true});
            requester.errorHandler = function () {
                return true;
            };
            sandbox.spy(requester, "errorHandler");

            handleResponse(requester);
            expect(requester.responseHandler).not.toHaveBeenCalled();
            expect(ajaxRequestEndedStub).toHaveBeenCalled();
            expect(JRS.vars.ajax_in_progress).toBeFalsy();
        });
        it('should do targetted update', function () {
            var resposeText = '<div>rerere</div>';    //response is always wrapped with html tag
            //response is always wrapped with html tag
            var requester = new AjaxRequester('', [], '');
            requester.params = [
                'destination',
                false,
                'callback'
            ];
            requester.xmlhttp = { responseText: resposeText };
            targettedResponseHandler(requester);
            expect(jQuery('#destination').text()).toEqual('rerere');
        });
        it('The targetted update text should be hard escaped twice if it starts with <div> (legacy code).', function () {
            var responseText = '<div id="dest"><ScrIpt>rerere</ScrIpt></div>';    //response is always wrapped with html tag
            //response is always wrapped with html tag
            var requester = new AjaxRequester('', [], '');
            requester.params = [
                'destination',
                false,
                'callback'
            ];
            requester.xmlhttp = { responseText: responseText };
            targettedResponseHandler(requester);
            var el = jQuery('#dest')[0];

            expect(el.childNodes[0] instanceof Text).toEqual(true);
            // outerHTML escapes > of the tag if the < is escaped.  In code, only < is escaped
            expect(el.outerHTML).toEqual('<div id="dest">&amp;lt;ScrIpt&amp;gt;rerere&amp;lt;/ScrIpt&amp;gt;</div>');
        });
        it('The targetted update text should be softHTMLescape\'d if not starting with <div>. \'div\' is whitelisted in xssUtil.', function () {
            var responseText = '<!-- dummy --><div id="dest"><ScrIpt>rerere</ScrIpt></div>';    //response is always wrapped with html tag
            //response is always wrapped with html tag
            var requester = new AjaxRequester('', [], '');
            requester.params = [
                'destination',
                false,
                'callback'
            ];
            requester.xmlhttp = { responseText: responseText };
            targettedResponseHandler(requester);
            var el = jQuery('#dest')[0];    // outerHTML escapes > of the tag if the < is escaped.  In code, only < is escaped.
            // outerHTML escapes > of the tag if the < is escaped.  In code, only < is escaped.
            expect(el.outerHTML).toEqual('<div id="dest">&lt;ScrIpt&gt;rerere&lt;/ScrIpt&gt;</div>');
        });
        it('should do targetted update for specified element', function () {
            var resposeText = 'rerere';
            var requester = new AjaxRequester('', [], '');
            requester.params = [
                'destination',
                'someId',
                'callback'
            ];

            let updateUsingResponseSubsetStub = sandbox.stub();
            rewire$updateUsingResponseSubset(updateUsingResponseSubsetStub);

            targettedResponseHandler(requester);
            expect(updateUsingResponseSubsetStub).toHaveBeenCalledWith(requester.xmlhttp, 'someId', jQuery('#destination')[0]);
        });
        it('should invoke callback after targetted update', function () {
            var resposeText = 'rerere';
            var requester = new AjaxRequester('', [], '');
            requester.params = [
                'destination',
                'someId',
                'callback'
            ];

            let updateUsingResponseSubsetStub = sandbox.stub();
            rewire$updateUsingResponseSubset(updateUsingResponseSubsetStub);
            let invokeCallbacksStub = sandbox.stub();
            rewire$invokeCallbacks(invokeCallbacksStub);

            targettedResponseHandler(requester);
            expect(updateUsingResponseSubsetStub).toHaveBeenCalled();
            expect(invokeCallbacksStub).toHaveBeenCalled();
        });
        it('should do cumulative update', function () {
            var resposeText = 'rerere';
            var requester = new AjaxRequester('', [], '');
            requester.params = [
                'destination',
                false,
                'callback'
            ];
            requester.xmlhttp = { responseText: resposeText };
            cumulativeResponseHandler(requester);
            expect(jQuery('#destination').text()).toEqual(predefinedText + resposeText);
        });
        it('should do cumulative update for specified element', function () {
            var resposeText = 'rerere';
            var requester = new AjaxRequester('', [], '');
            requester.params = [
                'destination',
                'someId',
                'callback'
            ];
            let updateUsingResponseSubsetStub = sandbox.stub();
            rewire$updateUsingResponseSubset(updateUsingResponseSubsetStub);

            cumulativeResponseHandler(requester);
            expect(updateUsingResponseSubsetStub).toHaveBeenCalledWith(requester.xmlhttp, 'someId', jQuery('#destination')[0]);
        });
        it('should invoke callback after cumulative update', function () {
            var resposeText = 'rerere';
            var requester = new AjaxRequester('', [], '');
            requester.params = [
                'destination',
                'someId',
                'callback'
            ];
            let updateUsingResponseSubsetStub = sandbox.stub();
            rewire$updateUsingResponseSubset(updateUsingResponseSubsetStub);
            let invokeCallbacksStub = sandbox.stub();
            rewire$invokeCallbacks(invokeCallbacksStub);

            cumulativeResponseHandler(requester);
            expect(updateUsingResponseSubsetStub).toHaveBeenCalled();
            expect(invokeCallbacksStub).toHaveBeenCalled();
        });
        it('should insert table in row update', function () {
            var resposeText = '<table><tr><td>A</td></tr></table>';
            var requester = new AjaxRequester('', [], '');
            requester.params = [
                'table',
                false,
                'callback'
            ];
            requester.xmlhttp = { responseText: resposeText };

            let invokeCallbacksStub = sandbox.stub();
            rewire$invokeCallbacks(invokeCallbacksStub);

            rowCopyResponseHandler(requester);
            expect(jQuery('#table tr td').text()).toEqual('A');
        });    //TODO rowCopyResponseHandler fails silently if response is not a table
        /*     it("should do nothing if response is not table in row update",function(){
                 var resposeText = '<div></div>';
                 var requester = new AjaxRequester('', [], '');
                 requester.params = ['table', false, 'callback'];
                 requester.xmlhttp = {responseText:resposeText};

                 spyOn(window,'invokeCallbacks');
                 spyOn(window,'copyTable');
                 spyOn(window,'alert');

                 rowCopyResponseHandler(requester);

                 expect(window.invokeCallbacks).not.toHaveBeenCalled();
                 expect(window.copyTable).not.toHaveBeenCalled();
                 });  */
        //TODO rowCopyResponseHandler fails silently if response is not a table
        /*     it("should do nothing if response is not table in row update",function(){
                 var resposeText = '<div></div>';
                 var requester = new AjaxRequester('', [], '');
                 requester.params = ['table', false, 'callback'];
                 requester.xmlhttp = {responseText:resposeText};

                 spyOn(window,'invokeCallbacks');
                 spyOn(window,'copyTable');
                 spyOn(window,'alert');

                 rowCopyResponseHandler(requester);

                 expect(window.invokeCallbacks).not.toHaveBeenCalled();
                 expect(window.copyTable).not.toHaveBeenCalled();
                 });  */
        it('should do nothing if target element is not a table in row update', function () {
            var resposeText = '<div></div>';
            var requester = new AjaxRequester('', [], '');
            requester.params = [
                'destination',
                false,
                'callback'
            ];
            requester.xmlhttp = { responseText: resposeText };

            let invokeCallbacksStub = sandbox.stub();
            rewire$invokeCallbacks(invokeCallbacksStub);

            let copyTableStub = sandbox.stub();
            rewire$copyTable(copyTableStub);

            spyOn(window, 'alert');

            rowCopyResponseHandler(requester);

            expect(invokeCallbacksStub).not.toHaveBeenCalled();
            expect(copyTableStub).not.toHaveBeenCalled();
        });
        it('should get JSON from server and parse it', function () {
            var resposeText = '{"a":"field", "b":1, "c":2}';
            var responceObject = {
                a: 'field',
                b: 1,
                c: 2
            };
            var requester = new AjaxRequester('', [], '');
            requester.params = [
                'destination',
                false,
                'callback'
            ];
            requester.xmlhttp = { responseText: resposeText };
            let invokeCallbacksStub = sandbox.stub();
            rewire$invokeCallbacks(invokeCallbacksStub);
            evalJSONResponseHandler(requester);
            expect(invokeCallbacksStub).toHaveBeenCalledWith('callback', responceObject);
        });
        it('should invoke callback even if JSON is malformed and parse fails', function () {
            var resposeText = '{"a":"field++ , "b":1, "c":2}';
            var responceObject = null;
            var requester = new AjaxRequester('', [], '');
            requester.params = [
                'destination',
                false,
                'callback'
            ];
            requester.xmlhttp = { responseText: resposeText };
            let invokeCallbacksStub = sandbox.stub();
            rewire$invokeCallbacks(invokeCallbacksStub);
            evalJSONResponseHandler(requester);
            expect(invokeCallbacksStub).toHaveBeenCalledWith('callback', responceObject);
        });
        it('invokeCallback should invoke function, passed as argument', function () {
            var sample = {
                callback: function () {
                }
            };
            spyOn(sample, 'callback');
            invokeCallbacks(sample.callback);
            expect(sample.callback).toHaveBeenCalled();
        });    // TODO  invokeCallback allows to pass just one argument to function, add support for multiple arguments?
        // TODO  invokeCallback allows to pass just one argument to function, add support for multiple arguments?
        it('invokeCallback should invoke function, passed as argument and pass additional arguments to it', function () {
            var sample = {
                callback: function () {
                }
            };
            spyOn(sample, 'callback');
            invokeCallbacks(sample.callback, sample);
            expect(sample.callback).toHaveBeenCalledWith(sample);
        });
    });
    describe('public API', function () {
        var url = 'http://localhost:8080';
        let ajaxUpdateStub;

        beforeEach(function () {
            ajaxUpdateStub = sandbox.stub();
            rewire$ajaxUpdate(ajaxUpdateStub);
        });

        it('should use cumulativeResponseHandler for ajaxTargettedUpdate if corresponding mode specified', function () {
            let cumulativeResponseHandlerStub = sandbox.stub();
            rewire$cumulativeResponseHandler(cumulativeResponseHandlerStub);

            ajaxTargettedUpdate(url, { mode: AjaxRequester.prototype.CUMULATIVE_UPDATE });
            expect(ajaxUpdateStub).toHaveBeenCalled();
            expect(typeof ajaxUpdateStub.args[0][1].responseHandler).toEqual("function");
            ajaxUpdateStub.args[0][1].responseHandler();
            expect(cumulativeResponseHandlerStub).toHaveBeenCalled();
        });
        it('should use rowCopyResponseHandler for ajaxTargettedUpdate if corresponding mode specified', function () {

            let rowCopyResponseHandlerStub = sandbox.stub();
            rewire$rowCopyResponseHandler(rowCopyResponseHandlerStub);

            ajaxTargettedUpdate(url, { mode: AjaxRequester.prototype.ROW_COPY_UPDATE });
            expect(ajaxUpdateStub).toHaveBeenCalled();
            expect(typeof ajaxUpdateStub.args[0][1].responseHandler).toEqual("function");
            ajaxUpdateStub.args[0][1].responseHandler();
            expect(rowCopyResponseHandlerStub).toHaveBeenCalled();
        });
        it('should use evalJSONResponseHandler for ajaxTargettedUpdate if corresponding mode specified', function () {
            let evalJSONResponseHandlerStub = sandbox.stub();
            rewire$evalJSONResponseHandler(evalJSONResponseHandlerStub);

            ajaxTargettedUpdate(url, { mode: AjaxRequester.prototype.EVAL_JSON });
            expect(ajaxUpdateStub).toHaveBeenCalled();
            expect(typeof ajaxUpdateStub.args[0][1].responseHandler).toEqual("function");
            ajaxUpdateStub.args[0][1].responseHandler();
            expect(evalJSONResponseHandlerStub).toHaveBeenCalled();
        });
        it('should use targettedResponseHandler for ajaxTargettedUpdate by default', function () {
            let targettedResponseHandlerStub = sandbox.stub();
            rewire$targettedResponseHandler(targettedResponseHandlerStub);

            ajaxTargettedUpdate(url, {});
            expect(ajaxUpdateStub).toHaveBeenCalled();
            expect(typeof ajaxUpdateStub.args[0][1].responseHandler).toEqual("function");
            ajaxUpdateStub.args[0][1].responseHandler();
            expect(targettedResponseHandlerStub).toHaveBeenCalled();
        });
        it('should run preFillAction if specified for ajaxTargettedUpdate', function () {
            var preFillAction = jasmine.createSpy('preFillAction');

            let targettedResponseHandlerStub = sandbox.stub();
            rewire$targettedResponseHandler(targettedResponseHandlerStub);

            ajaxTargettedUpdate(url, { preFillAction: preFillAction });
            expect(ajaxUpdateStub).toHaveBeenCalled();
            expect(typeof ajaxUpdateStub.args[0][1].responseHandler).toEqual("function");
            ajaxUpdateStub.args[0][1].responseHandler();
            expect(preFillAction).toHaveBeenCalled();
        });
        it('should not use any response handlers for ajaxNonReturningUpdate', function () {
            ajaxNonReturningUpdate(url, {});
            expect(ajaxUpdateStub).toHaveBeenCalled();
            expect(ajaxUpdateStub.args[0][1].responseHandler).toBe(null);
        });
        it('should parse valued from form, send it, and use targettedResponseHandler', function () {
            var samplePOSTData = { a: 1 };

            let getPostDataStub = sandbox.stub();
            rewire$getPostData(getPostDataStub);
            let appendPostDataStub = sandbox.stub().returns(samplePOSTData);
            rewire$appendPostData(appendPostDataStub);

            ajaxTargettedFormSubmit(false, url, {});
            expect(ajaxUpdateStub).toHaveBeenCalled();
            expect(ajaxUpdateStub.args[0][1].responseHandler).toBe(targettedResponseHandler);
            expect(ajaxUpdateStub.args[0][1].postData).toBe(samplePOSTData);
        });
    });
    describe('private functions', function () {
        var url = 'http://localhost:8080';
        var options = {
            fillLocation: '',
            fromLocation: '',
            callback: false,
            postData: false,
            responseHandler: true
        };
        it('should do GET request if postData not specified', function () {
            let ajaxRequestStartedStub = sandbox.stub();
            rewire$ajaxRequestStarted(ajaxRequestStartedStub);

            spyOn(AjaxRequester.prototype, 'doGet');
            ajaxUpdate(url, options);
            expect(ajaxRequestStartedStub).toHaveBeenCalled();
            expect(AjaxRequester.prototype.doGet).toHaveBeenCalled();
        });
        it('should do POST request if postData not specified', function () {
            let ajaxRequestStartedStub = sandbox.stub();
            rewire$ajaxRequestStarted(ajaxRequestStartedStub);

            spyOn(AjaxRequester.prototype, 'doPost');
            options.postData = true;
            ajaxUpdate(url, options);
            expect(ajaxRequestStartedStub).toHaveBeenCalled();
            expect(AjaxRequester.prototype.doPost).toHaveBeenCalled();
            options.postData = false;
        });
        it('should be able to handle errors in requester', function () {
            var requester = { errorHandler: jasmine.createSpy() };
            checkForErrors(requester);
            expect(requester.errorHandler).toHaveBeenCalled();
        });
        it('should be able to getPostData', function () {
            expect(getPostData(jQuery('#post')[0], {})).toEqual('checkedCheckbox=on&textBox=sample%2Ftext');
        });
        it('should exclude elements, set in additional data', function () {
            expect(getPostData(jQuery('#post')[0], { textBox: true })).toEqual('checkedCheckbox=on');
        });
        it('should handle server errors', function () {
            spyOn(dialogs.errorPopup, 'show');
            var agent = { status: 500 };
            expect(baseErrorHandler(agent)).toBeTruthy();
            expect(dialogs.errorPopup.show).toHaveBeenCalled();
        });
        it('should handle server errors set by header', function () {
            spyOn(dialogs.errorPopup, 'show');
            var agent = {
                getResponseHeader: function (header) {
                    return header === 'JasperServerError';
                }
            };
            expect(baseErrorHandler(agent)).toBeTruthy();
            expect(dialogs.errorPopup.show).toHaveBeenCalled();
        });
        it('should handle server errors set by header and skip notification if corresponding header set', function () {
            spyOn(dialogs.errorPopup, 'show');
            var agent = {
                getResponseHeader: function (header) {
                    return header === 'JasperServerError' || header === 'SuppressError';
                }
            };
            expect(baseErrorHandler(agent)).toBeTruthy();
            expect(dialogs.errorPopup.show).not.toHaveBeenCalled();
        });
        it('should not invoke response timer of requester if corresponding flag not set', function () {
            var requester = new AjaxRequester('', [], '');
            spyOn(requester, 'startResponseTimer');
            requester.showLoading = false;
            ajaxRequestStarted(requester);
            expect(requester.startResponseTimer).not.toHaveBeenCalled();
        });
        it('should invoke response timer of requester if corresponding flag set', function () {
            var requester = new AjaxRequester('', [], '');
            spyOn(requester, 'startResponseTimer');
            requester.showLoading = true;
            ajaxRequestStarted(requester);
            expect(requester.startResponseTimer).toHaveBeenCalled();
        });
        it('should not close loading popup until all requests ended', function () {
            var request1 = new AjaxRequester('', [], '');
            var request2 = new AjaxRequester('', [], '');
            spyOn(dialogs.popup, 'hide');
            request1.showLoading = true;
            request2.showLoading = true;
            ajax.ajaxRequestCount = 0;
            ajaxRequestStarted(request1);
            ajaxRequestStarted(request2);
            ajaxRequestEnded(request1);
            expect(dialogs.popup.hide).not.toHaveBeenCalled();
            ajaxRequestEnded(request2);
            expect(dialogs.popup.hide).toHaveBeenCalled();
        });

        it("Should escape output in case of targettedResponseHandler", function(){
            var mockValue = {
                xmlhttp: {
                    responseText: {
                        trim: function(){
                            return '<div> {"stringValue": "qwertyuiopasdfghjklzxcvbnm,.!@#$%^&*()\" &quot;"} </div>'
                        }
                    }
                },
                params: [
                    "destination"
                ]
            };

            targettedResponseHandler(mockValue);

            expect(jQuery("#destination").text()).toEqual(" {&quot;stringValue&quot;: &quot;qwertyuiopasdfghjklzxcvbnm,.!@#$%^&*&#40;&#41;&quot; &amp;quot;&quot;} ");
        });
    });
});