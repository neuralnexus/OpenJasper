/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

requirejs.config({

    baseUrl: "../src",
    config: {
        theme: {
            paths: {
                "js-sdk": "src/runtime_dependencies/js-sdk",
                "bi-report": "."
            }
        }
    }

});