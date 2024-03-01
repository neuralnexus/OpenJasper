import React, { FC } from 'react';
import { Button, List, ListItem } from '@material-ui/core';
import { OnExpandCollapseClick, SubPanelDefinition } from './collapsiblePanelTypes';
import { SetSubPanelState, SubPanelState } from './useCollapsiblePanelState';
import i18nLabel from '../utils/i18nLabel';

export interface TabsListProps {
    panelIndex: number,
    subPanels: SubPanelDefinition[],
    subPanelsState: SubPanelState[],
    setSubPanelState: SetSubPanelState,
    onExpandClick: OnExpandCollapseClick
}

export const TabsList: FC<TabsListProps> = ({
    subPanelsState, panelIndex, subPanels, setSubPanelState, onExpandClick
}) => {
    const openSubPanelLabel = i18nLabel('materialUi.component.collapsiblePanel.open');

    const handleExpandClick = (index: number, id: string) => {
        const result = onExpandClick({ type: 'subpanel', id })
        if (typeof result === 'undefined' || result) {
            setSubPanelState(panelIndex, index, true)
        }
    }

    return (
        <List
            disablePadding
            className="jr-mPanel-sections mui"
            component="ul"
        >
            {
                subPanels.map((subPanelDefinition, subPanelIndex) => {
                    const subPanelState = subPanelsState[subPanelIndex];
                    const isTabVisible = !subPanelState.open;

                    return isTabVisible && (
                        <ListItem
                            disableGutters
                            className="jr-mPanel-section mui"
                            component="li"
                            key={subPanelState.id}
                            data-name={`${subPanelState.id}-tab`}
                        >
                            <Button
                                data-name={`${subPanelState.id}-tab-expand`}
                                classes={{ root: 'jr-mPanel-section-action mui' }}
                                onClick={() => handleExpandClick(subPanelIndex, subPanelState.id)}
                                aria-label={openSubPanelLabel}
                            >
                                {subPanelDefinition.label}
                            </Button>
                        </ListItem>
                    )
                })
            }
        </List>
    )
}
