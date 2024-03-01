import React, { forwardRef } from 'react';
import { MenuItem } from '@material-ui/core';

export type SelectItemProps = Parameters<typeof MenuItem>[0];

export const SelectItem = forwardRef<HTMLLIElement, SelectItemProps>(({
    className = '', children, ...rest
}, ref) => {
    return (
        <MenuItem ref={ref} className={`jr-mInput-select-item mui ${className}`} {...rest}>{children}</MenuItem>
    )
})
