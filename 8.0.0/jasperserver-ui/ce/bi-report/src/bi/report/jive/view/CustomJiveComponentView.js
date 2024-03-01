/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import Backbone from 'backbone';
import $ from 'jquery';
import logger from "js-sdk/src/common/logging/logger";
import {loadCss} from 'js-sdk/src/common/util/loader/cssLoader';
import {loadDynamicModule} from '../../loader/dynamicModuleLoader'

const localLogger = logger.register("CustomJiveComponentView");

export default Backbone.View.extend({
    loadCss: function () {
        const css = this.model.get('css');
        css && loadCss(css.href);
    },
    render: function () {
        const renderDeferred = new $.Deferred();
        const data = this.model.get('instanceData');
        const script = this.model.get('script');

        this.loadCss();

        loadDynamicModule(script.name, script.href).then((renderer) => {
            // Cleanup the DIV...
            // This is due to a bug in the interactive viewer which
            // inovkes the component twice.
            $('#' + data.id + ' svg').remove();
            renderer(data);
            renderDeferred.resolve();
        }).catch((err) => {
            localLogger.debug(err);
            renderDeferred.reject(err);
        });

        return renderDeferred;
    }
});