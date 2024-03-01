import React, { FC } from 'react';
import { Drawer } from '@material-ui/core';
import { IconButton } from '../IconButton/IconButton';
import { Anchor } from './collapsiblePanelTypes';
import i18nLabel from '../utils/i18nLabel';

export interface SinglePanelProps {
    anchor: Anchor,
    className: string,
    id: string,
    onCollapse: () => void,
}

export const SinglePanel: FC<SinglePanelProps> = (
    {
        onCollapse,
        anchor,
        className = '',
        id,
        children
    }
) => {
    const closePanelLabel = i18nLabel('materialUi.component.collapsiblePanel.close');
    return (
        <Drawer
            variant="persistent"
            anchor={anchor}
            open
            style={{ width: '100%', height: '100%' }}
            className={`mui jr-mPanel ${className}`}
            data-name={id}
        >
            <div className="jr-mPanel-action mui">
                <IconButton
                    data-name={`${id}-collapse`}
                    icon={(anchor === 'right') ? 'caretRightLarge' : 'caretLeftLarge'}
                    size="small"
                    className="jr-mPanel-action-button"
                    aria-label={closePanelLabel}
                    onClick={() => onCollapse()}
                />
            </div>
            {children}
        </Drawer>
    )
};
