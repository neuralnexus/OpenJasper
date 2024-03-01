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

import dynamicTree from 'src/dynamicTree/dynamicTree.utils';
import sinon from 'sinon';

describe('dynamicTree Utils', function () {
    describe('modifyRootObject', function () {
        var treeId;
        beforeEach(function () {
            treeId = 'testTree';
            sinon.stub(dynamicTree, "TreeSupport");
        });

        afterEach(function() {
            dynamicTree.TreeSupport.restore();
        });

        it('should not modify Root Object if no organization provided', function () {
            var tree = new dynamicTree.createRepositoryTree(treeId, {
                organizationId: "",
                publicFolderUri: "/public"
            });
            expect(tree).toBeDefined();
            expect(tree.orgMode).toBeFalsy();
            expect(tree.modifyRootObject({}, false)).toEqual({});
        });

        it('should modify Root Object for given organization', function () {
            var rootObj = {
                    children: [
                        {
                            id: "adhoc",
                            label: "Ad Hoc Components",
                            uri: "/adhoc"
                        },
                        {
                            id: "analysis",
                            label: "Analysis Components",
                            uri: "/analysis"
                        },
                        {
                            id: "ContentFiles",
                            label: "Content Files",
                            uri: "/ContentFiles"
                        },
                        {
                            id: "public",
                            label: "Public",
                            uri: "/public"
                        }
                    ],
                    id: "/",
                    label: "Organization",
                },
                newRootObj = {
                    children: [
                        {
                            children: [
                                {
                                    id: "adhoc",
                                    label: "Ad Hoc Components",
                                    uri: "/adhoc"
                                },
                                {
                                    id: "analysis",
                                    label: "Analysis Components",
                                    uri: "/analysis"
                                },
                                {
                                    id: "ContentFiles",
                                    label: "Content Files",
                                    uri: "/ContentFiles"
                                }
                            ],
                            id: "/",
                            label: "Organization"
                        },
                        {
                            id: "public",
                            label: "Public",
                            uri: "/public"
                        }
                    ],
                    extra: {},
                    label: "",
                    type: "superroot"
                },
                tree = new dynamicTree.createRepositoryTree(treeId, {
                    organizationId: "organization_1",
                    publicFolderUri: "/public"
                });
            expect(tree.modifyRootObject(rootObj, false)).toEqual(newRootObj);
        });

        it('should modify Root object when only content of public is provided', function () {
            var rootObj = {
                    children: [
                        {
                            id: "Additional_Resources_RunReport_JIVE",
                            label: "Additional_Resources_RunReport_JIVE",
                            uri: "/public/Additional_Resources_RunReport_JIVE"
                        },
                        {
                            id: "adhoc",
                            label: "Ad Hoc Components",
                            uri: "/public/adhoc"
                        },
                        {
                            id: "audit",
                            label: "Audit",
                            uri: "/public/audit"
                        }
                    ],
                    id: "public",
                    label: "Public",
                },
                newRootObj = {
                    type: "superroot",
                    label: "",
                    extra: {},
                    children: [
                        {
                            children: [
                                {
                                    id: "Additional_Resources_RunReport_JIVE",
                                    label: "Additional_Resources_RunReport_JIVE",
                                    uri: "/public/Additional_Resources_RunReport_JIVE"
                                },
                                {
                                    id: "adhoc",
                                    label: "Ad Hoc Components",
                                    uri: "/public/adhoc"
                                },
                                {
                                    id: "audit",
                                    label: "Audit",
                                    uri: "/public/audit"
                                }
                            ],
                            id: "public",
                            label: "Public",
                        }
                    ]
                },
                tree = new dynamicTree.createRepositoryTree(treeId, {
                    organizationId: "organization_1",
                    publicFolderUri: "/public"
                });
            expect(tree.modifyRootObject(rootObj, false)).toEqual(newRootObj);
        });

        it('should modify Root Object for children call back', function () {
            var rootObj = [
                    {
                        id: "Additional_Resources_RunReport_JIVE",
                        label: "Additional_Resources_RunReport_JIVE",
                        uri: "/public/Additional_Resources_RunReport_JIVE"
                    },
                    {
                        id: "adhoc",
                        label: "Ad Hoc Components",
                        uri: "/public/adhoc"
                    },
                    {
                        id: "audit",
                        label: "Audit",
                        uri: "/public/audit"
                    },
                    {
                        id: "public",
                        label: "public",
                        uri: "/public"
                    }
                ],
                newRootObj = [
                    {
                        id: "Additional_Resources_RunReport_JIVE",
                        label: "Additional_Resources_RunReport_JIVE",
                        uri: "/public/Additional_Resources_RunReport_JIVE"
                    },
                    {
                        id: "adhoc",
                        label: "Ad Hoc Components",
                        uri: "/public/adhoc"
                    },
                    {
                        id: "audit",
                        label: "Audit",
                        uri: "/public/audit"
                    }
                ],
                tree = new dynamicTree.createRepositoryTree(treeId, {
                    organizationId: "organization_1",
                    publicFolderUri: "/public"
                });
            expect(tree.modifyRootObject(rootObj, true, { param: { uri: "/public" }})).toEqual(newRootObj);
        });

        it('should not modify Root Object for children call back', function () {
            var rootObj = [
                    {
                        id: "Additional_Resources_RunReport_JIVE",
                        label: "Additional_Resources_RunReport_JIVE",
                        uri: "/public/Additional_Resources_RunReport_JIVE"
                    },
                    {
                        id: "adhoc",
                        label: "Ad Hoc Components",
                        uri: "/public/adhoc"
                    },
                    {
                        id: "audit",
                        label: "Audit",
                        uri: "/public/audit"
                    }
                ],
                tree = new dynamicTree.createRepositoryTree(treeId, {
                    organizationId: "organization_1",
                    publicFolderUri: "/public"
                });
            expect(tree.modifyRootObject(rootObj, true, { param: { uri: "/public" }})).toEqual(rootObj);
        });
    });
});