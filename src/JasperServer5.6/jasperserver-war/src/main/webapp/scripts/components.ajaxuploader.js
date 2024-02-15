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
 * @author: ztomchenco
 * @version: $Id: components.ajaxuploader.js 47331 2014-07-18 09:13:06Z kklein $
 */

jaspersoft || (jaspersoft = {components:{}});
jaspersoft.components || (jaspersoft.components = {});

jaspersoft.components.AjaxUploader = (function ($, Template) {
    //TODO: try to replace oldscoll loops (it's fast but too messy) by underscore
    function parseResponse(document){
        var result;
        if (document.firstChild.innerText && document.firstChild.innerText !== "") {
            if (document.body){
                var result = new ActiveXObject("Microsoft.XMLDOM");
                result.async = false;
                //remove injected by IE symbols
                result.loadXML(document.firstChild.innerText.replace("\r\n-", "\r\n"));
            }  else {
                result = document;
            }
        }  else {
            result = document;
        }

        var firstDocChild = null;
        result.firstChild && (firstDocChild = result.firstChild.nodeName.toLowerCase());
        if (!firstDocChild || firstDocChild === "html" || firstDocChild === "#comment"){
            return {errorCode: "unexpected.error"}
        }

        return parseChildren((result.firstChild.nodeName.toLowerCase() === "xml")
                ? result.firstChild.nextSibling : result.firstChild);
    }

    function parseChildren(node){
        var children;
        if (!node.children){
            children = [];
            for (var i= 0, l= node.childNodes.length; i<l; i++){
                if (node.childNodes[i].nodeValue === null){
                    children.push(node.childNodes[i]);
                }
            }
        } else {
            children = node.children;
        }

        if (children.length === 0 && node.childNodes.length === 1){
            return node.childNodes[0].nodeValue;
        } else {
            var ob;
            if (children.length > 1 && (children[0].nodeName === children[1].nodeName)){
                ob = [];
                for (var i= 0, l= children.length; i<l; i++){
                    ob[i] =  parseChildren(children[i]);
                }
            } else{
                ob = {};
                for (var i= 0, l= children.length; i<l; i++){
                    ob[children[i].nodeName] =  parseChildren(children[i]);
                }
            }
        }
        return ob;
    }

    var AjaxUploader = function (uploadForm, callback, timeout) {
        this.name = _.uniqueId("uploadTarget");
        timeout && (this.timeout = timeout);
        var template = Template.createTemplate("ajaxUploadTemplate");
        if(!template){
            template = Template.createTemplateFromText("<iframe class='hidden' name='{{name}}'></iframe>");
        }

        this.iframe = $(template({name:this.name}));
        $(uploadForm).append(this.iframe).attr("target",this.name);

        (function(uploader, callback) {
            uploader.iframe.load(function() {

                if (uploader.isTimeout()){
                    callback({errorCode:"error.timeout"});
                }else if (jQuery(this.contentWindow.document).text()) {
                    try{
                        callback(parseResponse(this.contentWindow.document));
                    }catch(e){
                        callback({errorCode:"error.invalid.response"});
                    }
                }

                //got response from server
                uploader.stopTimeoutLookup();
            })
        })(this, callback);

        this.parceXmlDocToObject = parseResponse;
    };

    AjaxUploader.prototype.startTimeoutLookup = function(callback){
         if (this.timeout){
             var stepMsec = 1000, stepsCountMsec = 0;
             var intervalHandler = function(){
                 stepsCountMsec += stepMsec;
                 if (stepsCountMsec >= this.timeout){
                     this.stopTimeoutLookup();
                     callback({errorCode:"error.timeout"});
                 }
             };
             this._intervalId = setInterval(_.bind(intervalHandler, this), stepMsec);
         }

    };

    AjaxUploader.prototype.stopTimeoutLookup = function() {
        if (this.timeout) {
            clearInterval(this._intervalId);
            this._intervalId = -1;
        }
    };

    AjaxUploader.prototype.isTimeout = function() {
        if(!this.timeout) false;
        if (this._intervalId === -1) return true;
        return false;
    };

    return AjaxUploader;

})(jQuery, jaspersoft.components.templateEngine);
