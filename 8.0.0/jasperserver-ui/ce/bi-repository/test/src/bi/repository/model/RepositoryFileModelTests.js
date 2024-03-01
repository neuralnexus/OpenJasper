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
import RepositoryFileModel from 'src/bi/repository/model/RepositoryFileModel';
import repositoryResourceTypes from 'src/bi/repository/enum/repositoryResourceTypes';
import repositoryFileTypes from 'src/bi/repository/enum/repositoryFileTypes';
import Backbone from 'backbone';
describe('RepositoryFileModel', function () {
    it('should be Backbone.Model instance', function () {
        expect(typeof RepositoryFileModel).toBe('function');
        expect(RepositoryFileModel.prototype instanceof Backbone.Model).toBeTruthy();
    });
    it('should have "' + repositoryResourceTypes.FILE + '" type', function () {
        expect(RepositoryFileModel.prototype.type).toEqual(repositoryResourceTypes.FILE);
    });
    it('should not have validation for parentFolderUri attribute', function () {
        expect(RepositoryFileModel.prototype.validation.parentFolderUri).toBeUndefined();
    });
    it('should have "stringifyContent" property set to true', function () {
        expect(RepositoryFileModel.prototype.stringifyContent).toBe(true);
    });
    it('should extend "defaults" from RepositoryResourceModel', function () {
        expect(RepositoryFileModel.prototype.defaults.hasOwnProperty('type')).toBe(true);
        expect(RepositoryFileModel.prototype.defaults.hasOwnProperty('content')).toBe(true);
        expect(RepositoryFileModel.prototype.defaults.type).toBe(repositoryFileTypes.UNSPECIFIED);
    });
    it('should decode content on init', function () {
        var model = new RepositoryFileModel({ content: 'W3siYSI6Mn0seyJiIjoxfV0=' });
        expect(model.content).toEqual([
            { a: 2 },
            { b: 1 }
        ]);
    });
    it('should decode content when it changes', function () {
        var model = new RepositoryFileModel();
        model.set({ content: 'W3siYSI6Mn0seyJiIjoxfV0=' });
        expect(model.content).toEqual([
            { a: 2 },
            { b: 1 }
        ]);
    });
    it('should encode content in setContent method', function () {
        var model = new RepositoryFileModel();
        model.setContent([
            { a: 2 },
            { b: 1 }
        ]);
        expect(model.get('content')).toBe('W3siYSI6Mn0seyJiIjoxfV0=');
    });
    it('should have fetchContent method', function () {
        expect(typeof RepositoryFileModel.prototype.fetchContent).toBe('function');
        var model = new RepositoryFileModel({ uri: '/public/test' }), backboneAjaxSpy = sinon.spy(Backbone, 'ajax'), setContentSpy = sinon.spy(model, 'setContent'), fakeServer = sinon.fakeServer.create();
        model.fetchContent();
        fakeServer.respondWith([
            200,
            { 'Content-Type': 'application/json' },
            JSON.stringify('test')
        ]);
        fakeServer.respond();
        expect(backboneAjaxSpy.called).toBeTruthy();
        expect(backboneAjaxSpy.getCall(0).args[0].url).toBe('/rest_v2/resources/public/test?expanded=false');
        expect(backboneAjaxSpy.getCall(0).args[0].type).toBe('GET');
        expect(setContentSpy).toHaveBeenCalledWith('test');
        fakeServer.restore();
        backboneAjaxSpy.restore();
    });
});