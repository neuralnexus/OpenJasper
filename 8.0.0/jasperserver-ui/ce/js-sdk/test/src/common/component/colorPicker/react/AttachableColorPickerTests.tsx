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

import * as React from 'react';
import sinon, { SinonStub } from 'sinon';
import { mount } from 'enzyme';
import positionUtil from 'src/common/component/base/util/attachableComponentPositionUtil';
import { withAbilityToAttach } from 'src/common/component/colorPicker/react/AttachableColorPicker';
import { createFakeComponent } from '../../../../../mock/fakeComponent';

// @ts-ignore
import visibleAttachableColorPickerSnapshot from './snapshots/visibleAttachableColorPickerSnapshot.htm';
// @ts-ignore
import visibleAttachableColorPickerWithDisabledAlphaSnapshot from './snapshots/visibleAttachableColorPickerWithDisabledAlphaSnapshot.htm';
// @ts-ignore
import invisibleAttachableColorPickerSnapshot from './snapshots/invisibleAttachableColorPickerSnapshot.htm';
import customMatcher from '../../../../../tools/customMatcher';

describe('AttachableColorPicker Tests.', () => {
    let attachTo: any,
        dom: any,
        sandbox: any;

    const addEventListenerStubInfo = {
            event: '',
            callback(e: any) : void {} // eslint-disable-line @typescript-eslint/no-unused-vars
        },
        removeEventListenerStubInfo = {
            event: '',
            callback(e: any) : void {} // eslint-disable-line @typescript-eslint/no-unused-vars
        };

    beforeEach(() => {
        sandbox = sinon.createSandbox();
        jasmine.addMatchers(customMatcher);

        sandbox.stub(positionUtil, 'getPosition').returns({
            top: 100,
            left: 100
        });

        dom = {
            createElement(name: string) {
                return {
                    name
                };
            },
            addEventListener(event: string, callback: (e: any) => void) {
                addEventListenerStubInfo.event = event;
                addEventListenerStubInfo.callback = callback;
            },

            removeEventListener(event: string, callback: (e: any) => void) {
                removeEventListenerStubInfo.event = event;
                removeEventListenerStubInfo.callback = callback;
            }
        };

        attachTo = {
            id: 'element',
            contains: sandbox.stub(),
            isEqualNode: sandbox.stub()
        };
    });

    afterEach(() => {
        sandbox.restore();
    });

    it('should render visible color picker', () => {
        const AttachableColorPicker = withAbilityToAttach(
            createFakeComponent('ColorPicker'), dom
        );

        const show = true;
        const color = 'color';
        const onChangeComplete = function onChangeComplete() {};
        const onHide = () => {};
        const padding = { top: 5, left: 5 };

        const component = mount(
            <AttachableColorPicker
                padding={padding}
                show={show}
                color={color}
                onChangeComplete={onChangeComplete}
                onHide={onHide}
                attachTo={attachTo}
            />
        );

        expect(component.html()).toEqualSnapshot(visibleAttachableColorPickerSnapshot);

        const stub = positionUtil.getPosition as SinonStub;

        expect(stub).toHaveBeenCalledWith(attachTo, padding, component.getDOMNode());
    });

    it('should render visible color picker with disabledAlpha and transparentPreset', () => {
        const AttachableColorPicker = withAbilityToAttach(
            createFakeComponent('ColorPicker'), dom
        );

        const show = true;
        const color = 'color';
        const onChangeComplete = function onChangeComplete() {};
        const onHide = () => {};
        const padding = { top: 5, left: 5 };

        const component = mount(
            <AttachableColorPicker
                padding={padding}
                show={show}
                color={color}
                showTransparentPreset={false}
                onChangeComplete={onChangeComplete}
                onHide={onHide}
                attachTo={attachTo}
            />
        );

        expect(component.html()).toEqualSnapshot(visibleAttachableColorPickerWithDisabledAlphaSnapshot);
    });

    it('should render invisible color picker', () => {
        const AttachableColorPicker = withAbilityToAttach(
            createFakeComponent('ColorPicker'), dom
        );

        const show = false;
        const color = 'color';
        const onChangeComplete = function onChangeComplete() {};
        const onHide = () => {};

        const component = mount(
            <AttachableColorPicker
                padding={{ top: 5, left: 5 }}
                show={show}
                color={color}
                onChangeComplete={onChangeComplete}
                onHide={onHide}
                attachTo={attachTo}
            />
        );

        expect(component.html()).toEqualSnapshot(invisibleAttachableColorPickerSnapshot);
    });

    it('should call onHide callback if click on document was made', () => {
        const AttachableColorPicker = withAbilityToAttach(
            createFakeComponent('ColorPicker'), dom
        );

        const show = true;
        const color = 'color';
        const onChangeComplete = function onChangeComplete() {};
        const onHide = sandbox.stub();
        const padding = { top: 5, left: 5 };

        const component = mount(
            <AttachableColorPicker
                padding={padding}
                show={show}
                color={color}
                onChangeComplete={onChangeComplete}
                onHide={onHide}
                attachTo={attachTo}
            />
        );

        expect(component.html()).toEqualSnapshot(visibleAttachableColorPickerSnapshot);

        const colorPickerEl = component.getDOMNode();

        sandbox.stub(colorPickerEl, 'contains').returns(false);
        sandbox.stub(colorPickerEl, 'isEqualNode').returns(false);

        expect(addEventListenerStubInfo.event).toEqual('mousedown');

        addEventListenerStubInfo.callback({
            target: 'target'
        });

        const stub = positionUtil.getPosition as SinonStub;

        expect(stub).toHaveBeenCalledWith(attachTo, padding, colorPickerEl);

        // @ts-ignore
        expect(colorPickerEl.contains).toHaveBeenCalledWith('target');
        // @ts-ignore
        expect(colorPickerEl.isEqualNode).toHaveBeenCalledWith('target');
        expect(attachTo.contains).toHaveBeenCalledWith('target');
        expect(attachTo.isEqualNode).toHaveBeenCalledWith('target');

        expect(onHide).toHaveBeenCalled();
    });

    it('should not hide color picker if color picker contains event target', () => {
        const AttachableColorPicker = withAbilityToAttach(
            createFakeComponent('ColorPicker'), dom
        );

        const show = true;
        const color = 'color';
        const onChangeComplete = function onChangeComplete() {};
        const onHide = sandbox.stub();

        const component = mount(
            <AttachableColorPicker
                padding={{ top: 5, left: 5 }}
                show={show}
                color={color}
                onChangeComplete={onChangeComplete}
                onHide={onHide}
                attachTo={attachTo}
            />
        );

        expect(component.html()).toEqualSnapshot(visibleAttachableColorPickerSnapshot);

        const colorPickerEl = component.getDOMNode();

        sandbox.stub(colorPickerEl, 'contains').returns(true);
        sandbox.stub(colorPickerEl, 'isEqualNode').returns(false);

        addEventListenerStubInfo.callback({
            target: 'target'
        });

        expect(onHide).not.toHaveBeenCalled();
    });

    it('should not hide color picker if click on color picker was made', () => {
        const AttachableColorPicker = withAbilityToAttach(
            createFakeComponent('ColorPicker'), dom
        );

        const show = true;
        const color = 'color';
        const onChangeComplete = function onChangeComplete() {};
        const onHide = sandbox.stub();

        const component = mount(
            <AttachableColorPicker
                padding={{ top: 5, left: 5 }}
                show={show}
                color={color}
                onChangeComplete={onChangeComplete}
                onHide={onHide}
                attachTo={attachTo}
            />
        );

        expect(component.html()).toEqualSnapshot(visibleAttachableColorPickerSnapshot);

        const colorPickerEl = component.getDOMNode();

        sandbox.stub(colorPickerEl, 'contains').returns(false);
        sandbox.stub(colorPickerEl, 'isEqualNode').returns(true);

        addEventListenerStubInfo.callback({
            target: 'target'
        });

        expect(onHide).not.toHaveBeenCalled();
    });

    it('should not hide color picker if attachTo contains event target', () => {
        const AttachableColorPicker = withAbilityToAttach(
            createFakeComponent('ColorPicker'), dom
        );

        const show = true;
        const color = 'color';
        const onChangeComplete = function onChangeComplete() {};
        const onHide = sandbox.stub();

        const component = mount(
            <AttachableColorPicker
                padding={{ top: 5, left: 5 }}
                show={show}
                color={color}
                onChangeComplete={onChangeComplete}
                onHide={onHide}
                attachTo={attachTo}
            />
        );

        expect(component.html()).toEqualSnapshot(visibleAttachableColorPickerSnapshot);

        const colorPickerEl = component.getDOMNode();

        sandbox.stub(colorPickerEl, 'contains').returns(false);
        sandbox.stub(colorPickerEl, 'isEqualNode').returns(false);

        const stub = attachTo.contains as SinonStub;

        stub.returns(true);

        addEventListenerStubInfo.callback({
            target: 'target'
        });

        expect(onHide).not.toHaveBeenCalled();
    });

    it('should not hide color picker if click on attachTo was made', () => {
        const AttachableColorPicker = withAbilityToAttach(
            createFakeComponent('ColorPicker'), dom
        );

        const show = true;
        const color = 'color';
        const onChangeComplete = function onChangeComplete() {};
        const onHide = sandbox.stub();

        const component = mount(
            <AttachableColorPicker
                padding={{ top: 5, left: 5 }}
                show={show}
                color={color}
                onChangeComplete={onChangeComplete}
                onHide={onHide}
                attachTo={attachTo}
            />
        );

        expect(component.html()).toEqualSnapshot(visibleAttachableColorPickerSnapshot);

        const colorPickerEl = component.getDOMNode();

        sandbox.stub(colorPickerEl, 'contains').returns(false);
        sandbox.stub(colorPickerEl, 'isEqualNode').returns(false);

        const stub = attachTo.isEqualNode as SinonStub;

        stub.returns(true);

        addEventListenerStubInfo.callback({
            target: 'target'
        });

        expect(onHide).not.toHaveBeenCalled();
    });

    it('should remove on document event listener on unmount', () => {
        const AttachableColorPicker = withAbilityToAttach(
            createFakeComponent('ColorPicker'), dom
        );

        const show = true;
        const color = 'color';
        const onChangeComplete = function onChangeComplete() {};
        const onHide = () => {};

        const component = mount(
            <AttachableColorPicker
                padding={{ top: 5, left: 5 }}
                show={show}
                color={color}
                onChangeComplete={onChangeComplete}
                onHide={onHide}
                attachTo={attachTo}
            />
        );

        component.unmount();

        expect(removeEventListenerStubInfo.event).toEqual('mousedown');
        expect(removeEventListenerStubInfo.callback).toEqual(addEventListenerStubInfo.callback);
    });
});
