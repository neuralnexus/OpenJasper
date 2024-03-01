import React, { forwardRef } from 'react';
import { Accordion as MuiAccordion, AccordionProps as MuiAccordionProps } from '@material-ui/core';

export type AccordionSize = 'normal' | 'small';

const AccordionSizeToClass: {[key in AccordionSize]: string} = {
    small: 'jr-mAccordionSmall',
    normal: ''
}

export type AccordionProps = MuiAccordionProps & {
    size?: AccordionSize
};

export const Accordion = forwardRef<unknown, AccordionProps>(({
    className = '', size = 'normal', children, ...rest
}, ref) => {

    const sizeClass = AccordionSizeToClass[size];

    return (
        <MuiAccordion ref={ref} className={`jr-mAccordion mui ${sizeClass} ${className}`} elevation={0} square {...rest}>
            {children}
        </MuiAccordion>
    )
})
