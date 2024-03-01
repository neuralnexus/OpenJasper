import React, { forwardRef } from 'react';
import { Divider as MuiDivider, DividerProps } from '@material-ui/core';

export const Divider = forwardRef<HTMLHRElement, DividerProps>(({
    className = '', ...rest
}, ref) => {
    return (
        <MuiDivider ref={ref} component="div" classes={{ root: `jr-mInputs-divider mui ${className}` }} {...rest} />
    )
});
