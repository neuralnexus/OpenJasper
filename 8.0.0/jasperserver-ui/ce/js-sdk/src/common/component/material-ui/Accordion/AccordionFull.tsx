import React, { forwardRef, ReactNode } from 'react';
import { Accordion, AccordionProps } from './Accordion';
import { AccordionSummary, AccordionSummaryProps } from '../AccordionSummary/AccordionSummary';
import { AccordionDetails, AccordionDetailsProps } from '../AccordionDetails/AccordionDetails';

export type AccordionFullProps = AccordionProps & {
    id?: string,
    SummaryProps?: AccordionSummaryProps,
    summary?: ReactNode
    DetailsProps?: AccordionDetailsProps
};

export const AccordionFull = forwardRef<unknown, AccordionFullProps>(({
    id, size = 'normal', SummaryProps, summary, DetailsProps, children, ...rest
}, ref) => {
    const headerId = id ? `${id}-header` : undefined;
    const contentId = id ? `${id}-content` : undefined;

    return (
        <Accordion ref={ref} size={size} {...rest}>
            <AccordionSummary id={headerId} aria-controls={contentId} size={size} {...SummaryProps}>{summary}</AccordionSummary>
            <AccordionDetails {...DetailsProps}>{children}</AccordionDetails>
        </Accordion>
    )
})
