/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import $ from 'jquery';
import _ from 'underscore';
import React from 'react';

import 'jquery-ui/ui/widgets/draggable';
import 'jquery-ui/ui/widgets/resizable';

export default class ReactDialog extends React.Component {

    constructor(props) {
        super(props);

        this.initialProps = {
            passRootElement: () => {},
            headerCrossCloser: true,
            onHeaderCrossCloserClick: () => {},
            dialogClass: undefined,
            initialPosition: 'center',
            draggable: true,
            resizable: true,
            initialWidth: undefined,
            initialHeight: undefined,
            minWidth: undefined,
            minHeight: undefined,
            maxWidth: undefined,
            maxHeight: undefined,
            buttons: []
        }
        // save some props as initial values which would not be changed with time
        if (this.props) {
            Object.keys(this.props).forEach((key) => {
                if (typeof (this.props[key]) !== 'undefined') this.initialProps[key] = this.props[key];
            });
        }
    }

    componentDidMount () {
        if (this.initialProps.draggable) {
            this.$el.draggable({
                handle: ".jr-jDialogDraggable",
                addClasses: false,
                containment: "document"
            });
        }

        if (this.initialProps.resizable) {
            const resizeConfig = {
                handles: {
                    "se": this.$el.find(".jr-mDialog-footer-sizer")
                }
            };
            if (this.initialProps.minWidth) resizeConfig.minWidth = this.initialProps.minWidth;
            if (this.initialProps.minHeight) resizeConfig.minHeight = this.initialProps.minHeight;
            if (this.initialProps.maxWidth) resizeConfig.maxWidth = this.initialProps.maxWidth;
            if (this.initialProps.maxHeight) resizeConfig.maxHeight = this.initialProps.maxHeight;

            this.$el.resizable(resizeConfig);
        }
    }

    componentDidUpdate() {
        if (!this.$el.is(':visible')) {
            return;
        }
        this._setupDialog();
    }

    componentWillUnmount() {
        try {
            this.$el.draggable("destroy");
            this.$el.resizable("destroy");
        } catch (err) {
        }
    }

    _setupDialog() {

        if (this.props.initialWidth) {
            this.$el.width(this.props.initialWidth);
        }

        if (this.props.initialHeight) {
            this.$el.height(this.props.initialHeight);
        }

        let position = null;
        if (this.props.initialPosition) {
            if (this.props.initialPosition === 'center') {
                const restrainAreaWidth = $(document).width();
                const restrainAreaHeight = ($(document).height() || document.body.parentNode.scrollHeight);
                const width = this.$el.width();
                const height = this.$el.height();

                position = {
                    top: Math.floor((restrainAreaHeight - height) / 2),
                    left: Math.floor((restrainAreaWidth - width) / 2)
                };
            }
            else if (typeof this.props.initialPosition === 'object') {
                position = {
                    top: this.props.initialPosition.top,
                    left: this.props.initialPosition.left
                };
            }
        }
        if (position) {
            this.$el.offset(position);
        }
    }

    _getFooterSection() {

        const buttons = this.initialProps.buttons.map((button, index) => {

            const buttonClasses = [];
            if (button.type === 'primary') {
                buttonClasses.push('jr-mButtonPrimary');
            }
            if (button.type === 'secondary') {
                buttonClasses.push('jr-mButtonSecondary');
            }

            return (
                <button
                    key = { index }
                    className = { `jr jr-mButton jr-mButtonText ${ buttonClasses.join(' ') }` }
                    onClick = { button.onClick }
                >
                    <span className="jr-mButton-label jr">{ button.title }</span>
                </button>
            );
        })

        return (
            <div className="jr-mDialog-footer jr">
                { buttons }
                { this.initialProps.resizable && (<div className="jr jr-mDialog-footer-sizer" />) }
            </div>
        );
    }

    _setElement (el) {
        if (!el) return;
        if (!this.initialProps.passRootElement) return;
        this.$el = $(el);
        this.initialProps.passRootElement(el);
    }

    render() {

        const props = _.defaults(_.extend({}, this.props), {
            isVisible: false,
            title: 'Dialog title',
            dialogClass: undefined
        });

        const classes = [];
        if (!props.isVisible) {
            classes.push('jr-isHidden');
        }
        if (props.dialogClass) {
            classes.push(props.dialogClass);
        }

        const dialogStyles = {
            position: 'fixed',
            zIndex: 4000
        };

        const headerClasses = [];
        if (this.initialProps.draggable) {
            headerClasses.push('jr-jDialogDraggable');
        }

        return (
            <div
                ref = { this._setElement.bind(this) }
                className = { `jr jr-mDialog ${ classes.join(' ') }` }
                style= { dialogStyles }
            >
                <div className={`jr jr-mDialog-header ${ headerClasses.join(' ') }`}>
                    <h1 className="jr-mDialog-header-title jr-jDialogTitle jr">{ props.title }</h1>
                    { this.initialProps.headerCrossCloser && (
                        <button
                            className="jr-mDialog-header-close jr"
                            onClick = { this.initialProps.onHeaderCrossCloserClick }
                        >
                            <span className="jr-mButton-icon jr-mIcon jr-cancel jr"/>
                        </button>
                    )}
                </div>

                <div className="jr-mDialog-body jr">
                    { this.props.children }
                </div>

                { this._getFooterSection() }
            </div>
        );
    }
}
