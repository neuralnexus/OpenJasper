/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import JiveOverlayView from 'src/bi/report/jive/view/overlay/JiveOverlayView';
import $ from 'jquery';
import setTemplates from 'js-sdk/test/tools/setTemplates';

describe('JiveOverlayView tests', function () {
    var jiveOverlayView;
    beforeEach(function () {
        setTemplates('<div class=\'overlayContainer\'></div>');
        jiveOverlayView = new JiveOverlayView({ parentElement: '.overlayContainer' });
    });
    afterEach(function () {
        jiveOverlayView && jiveOverlayView.remove();
        $('.overlayContainer').remove();
    });
    it('should render overlay', function () {
        expect(jiveOverlayView.rendered).toBeFalsy();
        jiveOverlayView.render();
        expect(jiveOverlayView.rendered).toBeTruthy();
        expect(jiveOverlayView.$el.is(':visible')).toBeFalsy();
    });
    it('should apply css', function () {
        jiveOverlayView.render();
        jiveOverlayView.css({
            width: 200,
            height: 100
        });
        expect(jiveOverlayView.$el.width()).toEqual(200);
        expect(jiveOverlayView.$el.height()).toEqual(100);
    });
    it('should show overlay', function () {
        jiveOverlayView.render();
        expect(jiveOverlayView.$el.is(':visible')).toBeFalsy();
        jiveOverlayView.show();
        expect(jiveOverlayView.$el.is(':visible')).toBeTruthy();
    });
});