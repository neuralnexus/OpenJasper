import sinon from 'sinon';
import {request} from 'src/transport/requestExtension';
import $ from 'jquery'

describe('request Tests', () => {
    beforeEach(() => {
        sinon.stub($, 'ajax').callsFake(() => ($.Deferred()));
    });

    afterEach(() => {
        if ($.ajax.restore){
            $.ajax.restore();
        }
    });

    it('should call xhr request', () => {
        request({
            url: '/uri'
        });

        expect($.ajax).toHaveBeenCalledWith({
            headers: {
                "X-Suppress-Basic": "true",
                "Cache-Control": "no-cache, no-store",
                "Pragma": "no-cache",
                "Accept-Language": "en",
                "X-Remote-Domain": window.location.href
            },
            url: "/uri"
        });
    });
});