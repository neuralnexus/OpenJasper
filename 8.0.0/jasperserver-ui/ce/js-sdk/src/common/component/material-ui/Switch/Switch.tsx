import React, { forwardRef, HTMLAttributes } from 'react';
import {
    FormControlLabel, FormControlLabelProps, Switch as MuiSwitch, SwitchProps as MuiSwitchProps
} from '@material-ui/core';
import { INLINE_CLASS, InputSize, SizeToClass } from '../types/InputTypes';

export interface SwitchProps extends Omit<FormControlLabelProps, 'control'> {
    size?: Exclude<InputSize, 'large'>,
    control?: React.ReactElement<any, any>,
    SwitchProps?: Partial<MuiSwitchProps>,
    WrapperProps?: HTMLAttributes<HTMLDivElement> & {[key: string]: any}
}

export const Switch = forwardRef<HTMLDivElement, SwitchProps>(({
    classes = {}, size = 'medium', control, SwitchProps = {}, WrapperProps, ...rest
}, ref) => {

    const { className: switchPropsClassName = '', ...restSwitchProps } = SwitchProps;

    return (
        <div ref={ref} className={`jr-mInput jr-mInputSwitch ${INLINE_CLASS} ${SizeToClass[size]} mui`} {...WrapperProps}>
            <FormControlLabel
                classes={{ label: `jr-mInput-label mui ${classes?.root ?? ''}`, ...classes }}
                control={control ?? <MuiSwitch size={size} color="primary" className={`jr-mInput-switch mui ${switchPropsClassName}`} {...restSwitchProps} />}
                labelPlacement="start"
                {...rest}
            />
        </div>
    )
})
