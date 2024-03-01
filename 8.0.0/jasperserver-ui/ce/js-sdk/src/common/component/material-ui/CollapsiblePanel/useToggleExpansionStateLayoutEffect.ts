import { useLayoutEffect, useMemo } from 'react';
import { OnExpansionStateChangedCallback } from './collapsiblePanelTypes';
import { PanelsState } from './useCollapsiblePanelState';

const getPanelsExpansionOpenState = (panelsState: PanelsState) => {
    return panelsState.map((panel) => {
        return { id: panel.id, subPanels: panel.subPanels.map((subPanel) => ({ id: subPanel.id, open: subPanel.open })) };
    });
};

const getSubPanelsExpansionHash = (panelsState: PanelsState) => {
    return panelsState.reduce((acc, panel) => {
        return panel.subPanels.reduce((acc1, subPanel) => {
            return `${acc1}${subPanel.open}`
        }, acc)
    }, '');
};

export const useToggleExpansionStateLayoutEffect = (callback: OnExpansionStateChangedCallback, panelsState: PanelsState) => {
    const initialPanelsExpansionHash = useMemo(() => getSubPanelsExpansionHash(panelsState), [panelsState]);

    useLayoutEffect(() => {
        callback(getPanelsExpansionOpenState(panelsState));
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [callback, initialPanelsExpansionHash])
};
