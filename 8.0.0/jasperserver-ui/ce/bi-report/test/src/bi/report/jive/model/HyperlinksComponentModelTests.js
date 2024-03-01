/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import Backbone from 'backbone';
import HyperlinksComponentModel from 'src/bi/report/jive/model/HyperlinksComponentModel';
import hyperlinkTypes from 'src/bi/report/jive/enum/hyperlinkTypes';
import hyperlinkTargets from 'src/bi/report/jive/enum/hyperlinkTargets';

describe('HyperlinksComponentModel tests', function () {
    var model;
    beforeEach(function () {
        model = new HyperlinksComponentModel();
        model.parent = new Backbone.Model({ reportURI: '/public/test' });
    });
    it('should be able to parse data', function () {
        var result = model.parse({
            hyperlinks: [{
                anchor: 'summary',
                href: 'null/flow.html?_eventId_navigate=&pageIndex=2&_flowExecutionKey=null#summary',
                id: '1281016021',
                params: { test: 'test' },
                selector: '._jrHyperLink.LocalAnchor',
                target: 'Blank',
                targetValue: '_blank',
                tooltip: 'Skip to the summary section',
                type: 'LocalAnchor',
                typeValue: 'LocalAnchor'
            }]
        });
        expect(result.hyperlinks[0]).toEqual({
            anchor: 'summary',
            href: 'null/flow.html?_eventId_navigate=&pageIndex=2&_flowExecutionKey=null#summary',
            id: '1281016021',
            parameters: { test: 'test' },
            target: hyperlinkTargets.BLANK,
            targetValue: '_blank',
            tooltip: 'Skip to the summary section',
            type: hyperlinkTypes.LOCAL_ANCHOR,
            pages: undefined,
            resource: '/public/test'
        });
        result = model.parse({
            hyperlinks: [{
                href: 'null/flow.html?_eventId_navigate=&pageIndex=1&_flowExecutionKey=null',
                id: '891390511',
                page: '2',
                selector: '._jrHyperLink.LocalPage',
                targetValue: '_self',
                type: 'LocalPage',
                typeValue: 'LocalPage'
            }]
        });
        expect(result.hyperlinks[0]).toEqual({
            anchor: undefined,
            href: 'null/flow.html?_eventId_navigate=&pageIndex=1&_flowExecutionKey=null',
            id: '891390511',
            parameters: undefined,
            tooltip: undefined,
            target: hyperlinkTargets.SELF,
            targetValue: '_self',
            type: hyperlinkTypes.LOCAL_PAGE,
            pages: '2',
            resource: '/public/test'
        });
        result = model.parse({
            hyperlinks: [{
                href: 'http://jaspersoft.com',
                id: '957230446',
                params: { test: 'test' },
                reference: 'http://jaspersoft.com',
                selector: '._jrHyperLink.Reference',
                targetValue: '_self',
                type: 'Reference',
                typeValue: 'Reference'
            }]
        });
        expect(result.hyperlinks[0]).toEqual({
            anchor: undefined,
            href: 'http://jaspersoft.com',
            id: '957230446',
            parameters: { test: 'test' },
            tooltip: undefined,
            target: hyperlinkTargets.SELF,
            targetValue: '_self',
            type: hyperlinkTypes.REFERENCE,
            pages: undefined
        });
        result = model.parse({
            hyperlinks: [{
                anchor: 'summary',
                href: '/jasperserver-pro/fileview/fileview/public/Hyperlink_samples/targets/test.pdf#summary',
                id: '728087578',
                params: { test: 'test' },
                reference: '/jasperserver-pro/fileview/fileview/public/Hyperlink_samples/targets/test.pdf',
                selector: '._jrHyperLink.RemoteAnchor',
                targetValue: '_self',
                type: 'RemoteAnchor',
                typeValue: 'RemoteAnchor'
            }]
        });
        expect(result.hyperlinks[0]).toEqual({
            anchor: 'summary',
            href: '/jasperserver-pro/fileview/fileview/public/Hyperlink_samples/targets/test.pdf#summary',
            id: '728087578',
            parameters: { test: 'test' },
            tooltip: undefined,
            target: hyperlinkTargets.SELF,
            targetValue: '_self',
            type: hyperlinkTypes.REMOTE_ANCHOR,
            pages: undefined
        });
        result = model.parse({
            hyperlinks: [{
                anchor: 'title',
                href: '/jasperserver-pro/fileview/fileview/public/Hyperlink_samples/targets/test.pdf#JR_PAGE_ANCHOR_0_2',
                id: '344097327',
                page: '2',
                params: { test: 'test' },
                reference: '/jasperserver-pro/fileview/fileview/public/Hyperlink_samples/targets/test.pdf',
                selector: '._jrHyperLink.RemotePage',
                target: 'Parent',
                targetValue: '_parent',
                type: 'RemotePage',
                typeValue: 'RemotePage'
            }]
        });
        expect(result.hyperlinks[0]).toEqual({
            anchor: 'title',
            href: '/jasperserver-pro/fileview/fileview/public/Hyperlink_samples/targets/test.pdf#JR_PAGE_ANCHOR_0_2',
            id: '344097327',
            pages: '2',
            parameters: { test: 'test' },
            target: hyperlinkTargets.PARENT,
            targetValue: '_parent',
            type: hyperlinkTypes.REMOTE_PAGE,
            tooltip: undefined
        });
        result = model.parse({
            hyperlinks: [{
                href: 'null/flow.html?_flowId=viewReportFlow&reportUnit=%2Fpublic%2FHyperlink_samples%2FCities_Ad_Hoc_View_Report&ShipCountry_1=USA',
                id: '456040544',
                anchor: 'anchorOverride',
                page: '13',
                params: {
                    _report: '/public/Hyperlink_samples/Cities_Ad_Hoc_View_Report',
                    _anchor: 'test',
                    _page: '3',
                    ShipCountry_1: 'USA'
                },
                selector: '._jrHyperLink.ReportExecution',
                targetValue: '_self',
                tooltip: 'USA',
                type: 'ReportExecution',
                typeValue: 'Custom'
            }]
        });
        expect(result.hyperlinks[0]).toEqual({
            anchor: 'test',
            href: 'null/flow.html?_flowId=viewReportFlow&reportUnit=%2Fpublic%2FHyperlink_samples%2FCities_Ad_Hoc_View_Report&ShipCountry_1=USA',
            id: '456040544',
            pages: '3',
            parameters: {
                _report: '/public/Hyperlink_samples/Cities_Ad_Hoc_View_Report',
                _anchor: 'test',
                _page: '3',
                ShipCountry_1: 'USA'
            },
            target: hyperlinkTargets.SELF,
            targetValue: '_self',
            type: hyperlinkTypes.REPORT_EXECUTION,
            tooltip: 'USA',
            resource: '/public/Hyperlink_samples/Cities_Ad_Hoc_View_Report'
        });
    });
});