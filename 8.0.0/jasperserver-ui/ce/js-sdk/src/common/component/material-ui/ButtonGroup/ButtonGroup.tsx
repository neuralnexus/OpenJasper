import React, { forwardRef } from 'react';
import {
    ButtonGroup as MuiButtonGroup, ButtonGroupProps as MuiButtonGroupProps
} from '@material-ui/core';

export const ButtonGroup = forwardRef<HTMLDivElement, MuiButtonGroupProps>(({
    className = '', ...rest
}, ref) => {
    return (
        <MuiButtonGroup
            ref={ref}
            className={`jr-mButtongroup mui ${className}`}
            variant="contained"
            color="secondary"
            disableElevation
            {...rest}
        />
    )
})
