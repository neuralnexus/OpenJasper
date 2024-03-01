import React, { forwardRef } from 'react';
import {
    IconButton as MuiIconButton, ButtonProps as MuiButtonProps, IconButtonProps as MuiIconButtonProps
} from '@material-ui/core';
import {
    ButtonColor, ColorToClass, SizeToClass, VariantToClassName
} from '../types/ButtonTypes';

export type IconButtonProps = MuiIconButtonProps & {
    icon: string,
    variant?: MuiButtonProps['variant'],
    selected?: boolean,
    color?: ButtonColor
}

export const IconButton = forwardRef<HTMLButtonElement, IconButtonProps>(({
    color = 'secondary', classes = {}, className = '', size = 'medium', icon, variant = 'text', selected = false, ...rest
}, ref) => {

    const { label: labelClasses = '', ...restClasses } = classes;
    const iconClassName = `jr-${icon}`;
    const selectedClassName = selected ? 'jr-Mui-selected' : '';

    return (
        <MuiIconButton
            ref={ref}
            classes={{ label: `jr-mButton-icon jr-mIcon mui ${iconClassName} ${labelClasses}`, ...restClasses }}
            className={`jr-mButton ${ColorToClass[color]} ${SizeToClass[size]} mui ${VariantToClassName[variant]} ${selectedClassName} ${className}`}
            {...rest}
        />
    )
})
