import React, { forwardRef } from 'react';
import { Link as MuiLink, LinkProps as MuiLinkProps } from '@material-ui/core';
import { ColorToClass, LinkColor } from '../types/LinkTypes';

export type LinkProps = Omit<MuiLinkProps, 'color'> & {
    color?: LinkColor
};

export const Link = forwardRef<HTMLLinkElement, LinkProps>(({ className = '', color = 'default', ...rest }, ref) => {
    const colorClassName = ColorToClass[color];

    return (
        <MuiLink ref={ref} className={`jr-mLink mui ${colorClassName} ${className}`} {...rest} />
    )
})
