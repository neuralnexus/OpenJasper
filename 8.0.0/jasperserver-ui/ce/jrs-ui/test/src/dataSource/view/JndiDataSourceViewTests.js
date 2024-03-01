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
import _ from 'underscore';
import jrsConfigs from 'js-sdk/src/jrs.configs';
import dataSourceConfig from '../test/mock/dataSourceConfigMock'
import JndiDataSourceView from 'src/dataSource/view/JndiDataSourceView';
import BaseDataSourceView from 'src/dataSource/view/BaseDataSourceView';

describe('Testing JndiDataSourceView', function () {
    var jndiDataSourceView, stub = {};
    beforeEach(function () {
        jrsConfigs.addDataSource = dataSourceConfig;
        stub.customInit = sinon.stub(BaseDataSourceView.prototype, 'initialize');
        jndiDataSourceView = new JndiDataSourceView(_.extend(jrsConfigs.addDataSource.initOptions, {
            dataSourceType: undefined,
            dataSource: undefined,
            el: $('[name=dataSourceTestArea]')
        }));
    });
    afterEach(function () {
        jndiDataSourceView.remove();
        BaseDataSourceView.prototype.initialize.restore();
        delete jrsConfigs.addDataSource;
    });
    it('JndiDataSourceView should be defined', function () {
        expect(JndiDataSourceView).toBeDefined();
        expect(typeof JndiDataSourceView).toEqual("function");
    });
    it('JndiDataSourceView should have render method', function () {
        expect(JndiDataSourceView.prototype.render).toBeDefined();
        expect(typeof JndiDataSourceView.prototype.render).toEqual("function");
    });
    it('JndiDataSourceView: check inheritance from BaseDataSourceView', function () {
        expect(stub.customInit).toHaveBeenCalled();
    });
    it('JndiDataSourceView: testing render method', function () {
        // stubbing...
        stub.renderJndiSpecificSection = sinon.stub(JndiDataSourceView.prototype, 'renderJndiSpecificSection');
        stub.renderTimezoneSection = sinon.stub(BaseDataSourceView.prototype, 'renderTimezoneSection');
        stub.renderTestConnectionSection = sinon.stub(BaseDataSourceView.prototype, 'renderTestConnectionSection');
        jndiDataSourceView.render();    // testing...
        // testing...
        expect(stub.customInit).toHaveBeenCalled();
        expect(stub.renderJndiSpecificSection).toHaveBeenCalled();
        expect(stub.renderTimezoneSection).toHaveBeenCalled();
        expect(stub.renderTestConnectionSection).toHaveBeenCalled();    // cleaning...
        // cleaning...
        JndiDataSourceView.prototype.renderJndiSpecificSection.restore();
        BaseDataSourceView.prototype.renderTimezoneSection.restore();
        BaseDataSourceView.prototype.renderTestConnectionSection.restore();
    });
    it('JndiDataSourceView: renderJndiSpecificSection calls append method on $el', function () {
        sinon.stub(BaseDataSourceView.prototype, 'templateData');
        sinon.stub(_, 'template').callsFake(function () {
            return '';
        });
        stub.append = sinon.stub(jndiDataSourceView.$el, 'append');
        jndiDataSourceView.renderJndiSpecificSection();
        expect(stub.append).toHaveBeenCalled();
        expect(_.isString(stub.append.getCall(0).args[0])).toBeTruthy();
        jndiDataSourceView.$el.append.restore();
        _.template.restore();
        BaseDataSourceView.prototype.templateData.restore();
    });
});