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

module.exports = {
    icons: {
        src: 'images/*.svg',                // file path: to get .svg files
        dest: 'themes/default/jasper-ui/fonts',                      // file path: saved fonts
        destScss: 'scss/',                  // file path: saved .scss
        options: {
            cache: process.env.WEBFONT_CACHE_DIR,
            hashes: false,
            font: 'icons',
            htmlDemo: false,
            engine: 'node',                 // use node to render fonts, not 'fontforge'
            relativeFontPath: '#{$font-path}',     // css property: background url()
            types: 'eot,woff,ttf,svg',
            stylesheet: 'scss',              // specify to render .css stylesheet
            templateOptions: {
                baseClass: 'jr-mIcon',
                classPrefix: 'jr-mIcon.jr-'
            },
            codepoints: {                   // custom Unicode mapping to svg icons
                'arrowClosed':0xf101,
                'arrowDoubleLeft':0xf102,
                'arrowDoubleRight':0xf103,
                'arrowDown':0xf104,
                'arrowLeft':0xf105,
                'arrowOpen':0xf106,
                'arrowRight':0xf107,
                'arrowToBottom':0xf108,
                'arrowToLeft':0xf109,
                'arrowToRight':0xf10a,
                'arrowToTop':0xf10b,
                'arrowUp':0xf10c,
                'bookmark':0xf10d,
                'calendar':0xf10e,
                'cancel':0xf10f,
                'cancelRound':0xf110,
                'chartColumn':0xf111,
                'checkmark':0xf112,
                'checkmarkRound':0xf113,
                'clock':0xf114,
                'database':0xf115,
                'delete':0xf116,
                'download':0xf117,
                'edit':0xf118,
                'export':0xf119,
                'file':0xf11a,
                'fileOpen':0xf11b,
                'filter':0xf11c,
                'gear':0xf11d,
                'gears':0xf11e,
                'grid':0xf11f,
                'hamburger':0xf120,
                'hashtag':0xf121,
                'home':0xf122,
                'info':0xf123,
                'infoRound':0xf124,
                'inputControl':0xf125,
                'invert':0xf126,
                'join':0xf127,
                'joinFull':0xf128,
                'joinInner':0xf129,
                'joinLeft':0xf12a,
                'joinRight':0xf12b,
                'link':0xf12c,
                'linkExternal':0xf12d,
                'list':0xf12e,
                'loading':0xf12f,
                'maximize':0xf130,
                'meatball':0xf131,
                'message':0xf132,
                'minimize':0xf133,
                'minus':0xf134,
                'minusSquare':0xf135,
                'operatorAdd':0xf136,
                'operatorAnd':0xf137,
                'operatorColon':0xf138,
                'operatorDivide':0xf139,
                'operatorEqual':0xf13a,
                'operatorGreaterEqual':0xf13b,
                'operatorGreaterThan':0xf13c,
                'operatorIn':0xf13d,
                'operatorLessEqual':0xf13e,
                'operatorLessThan':0xf13f,
                'operatorMultiply':0xf140,
                'operatorNot':0xf141,
                'operatorNotEqual':0xf142,
                'operatorOr':0xf143,
                'operatorParenLeft':0xf144,
                'operatorParenRight':0xf145,
                'operatorPercent':0xf146,
                'operatorSubtract':0xf147,
                'plus':0xf148,
                'plusSquare':0xf149,
                'properties':0xf14a,
                'query':0xf14b,
                'redo':0xf14c,
                'refresh':0xf14d,
                'save':0xf14e,
                'search':0xf14f,
                'sort':0xf150,
                'squareBackground':0xf151,
                'stop':0xf152,
                'switch':0xf153,
                'textABottom':0xf154,
                'textACenter':0xf155,
                'textALeft':0xf156,
                'textAMiddle':0xf157,
                'textARight':0xf158,
                'textATop':0xf159,
                'textBold':0xf15a,
                'textItalic':0xf15b,
                'textUnderline':0xf15c,
                'treeHorizontal':0xf15d,
                'treeVertical':0xf15e,
                'undo':0xf15f,
                'undoAll':0xf160,
                'unlink':0xf161,
                'view':0xf162,
                'warning':0xf163,
                'warningRound':0xf164,
                'kebab':0xf165
            },
            normalize: true                 // render </svg> using its original size
        }
    }
};
