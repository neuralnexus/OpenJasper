import { useLayoutEffect, useMemo } from 'react';
import { OnResizeStopCallback } from './collapsiblePanelTypes';
import {
    getPanelSizeState,
    isPanelExpanded, isPanelFullyExpanded, PanelsState
} from './useCollapsiblePanelState';

const getPanelsExpansionHash = (panelsState: PanelsState) => {
    const result = panelsState.reduce((acc, panel, index) => {
        const { panelsHash, allTabsOpen } = acc;

        const panelExpanded = isPanelExpanded(index, panelsState);
        const panelFullyExpanded = isPanelFullyExpanded(index, panelsState);

        return {
            panelsHash: `${panelsHash}${panelsHash ? '-' : ''}${panel.id}:${(panelExpanded ? '1' : '0')}`,
            allTabsOpen: !panelFullyExpanded ? false : allTabsOpen
        };
    }, { panelsHash: '', allTabsOpen: true });

    return `tabs:${result.allTabsOpen ? '1' : '0'}-${result.panelsHash}`
}

export const useCollapsiblePanelExpansionLayoutEffect = (onResizeStop: OnResizeStopCallback, panelsState: PanelsState) => {
    const initialPanelsExpansionHash = useMemo(() => getPanelsExpansionHash(panelsState), [panelsState]);

    useLayoutEffect(() => {
        onResizeStop(getPanelSizeState(panelsState))
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [onResizeStop, initialPanelsExpansionHash])
};
