import React, { forwardRef } from 'react';
import { AccordionSummary as MuiAccordionSummary, AccordionSummaryProps as MuiAccordionSummaryProps } from '@material-ui/core';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';
import { AccordionSize } from '../Accordion/Accordion';

const AccordionSummarySizeToClass: {[key in AccordionSize]: string} = {
    small: 'jr-mAccordion-titleShaded',
    normal: ''
}

export type AccordionSummaryProps = MuiAccordionSummaryProps & {
    size?: AccordionSize
}

export const AccordionSummary = forwardRef<HTMLDivElement, AccordionSummaryProps>(({
    className = '', classes = {}, size = 'normal', children, ...rest
}, ref) => {
    const { content = '', expandIcon = '', ...restClasses } = classes;

    const sizeClass = AccordionSummarySizeToClass[size];

    return (
        <MuiAccordionSummary
            ref={ref}
            className={`jr-mAccordion-title mui ${sizeClass} ${className}`}
            classes={{
                content: `jr-mAccordion-title-text mui ${content}`,
                expandIcon: `jr-mAccordion-title-icon mui ${expandIcon}`,
                ...restClasses
            }}
            expandIcon={<ChevronRightIcon />}
            {...rest}
        >
            {children}
        </MuiAccordionSummary>
    )
})
