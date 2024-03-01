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

import _ from 'underscore';
import i18n from '../../i18n/AttributesBundle.properties';
import AttributesCollection from '../../attributes/collection/AttributesCollection';
import DesignerRowView from '../../attributes/view/DesignerRowView';
import DesignerEmptyView from '../../attributes/view/DesignerEmptyView';
import viewWithPermissionTrait from '../../attributes/view/viewWithPermissionTrait';
import RowView from '../../attributes/view/RowView';
import attributesTypesEnum from '../../attributes/enum/attributesTypesEnum';
import i18n2 from '../../i18n/CommonBundle.properties';
import AttributeModel from '../../attributes/model/AttributeModel';
import modelWithPermissionTrait from '../../attributes/model/modelWithPermissionTrait';
import tooltipTemplate from '../../attributes/templates/tooltipTemplate.htm';

export default function (options) {
    var collection = new AttributesCollection([], {
            context: options.context,
            model: options.type === attributesTypesEnum.USER ? AttributeModel : AttributeModel.extend(modelWithPermissionTrait)
        }), commonOptions = {
            type: options.type,
            collection: collection,
            childViewContainer: '.tbody',
            tooltip: {
                template: tooltipTemplate,
                i18n: i18n
            }
        };
    return _.extend(commonOptions, options.type !== attributesTypesEnum.SERVER ? {
        $container: options.container,
        designer: {
            childView: options.type === attributesTypesEnum.USER ? DesignerRowView : DesignerRowView.extend(viewWithPermissionTrait),
            emptyView: DesignerEmptyView,
            filters: [
                {
                    title: i18n['attributes.table.filters.all'],
                    value: 'true',
                    field: 'defaultFilter',
                    selected: true
                },
                {
                    title: i18n['attributes.table.filters.true'],
                    value: 'true',
                    field: 'inherited'
                },
                {
                    title: i18n['attributes.table.filters.false'],
                    value: 'false',
                    field: 'inherited'
                }
            ]
        },
        viewer: {childView: RowView}
    } : {
        el: options.el,
        childView: DesignerRowView.extend(viewWithPermissionTrait),
        emptyView: DesignerEmptyView,
        buttons: [
            {
                label: i18n2['button.save'],
                action: 'save',
                primary: true
            },
            {
                label: i18n2['button.cancel'],
                action: 'cancel',
                primary: false
            }
        ],
        buttonsContainer: '.buttonsContainer'
    });
}