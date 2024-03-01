import { useLayoutEffect, useMemo } from 'react';
import { OnResizeStopCallback } from './collapsiblePanelTypes';
import { getPanelSizeState, PanelsState } from './useCollapsiblePanelState';

const getSubPanelsWidthHeightHash = (panelsState: PanelsState) => {
    return panelsState.reduce((acc, panel) => {
        const panelWidthHash = `${acc}${acc.length ? '-' : ''}${panel.width}:`;
        return panel.subPanels.reduce((acc1, subPanel) => {
            return `${acc1}${subPanel.height}`
        }, panelWidthHash);
    }, '');
};

export const useResizeStopLayoutEffect = (callback: OnResizeStopCallback, panelsState: PanelsState) => {
    const initialPanelsWidthHeightHash = useMemo(() => getSubPanelsWidthHeightHash(panelsState), [panelsState]);
    useLayoutEffect(() => {
        callback(getPanelSizeState(panelsState));
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [callback, initialPanelsWidthHeightHash])
};
