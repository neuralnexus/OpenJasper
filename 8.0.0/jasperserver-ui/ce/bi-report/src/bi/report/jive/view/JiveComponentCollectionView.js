/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */


/**
 * @author: Igor Nesterenko
 * @version: $Id$
 */

import 'jquery-ui-touch-punch';
import Backbone from 'backbone';
import _ from 'underscore';
import $ from 'jquery';
import '../jr/theme/jive.csslink.css';
import logger from "js-sdk/src/common/logging/logger";
import {jiveComponentViewMapping} from './jiveComponentViewMapping';

let localLogger = logger.register("JiveComponentCollectionView");

function JiveComponentCollectionView(options) {
    options || (options = {});
    this.stateModel = options.stateModel;
    this.collection = options.collection;
    this.dialogStates = options.dialogStates;
    this.listenTo(this.collection, 'reset', this.initSubviews, this);
}
JiveComponentCollectionView.prototype = {
    initSubviews: function () {
        const
            self = this,
            viewModules = [],
            viewModels = [];

        _.invoke(this.subviews || [], 'remove');
        this.subviews = [];

        if (this.subviewsReadyDfd) {
            const old = this.subviewsReadyDfd;
            this.subviewsReadyDfd = new $.Deferred().done(function () {
                old.resolve();
            }).fail(function () {
                old.reject();
            });
        } else {
            this.subviewsReadyDfd = new $.Deferred();
        }

        this.collection.forEach(function (component) {
            const type = component.get('type');
            if (jiveComponentViewMapping[type]) {
                viewModules.push(jiveComponentViewMapping[type]);
                viewModels.push(component.get('id'));
            } else {
                localLogger.debug(`Jive component is not registered for the type [${type}]`)
            }
        });

        Promise.all(viewModules.map(m => m())).then((allModules) => {
            _.each(allModules, function ({default: ViewModule}, index) {
                self.subviews.push(new ViewModule({
                    model: self.collection.get(viewModels[index]),
                    report: self.collection.report,
                    dialogStates: self.dialogStates,
                    stateModel: self.stateModel
                }));
            });
            localLogger.debug('Create JIVE views ', self.subviews);
            self.subviewsReadyDfd.resolve();
        }).catch(this.subviewsReadyDfd.reject);
    },

    render: function($el, options) {
        var self = this,
            dfd = new $.Deferred();

        self.subviewsReadyDfd && self.subviewsReadyDfd.then(function () {
            var subViewsRenderDeferreds = _.invoke(self.subviews, "render", $el, options);
            $.when.apply($, subViewsRenderDeferreds).then(dfd.resolve, dfd.reject);
        }, dfd.reject);
        return dfd;
    },
    sizableSubviews: function () {
        return _.filter(this.subviews, function (jiveComponent) {
            return jiveComponent.setSize;
        });
    },
    scalableSubviews: function () {
        return _.filter(this.subviews, function (jiveComponent) {
            return jiveComponent.scale;
        });
    },
    getSizableSubviews: function () {
        var self = this, dfd = new $.Deferred();
        this.subviewsReadyDfd.then(function () {
            dfd.resolve(_.filter(self.subviews, function (jiveComponent) {
                return jiveComponent.setSize;
            }));
        });
        return dfd;
    },
    getScalableSubviews: function () {
        var self = this, dfd = new $.Deferred();
        this.subviewsReadyDfd.then(function () {
            dfd.resolve(_.filter(self.subviews, function (jiveComponent) {
                return jiveComponent.scale;
            }));
        });
        return dfd;
    },
    remove: function () {
        _.invoke(this.subviews || [], 'remove');
        this.stopListening(this.collection, 'reset', this.initSubviews, this);
    }
};
_.extend(JiveComponentCollectionView.prototype, Backbone.Events);
export default JiveComponentCollectionView;
