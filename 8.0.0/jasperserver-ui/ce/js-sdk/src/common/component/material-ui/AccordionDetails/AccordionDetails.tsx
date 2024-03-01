import React, { forwardRef } from 'react';
import { AccordionDetails as MuiAccordionDetails, AccordionDetailsProps as MuiAccordionDetailsProps } from '@material-ui/core';

export type AccordionDetailsProps = MuiAccordionDetailsProps;

export const AccordionDetails = forwardRef<unknown, MuiAccordionDetailsProps>(({
    className = '', children, ...rest
}, ref) => {

    return (
        <MuiAccordionDetails ref={ref} className={`jr-mAccordion-body mui ${className}`} {...rest}>{children}</MuiAccordionDetails>
    )
})
