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

import sinon from 'sinon';
import $ from 'jquery';
import TreeLevel from 'src/common/component/tree/TreeLevel';

describe('Tree component: TreeLevel', function () {
    var treeLevel;

    var obtainData = function() {
            return new $.Deferred().resolve({
                total: 1,
                data: [{value: "test"}]
            });
        },
        obtainDataSpy = sinon.spy(obtainData),
        owner = {
            getDataLayer: function() {
                return {
                    obtainData: obtainDataSpy
                }
            }
        };

    beforeEach(function () {
        treeLevel = new TreeLevel({
            plugins: [],
            owner: owner
        });
    });

    afterEach(function () {
        treeLevel && treeLevel.remove();
    });

    it("Should call list.renderData on node open", function () {
        treeLevel.render();
        var renderDataSpy = sinon.spy(treeLevel.list, "renderData");

        treeLevel._onOpen();

        sinon.assert.calledOnce(renderDataSpy);
        renderDataSpy.restore();
    });

    describe('cache', function () {
        beforeEach(function () {
            treeLevel = new TreeLevel({
                plugins: [],
                owner: owner,
                cache: {
                    searchKey: "searchString",
                    pageSize: 100
                }
            });
        });

        it("should use _getDataProvider to create dataprovider for list", function () {
            var getDataProviderSpy = sinon.spy(treeLevel, "_getDataProvider");

            treeLevel.render();

            sinon.assert.calledWith(getDataProviderSpy, {
                searchKey: "searchString",
                pageSize: 100
            });
        });

        it("should use obtainData as a request method for cacheable dataProvider", function () {
            treeLevel.render();

            expect(treeLevel.dataProvider).toBeDefined();
            expect(treeLevel.dataProvider.request).toBeDefined();

            treeLevel.dataProvider.request({
                limit: 5,
                offset: 10
            });

            sinon.assert.calledWith(obtainDataSpy, {
                limit: 5,
                offset: 10
            }, treeLevel);
        });

        it("should clear cache on remove", function () {
            var childNode = new TreeLevel({
                plugins: [],
                owner: owner,
                cache: {
                    searchKey: "searchString",
                    pageSize: 100
                }
            });

            treeLevel.items["/path"] = childNode;
            treeLevel.render();
            childNode.render();

            var clearSpy = sinon.spy(treeLevel.dataProvider, "clear"),
                clearSpy2 = sinon.spy(childNode.dataProvider, "clear");

            treeLevel.remove();

            sinon.assert.calledOnce(clearSpy);
            sinon.assert.calledOnce(clearSpy2);
        });

        it("should clear cache on callingClearCache", function () {
            var childNode = new TreeLevel({
                plugins: [],
                owner: owner,
                cache: {
                    searchKey: "searchString",
                    pageSize: 100
                }
            });

            treeLevel.items["/path"] = childNode;
            treeLevel.render();
            childNode.render();


            var clearSpy = sinon.spy(treeLevel.dataProvider, "clear"),
                clearSpy2 = sinon.spy(childNode.dataProvider, "clear");


            treeLevel.clearCache();

            sinon.assert.calledOnce(clearSpy);
            sinon.assert.calledOnce(clearSpy2);
        });
    });
});