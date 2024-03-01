import {
    useEffect, useMemo, useState
} from 'react';
import _ from 'underscore';
import { MaxWidth } from './collapsiblePanelTypes';
import { isPanelExpanded, PanelsState, PANEL_MARGIN_RIGHT } from './useCollapsiblePanelState';

interface PanelMaxWidthState {
    id: string,
    maxWidth: number
}

type MaxWidthState = PanelMaxWidthState[];

const getMaxWidth = (maxWidth: MaxWidth) => {
    return typeof maxWidth === 'function' ? maxWidth() : maxWidth;
}

export const generateInitialState = (collapsiblePanelMaxWidth: number, panelsState: PanelsState): MaxWidthState => {
    const currentTotalPanelsWidth = panelsState.reduce((acc, panel, index) => {
        const isExpanded = isPanelExpanded(index, panelsState);
        return acc + (isExpanded ? panel.width + PANEL_MARGIN_RIGHT : 0);
    }, 0);

    const freeSpace = collapsiblePanelMaxWidth - currentTotalPanelsWidth;

    return panelsState.reduce((acc, panel) => {
        const maxWidthForPanel = Math.max(panel.width, panel.width + freeSpace);
        return [...acc, {
            id: panel.id,
            maxWidth: maxWidthForPanel
        }];
    }, [] as MaxWidthState);
}

const getPanelsWidthOpenHash = (panelsState: PanelsState) => {
    return panelsState.reduce((acc, panel, index) => {
        const isExpanded = isPanelExpanded(index, panelsState);
        return `${acc}${acc.length ? '-' : ''}${panel.width}:${isExpanded}`;
    }, '');
};

const useCollapsiblePanelWidthOpenState = (callback: () => void, panelsState: PanelsState) => {
    const initialPanelsWidthHash = useMemo(() => getPanelsWidthOpenHash(panelsState), [panelsState]);
    useEffect(() => {
        callback();
    }, [callback, initialPanelsWidthHash])
};

export const WINDOW_RESIZE_MAX_WIDTH_CALC_DEBOUNCE = 500;

const calcPanelMaxWidth = (panelMaxWidth: MaxWidth, tabsPanelWidth: number) => getMaxWidth(panelMaxWidth) - tabsPanelWidth;

export const useMaxWidthState = (panelMaxWidth: MaxWidth, tabsPanelWidth: number, panelsState: PanelsState) => {
    const initialMaxWidth = useMemo(() => calcPanelMaxWidth(panelMaxWidth, tabsPanelWidth), [panelMaxWidth, tabsPanelWidth]);
    const [collapsiblePanelMaxWidth, setCollapsiblePanelMaxWidth] = useState(initialMaxWidth);

    useEffect(() => {
        setCollapsiblePanelMaxWidth(calcPanelMaxWidth(panelMaxWidth, tabsPanelWidth))
    }, [panelMaxWidth, tabsPanelWidth]);

    const initialState = useMemo(() => {
        return generateInitialState(collapsiblePanelMaxWidth, panelsState)
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [collapsiblePanelMaxWidth]);

    const [maxWidthState, setMaxWidthState] = useState(initialState);

    useCollapsiblePanelWidthOpenState(() => {
        const newPanelMaxWidthState = generateInitialState(collapsiblePanelMaxWidth, panelsState);
        if (!_.isEqual(maxWidthState, newPanelMaxWidthState)) {
            setMaxWidthState(newPanelMaxWidthState)
        }
    }, panelsState)

    useEffect(() => {
        const onResize = _.debounce(() => {
            setCollapsiblePanelMaxWidth(calcPanelMaxWidth(panelMaxWidth, tabsPanelWidth))
        }, WINDOW_RESIZE_MAX_WIDTH_CALC_DEBOUNCE)

        window.addEventListener('resize', onResize, true);

        return () => {
            window.removeEventListener('resize', onResize, true)
        }
    }, [panelMaxWidth, tabsPanelWidth, setCollapsiblePanelMaxWidth]);

    return maxWidthState;
}
