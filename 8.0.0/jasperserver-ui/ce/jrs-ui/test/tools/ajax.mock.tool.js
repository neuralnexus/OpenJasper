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

var fakeResponse = function(){
    var _error = [false];
    var _data = [];
    var _current = 0;

    return {
        addData:function(data) {
            _data.push(data);
        },

        setError:function(error) {
            _error.push(error ? error : false);
        },

        getResponse:function() {
            var res = {
                collect:function(coll){
                    var resArr = [];
                    for(var key in this){
                        if (Object.hasOwnProperty.apply(this, key)){
                            res.push(coll(key));
                        }
                    }
                    return resArr;
                }
            };
            if (_error[_current]) res.error = _error[_current];
            res.data = (_data[_current])?(_data[_current]):{};
            if (++_current >= _data.length) this.reset();
            return res;
        },

        reset:function() {
            _error = [false];
            _data = [];
            _current = 0;
        }
    };
}();


var AjaxRequester = function() {};
var doNothing = function() {
    return function(){};
};
var baseErrorHandler = function(){};

AjaxRequester.prototype.EVAL_JSON = 'j';
AjaxRequester.prototype.CUMULATIVE_UPDATE = 'c';
AjaxRequester.prototype.ROW_COPY_UPDATE = 'r';
AjaxRequester.prototype.TARGETTED_REPLACE_UPDATE = 't';
AjaxRequester.prototype.DUMMY_POST_PARAM = 'dummyPostData';
AjaxRequester.prototype.MAX_WAIT_TIME = '2000';
AjaxRequester.prototype.errorHandler = function() {return false};

function ajaxTargettedUpdateMock(url,options) {
    if (options.callback) {
        options.callback(fakeResponse.getResponse());
    }
}

export {fakeResponse, AjaxRequester, doNothing, baseErrorHandler, ajaxTargettedUpdateMock};