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
 * @version: $Id: jobs.js 47331 2014-07-18 09:13:06Z kklein $
 */

define('scheduler/collection/jobs', function(require){

    var _ = require('underscore'),
        Backbone = require('backbone'),
        config = require('jrs.configs'),
        jobModel = require('scheduler/model/job');

    return Backbone.Collection.extend({

        // url for fetching collection
        url: config.contextPath + '/rest_v2/jobs',

        // collection model
        model: jobModel,

        // parse response to adjust fields usage
        parse: function(response){
            return response ? response.jobsummary : [];
        },

        fetch: function(url, callback){
            var t = this,
                done = 0,
                result = [],
                uris = [url],
                options = config.contextPath + '/rest_v2/reports' + url + '/options';

            if (config.isProVersion)
                t.request({ url: options }, function(err, data){

                    if (!err && (data = data.reportOptionsSummary))
                        for(var i=0, l=data.length; i<l; i++)
                            if (data[i].uri) uris.push(data[i].uri);

                    _.each(uris, function(uri){
                        t.request({ url: t.url + '?' + 'reportUnitURI=' + uri }, function(err, data){
                            done++;
                            if (err) return t.trigger('error', err);
                            result = result.concat(t.parse(data));
                            if (done === uris.length) t.reset(result);
                        });
                    });
                });
            else t.request({ url: t.url + '?' + 'reportUnitURI=' + url }, function(err, data){
                if (err) return t.trigger('error', err);
                t.reset(t.parse(data));
            });
        },

        request: function(options, callback){
            options || (options = {});
            options.success = function(data, xhr){
                callback(undefined, data);
            };
            options.error = function(err){
                callback(err);
            };
            return Backbone.sync.call(this, 'read', new Backbone.Model(), options);
        },

        permission: function(url, callback){
            // call backbone sync method manually
            return Backbone.sync.call(this, 'read', new Backbone.Model(), {
                url: config.contextPath + '/rest_v2/resources/' + url.replace(/\/[^\/]+$/, ''),
                headers:{ 'Accept': 'application/repository.folder+json' },
                type: 'GET',
                success: function(data, xhr){
                    if ('function' === typeof callback)
                        callback(undefined, data.permissionMask);
                },
                error: function(err){
                    if ('function' === typeof callback)
                        callback(err);
                }
            });
        }

    });

});