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

export default {
    showDetails: true,
    showTotals: true,
    showSummary: true,
    columns: [
        {
            type: 'spacer',
            width: 200
        },
        {
            type: 'string',
            label: 'City',
            width: 300,
            format: null,
            horizontalAlign: 'left'
        },
        {
            type: 'number',
            label: 'Shipping charge',
            width: 150,
            format: null,
            horizontalAlign: 'left'
        }
    ],
    rows: [
        {
            type: 'group',
            data: ['Argentina']
        },
        {
            type: 'detail',
            data: [
                null,
                'Buenos Aires',
                34
            ]
        },
        {
            type: 'detail',
            data: [
                null,
                'Buenos Aires',
                40
            ]
        },
        {
            type: 'group total',
            data: [
                'Argentina Totals',
                2,
                74
            ]
        },
        {
            type: 'group',
            data: ['Austria']
        },
        {
            type: 'detail',
            data: [
                null,
                'Salzburg',
                31
            ]
        },
        {
            type: 'detail',
            data: [
                null,
                'Salzburg',
                12
            ]
        },
        {
            type: 'group total',
            data: [
                'Austria Totals',
                2,
                43
            ]
        }
    ],
    summary: [{
        type: 'total',
        data: [
            'Totals',
            4,
            117
        ]
    }]
};