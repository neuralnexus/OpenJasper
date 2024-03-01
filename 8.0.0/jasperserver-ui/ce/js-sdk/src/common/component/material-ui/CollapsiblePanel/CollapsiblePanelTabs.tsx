import React, { forwardRef } from 'react';
import {
    Anchor, DIRECTION_CLASS, OnExpandCollapseClick, PanelDefinition
} from './collapsiblePanelTypes';
import {
    isPanelFullyExpanded,
    PanelsState,
    SetPanelState,
    SetSubPanelState
} from './useCollapsiblePanelState';
import i18nLabel from '../utils/i18nLabel';
import { IconButton } from '../IconButton/IconButton';
import { TabsList } from './TabsList';

export interface CollapsiblePanelTabsProps {
    anchor: Anchor,
    setPanelState: SetPanelState
    setSubPanelState: SetSubPanelState
    panels: PanelDefinition[],
    panelsState: PanelsState,
    onExpandClick: OnExpandCollapseClick
}

export const CollapsiblePanelTabs = forwardRef<HTMLDivElement, CollapsiblePanelTabsProps>(({
    anchor,
    setPanelState,
    setSubPanelState,
    panels,
    panelsState,
    onExpandClick
}, ref) => {

    const { panelsDirectionClass, panelDirectionClass } = DIRECTION_CLASS[anchor];
    const openPanelLabel = i18nLabel('materialUi.component.collapsiblePanel.open');

    const handleExpandClick = (index: number, id: string) => {
        const result = onExpandClick({ type: 'panel', id })
        if (typeof result === 'undefined' || result) {
            setPanelState(index, true)
        }
    }

    return (
        <div className={`jr-mPanels ${panelsDirectionClass} jr-mPanelsMinimized jr-mPanelsStacked mui`} ref={ref}>
            {
                panels.map((panelDefinition, panelIndex) => {
                    const isVisible = !isPanelFullyExpanded(panelIndex, panelsState);
                    const panelState = panelsState[panelIndex];
                    const { id } = panelState;

                    return isVisible && (
                        <div
                            className={`jr-mPanel ${panelDirectionClass} jr-mPanelMinimized mui`}
                            key={id}
                            data-name={`${id}-tabs`}
                        >
                            <div className="jr-mPanel-action mui">
                                <IconButton
                                    data-name={`${id}-tabs-expand`}
                                    aria-label={openPanelLabel}
                                    icon={(anchor === 'right') ? 'caretLeftLarge' : 'caretRightLarge'}
                                    size="small"
                                    className="jr-mPanel-action-button"
                                    onClick={() => handleExpandClick(panelIndex, id)}
                                />
                            </div>
                            <TabsList
                                panelIndex={panelIndex}
                                subPanels={panelDefinition.subPanels}
                                subPanelsState={panelState.subPanels}
                                setSubPanelState={setSubPanelState}
                                onExpandClick={onExpandClick}
                            />
                        </div>
                    )
                })
            }
        </div>
    )
})
