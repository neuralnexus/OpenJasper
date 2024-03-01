/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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

// This file should be very first import in each final js webpack bundle
// This module does configuration for all js bundles

import bundleLoader from '../i18n/bundleLoader';
import settingsLoader from '../settings/settingsLoader';
import allExtensionsLoaded from './allExtensionsSetup';

export default ({bundles, settings, importCommonModule} = {}) => {
    if (importCommonModule) {
        // wait for bundles for the common modules and for the common module itself
        Promise.all([
            bundleLoader(["CommonBundle", "jasperserver_config", "jasperserver_messages", "jsexceptions_messages"]),
            settingsLoader(["dateTimeSettings"])
        ]).then(importCommonModule)
    }

    return Promise.all([
        allExtensionsLoaded,
        bundleLoader(bundles),
        settingsLoader(settings)
    ]);
}
