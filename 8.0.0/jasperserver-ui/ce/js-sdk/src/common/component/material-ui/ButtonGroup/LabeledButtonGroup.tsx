import React, { forwardRef } from 'react';
import {
    ButtonGroupProps as MuiButtonGroupProps, InputLabel, InputLabelProps as MuiInputLabelProps
} from '@material-ui/core';
import { ButtonGroup } from './ButtonGroup';
import { SizeToClass } from '../types/InputTypes';

export type ButtonGroupProps = MuiButtonGroupProps & {
    label: string,
    buttonGroupClassName?: string,
    InputLabelProps?: MuiInputLabelProps
}

export const LabeledButtonGroup = forwardRef<HTMLDivElement, ButtonGroupProps>(({
    id, label, className = '', buttonGroupClassName = '', size, InputLabelProps = {}, children, ...rest
}, ref) => {

    const inputLabelId = id ? `${id}-label` : undefined;
    const buttonGroupId = id ? `${id}-buttonGroup` : undefined;
    const ariaLabeledBy = inputLabelId || undefined;

    const { className: inputLabelClassName = '', ...restInputLabelProps } = InputLabelProps;

    return (
        <div id={id} ref={ref} className={`jr-mInput jr-mInputInline jr-mInputButtons mui ${SizeToClass[size ?? 'medium']} ${className}`}>
            <InputLabel id={inputLabelId} className={`jr-mInput-label mui ${inputLabelClassName}`} {...restInputLabelProps}>{label}</InputLabel>
            <ButtonGroup id={buttonGroupId} className={buttonGroupClassName} size={size} aria-labelledby={ariaLabeledBy} {...rest}>
                {children}
            </ButtonGroup>
        </div>
    )
})
