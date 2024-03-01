import React, { FC } from 'react';
import { Resizable, Enable, HandleClassName } from 're-resizable';
import {
    Anchor,
    DIRECTION_CLASS, OnExpandCollapseClick, MaxWidth, OnResizeCallback,
    OnResizeStartCallback, OnScrollCallback,
    PanelDefinition
} from './collapsiblePanelTypes';
import {
    isPanelExpanded, PanelsState, PanelState, SetPanelState, SetSubPanelState, PANEL_MARGIN_RIGHT
} from './useCollapsiblePanelState';
import { SinglePanel } from './SinglePanel';
import { SubPanelsList } from './SubPanelsList';
import { useMaxWidthState } from './useMaxWidthState';

export interface PanelsContainerProps {
    anchor: Anchor,
    panels: PanelDefinition[],
    panelsState: PanelsState,
    setPanelState: SetPanelState,
    maxWidth: MaxWidth,
    setSubPanelState: SetSubPanelState,
    onResizeStart: OnResizeStartCallback,
    onResize: OnResizeCallback
    onScroll: OnScrollCallback
    onCollapseClick: OnExpandCollapseClick,
    tabsPanelWidth: number
}

const ResizableEnableByAnchor: {[key in Anchor]: Enable} = {
    left: { right: true },
    right: { left: true }
};

const ResizeDirectionClasses: {[key in Anchor]: HandleClassName} = {
    right: { left: 'jr-mPanel-resizerHorizontal mui' },
    left: { right: '' }
};

interface ResizableContainerProps {
    anchor: Anchor,
    panelState: PanelState,
    onResize: OnResizeCallback,
    onResizeStart: OnResizeStartCallback,
    setPanelState: SetPanelState,
    maxWidth: number,
    panelIndex: number
}

const ResizableContainer: FC<ResizableContainerProps> = ({
    children, anchor, panelState, onResize, onResizeStart, setPanelState, panelIndex, maxWidth
}) => {
    return (
        <Resizable
            enable={ResizableEnableByAnchor[anchor]}
            handleClasses={ResizeDirectionClasses[anchor]}
            size={{
                width: panelState.width,
                height: '100%'
            }}
            minWidth={panelState.minWidth}
            maxWidth={maxWidth}
            style={{ marginRight: `${PANEL_MARGIN_RIGHT}px` }}
            onResize={(event, direction, elementRef, delta) => onResize({
                type: 'panel',
                id: panelState.id,
                delta: delta.width
            })}
            onResizeStart={() => onResizeStart({
                type: 'panel',
                id: panelState.id
            })}
            onResizeStop={(event, direction, elementRef, delta) => {
                setPanelState(panelIndex, undefined, panelState.width + delta.width);
            }}
        >
            {children}
        </Resizable>
    )
}

export const PanelsContainer: FC<PanelsContainerProps> = ({
    panels,
    panelsState,
    setPanelState,
    maxWidth,
    setSubPanelState,
    anchor,
    onResizeStart,
    onResize,
    onScroll,
    onCollapseClick,
    tabsPanelWidth
}) => {

    const { panelsDirectionClass, panelDirectionClass } = DIRECTION_CLASS[anchor];

    const handlePanelCollapse = (index: number, id: string) => {
        const result = onCollapseClick({ type: 'panel', id })
        if (typeof result === 'undefined' || result) {
            setPanelState(index, false)
        }
    }

    const panelMaxWidth = useMaxWidthState(maxWidth, tabsPanelWidth, panelsState);

    return (
        <div className={`jr-mPanels mui ${panelsDirectionClass}`}>
            {
                panels.map((panelItem, panelIndex) => {
                    const isOpen = isPanelExpanded(panelIndex, panelsState);
                    const panelState = panelsState[panelIndex];
                    return isOpen && (
                        <ResizableContainer
                            key={panelState.id}
                            onResize={onResize}
                            onResizeStart={onResizeStart}
                            panelIndex={panelIndex}
                            panelState={panelState}
                            anchor={anchor}
                            setPanelState={setPanelState}
                            maxWidth={panelMaxWidth[panelIndex].maxWidth}
                        >
                            <SinglePanel
                                className={`${panelDirectionClass}`}
                                anchor={anchor}
                                id={panelState.id}
                                onCollapse={() => handlePanelCollapse(panelIndex, panelState.id)}
                            >
                                <SubPanelsList
                                    panel={panels[panelIndex]}
                                    panelState={panelState}
                                    setSubPanelState={setSubPanelState}
                                    panelIndex={panelIndex}
                                    onResize={onResize}
                                    onResizeStart={onResizeStart}
                                    onScroll={onScroll}
                                    onCollapseClick={onCollapseClick}
                                />
                            </SinglePanel>
                        </ResizableContainer>
                    )
                })
            }
        </div>
    )
}
