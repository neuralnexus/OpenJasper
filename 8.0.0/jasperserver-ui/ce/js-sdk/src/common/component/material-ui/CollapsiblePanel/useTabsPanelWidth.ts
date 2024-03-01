import {
    MutableRefObject, useLayoutEffect, useMemo, useState
} from 'react';
import { PanelsState } from './useCollapsiblePanelState';

const getTabsPanelVisibleState = (panelsState: PanelsState) => {
    return panelsState.reduce((acc, panel) => {
        return panel.subPanels.reduce((acc1, subPanel) => {
            return !subPanel.open ? true : acc1
        }, acc)
    }, false as boolean);
};

const calcTabsPanelWidth = (el: HTMLDivElement | null) => {
    if (!el) {
        return 0;
    }

    return el.offsetWidth;
}

export const useTabsPanelWidth = (ref: MutableRefObject<HTMLDivElement | null>, panelsState: PanelsState) => {
    const isTabsPanelInitiallyVisible = useMemo(() => getTabsPanelVisibleState(panelsState), [panelsState]);
    const [tabsPanelWidth, setTabsPanelWidth] = useState(calcTabsPanelWidth(ref.current));

    useLayoutEffect(() => {
        setTabsPanelWidth(calcTabsPanelWidth(ref.current))
    }, [isTabsPanelInitiallyVisible, ref])

    return tabsPanelWidth;
};
