import React, {
    FC, UIEvent, useLayoutEffect, useRef, useState
} from 'react';
import { Card, CardContent, CardHeader } from '@material-ui/core';
import { IconButton } from '../IconButton/IconButton';
import i18nLabel from '../utils/i18nLabel';
import { SubPanelSlots } from './collapsiblePanelTypes';

export interface SubPanelProps {
    id: string,
    label: string,
    slots?: SubPanelSlots,
    onClose: () => void,
    onScroll: (scrollPos: number) => void,
    scrollPos: number
}

export const SubPanel: FC<SubPanelProps> = ({
    id, label, children, slots = { }, onClose, onScroll, scrollPos
}) => {
    const { headerAction } = slots;
    const closeSubPanelLabel = i18nLabel('materialUi.component.collapsiblePanel.close');
    const contentRef = useRef<HTMLDivElement>(null);

    const [internalScrollPos, setInternalScrollPos] = useState(0);

    const handleScroll = (ev: UIEvent<HTMLDivElement>) => {
        const newScrollPos = (ev.target as HTMLDivElement).scrollTop;
        setInternalScrollPos(newScrollPos);
        if (newScrollPos !== scrollPos) {
            onScroll(newScrollPos)
        }
    }

    useLayoutEffect(() => {
        if (contentRef.current && scrollPos !== internalScrollPos) {
            contentRef.current.scrollTop = scrollPos;
            setInternalScrollPos(scrollPos);
        }
    }, [contentRef, internalScrollPos, scrollPos])

    return (
        <Card elevation={0}>
            <CardHeader
                className="jr-mPanel-section-header mui"
                classes={{ content: 'jr-mPanel-section-header-title mui' }}
                title={label}
                action={(
                    <>
                        {headerAction}
                        <IconButton
                            data-name={`${id}-collapse`}
                            icon="cancel"
                            variant="contained"
                            color="default"
                            aria-label={closeSubPanelLabel}
                            onClick={onClose}
                        />
                    </>
                )}
                disableTypography
            />
            <CardContent ref={contentRef} component="div" onScroll={handleScroll}>{children}</CardContent>
        </Card>
    )
};
