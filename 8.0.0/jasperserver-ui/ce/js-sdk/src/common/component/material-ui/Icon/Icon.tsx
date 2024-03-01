import React, { forwardRef } from 'react';
import {
    Icon as MuiIcon, IconProps as MuiIconProps
} from '@material-ui/core';
import { IconSize, SizeToClass } from '../types/IconTypes';

export type IconProps = MuiIconProps & {
    icon: string
    size?: IconSize
};

export const Icon = forwardRef<HTMLButtonElement, IconProps>(({
    className = '', icon, size = 'medium', ...rest
}, ref) => {

    const iconClassName = `jr-${icon}`;

    return (
        <MuiIcon
            ref={ref}
            className={`jr-mIcon mui ${iconClassName} ${className} ${SizeToClass[size]}`}
            {...rest}
        />
    )
})
