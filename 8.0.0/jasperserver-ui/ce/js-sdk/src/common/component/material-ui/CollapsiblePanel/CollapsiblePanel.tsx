import React, {
    forwardRef,
    HTMLAttributes,
    useRef
} from 'react';
import {
    useCollapsiblePanelState
} from './useCollapsiblePanelState';
import {
    Anchor,
    PanelDefinition,
    MaxWidth,
    OnResizeCallback,
    OnResizeStartCallback,
    OnResizeStopCallback,
    OnExpansionStateChangedCallback,
    OnScrollCallback, OnExpandCollapseClick
} from './collapsiblePanelTypes';
import { CollapsiblePanelTabs } from './CollapsiblePanelTabs';
import { PanelsContainer } from './PanelsContainer';
import { useCollapsiblePanelExpansionLayoutEffect } from './useCollapsiblePanelExpansionLayoutEffect';
import { useToggleExpansionStateLayoutEffect } from './useToggleExpansionStateLayoutEffect';
import { useResizeStopLayoutEffect } from './useResizeStopLayoutEffect';
import { useTabsPanelWidth } from './useTabsPanelWidth';

export interface CollapsiblePanelProps {
    panels: PanelDefinition[],
    anchor?: Anchor,
    maxWidth?: MaxWidth,
    onScroll?: OnScrollCallback,
    onResize?: OnResizeCallback,
    onResizeStart?: OnResizeStartCallback
    onResizeStop?: OnResizeStopCallback
    onExpansionStateChanged?: OnExpansionStateChangedCallback,
    onExpandClick?: OnExpandCollapseClick,
    onCollapseClick?: OnExpandCollapseClick,
    wrapperProps?: HTMLAttributes<HTMLDivElement>
}

const emptyFn = () => {};
const getMaxWidth = () => { return window.innerWidth };

export const CollapsiblePanel = forwardRef<HTMLDivElement, CollapsiblePanelProps>(
    ({
        panels,
        anchor = 'right',
        maxWidth = getMaxWidth,
        onScroll = emptyFn,
        onResize = emptyFn,
        onResizeStart = emptyFn,
        onResizeStop = emptyFn,
        onExpansionStateChanged = emptyFn,
        onExpandClick = emptyFn,
        onCollapseClick = emptyFn,
        wrapperProps
    }, ref) => {
        const [panelsState, setPanelState, setSubPanelState] = useCollapsiblePanelState(panels);

        useCollapsiblePanelExpansionLayoutEffect(onResizeStop, panelsState);
        useToggleExpansionStateLayoutEffect(onExpansionStateChanged, panelsState);
        useResizeStopLayoutEffect(onResizeStop, panelsState);

        const panelTabsRef = useRef<HTMLDivElement>(null);
        const tabsPanelWidth = useTabsPanelWidth(panelTabsRef, panelsState);

        const panelsContainer = (
            <PanelsContainer
                anchor={anchor}
                panels={panels}
                panelsState={panelsState}
                maxWidth={maxWidth}
                setPanelState={setPanelState}
                setSubPanelState={setSubPanelState}
                onResizeStart={onResizeStart}
                onResize={onResize}
                onScroll={onScroll}
                onCollapseClick={onCollapseClick}
                tabsPanelWidth={tabsPanelWidth}
            />
        )

        return (
            <div
                ref={ref}
                {...wrapperProps}
            >
                { anchor === 'right' && panelsContainer }

                <CollapsiblePanelTabs
                    ref={panelTabsRef}
                    panels={panels}
                    anchor={anchor}
                    setPanelState={setPanelState}
                    setSubPanelState={setSubPanelState}
                    panelsState={panelsState}
                    onExpandClick={onExpandClick}
                />

                { anchor === 'left' && panelsContainer }
            </div>
        )
    }
);
