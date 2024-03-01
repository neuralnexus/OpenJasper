import {
    useCallback, useEffect, useMemo, useState
} from 'react';
import {
    PanelDefinition,
    SubPanelDefinition,
    PanelSizeStateDefinition
} from './collapsiblePanelTypes';

const DEFAULT_PANEL_WIDTH = 270;
const MIN_PANEL_WIDTH = 270;
const DEFAULT_PANEL_HEIGHT = 100;
const MIN_PANEL_HEIGHT = 50;
export const PANEL_MARGIN_RIGHT = 10;

export interface SubPanelState {
    id: string,
    open: boolean,
    initialHeight: number,
    height: number,
    scrollPos: number,
    minHeight: number
}

export interface PanelState {
    id: string,
    width: number,
    minWidth: number,
    subPanels: SubPanelState[]
}

export type PanelsState = PanelState[];

export const isPanelExpanded = (panelIndex: number, state: PanelsState) => {
    return state[panelIndex].subPanels.some((subPanel) => subPanel.open);
}

export const isPanelFullyExpanded = (panelIndex: number, state: PanelsState) => {
    return !state[panelIndex].subPanels.some((subPanel) => !subPanel.open);
}

export const getLastOpenSubPanelIndex = (state: PanelState) => {
    return state.subPanels.map((subPanel) => subPanel.open).lastIndexOf(true)
}

export const getPanelSizeState = (state: PanelsState): PanelSizeStateDefinition[] => {
    return state.map((panel) => {
        return {
            id: panel.id,
            width: panel.width,
            subPanels: panel.subPanels.map((subPanel) => ({
                id: subPanel.id,
                height: subPanel.height
            }))
        };
    });
};

function replaceArrayItem<T>(array: T[], element: T, index: number): T[] {
    return [...array.slice(0, index), element, ...array.slice(index + 1)]
}

const generateSubPanelExpansionState = (state: PanelsState, panelIndex: number, subPanelsExpansionState: boolean[]) => {
    const panelState = state[panelIndex];
    return subPanelsExpansionState.reduce((acc, open, subPanelIndex) => {
        const subPanelState = panelState.subPanels[subPanelIndex];

        const newSubPanel = {
            ...subPanelState,
            ...(open ? {} : { height: subPanelState.initialHeight }),
            open
        };

        return [...acc, newSubPanel];
    }, [] as SubPanelState[])
}

const getNewPanelState = (state: PanelsState, panelIndex: number, open?: boolean, width?: number) => {
    const panelState = state[panelIndex];
    let { subPanels } = panelState;
    if (typeof open === 'boolean') {
        subPanels = generateSubPanelExpansionState(state, panelIndex, subPanels.map(() => open))
    }

    const newPanelState: PanelState = {
        ...panelState,
        width: width ?? panelState.width,
        subPanels
    };

    return replaceArrayItem(state, newPanelState, panelIndex);
}

const getNewSubPanelState = (state: PanelsState, panelIndex: number, subPanelIndex: number, open?: boolean, height?: number, scrollPos?: number) => {
    const { subPanels } = state[panelIndex];
    const subPanelState = subPanels[subPanelIndex];
    const newSubPanelState: SubPanelState = {
        ...subPanelState,
        open: open ?? subPanelState.open,
        height: height ?? subPanelState.height,
        scrollPos: scrollPos ?? subPanelState.scrollPos
    };

    const newPanelState = {
        ...state[panelIndex],
        subPanels: replaceArrayItem(subPanels, newSubPanelState, subPanelIndex)
    };

    return replaceArrayItem(state, newPanelState, panelIndex);
}

const generateInitialSubPanelsState = (subPanels: SubPanelDefinition[]): SubPanelState[] => {
    return subPanels.reduce((accumulator, subPanelDefinition) => {
        const {
            height = DEFAULT_PANEL_HEIGHT, id, open = false, minHeight = MIN_PANEL_HEIGHT, scrollPos = 0
        } = subPanelDefinition;

        const effectiveMinHeight = Math.max(minHeight, MIN_PANEL_HEIGHT);
        const effectiveHeight = Math.max(height, effectiveMinHeight);
        const newSubPanelState: SubPanelState = {
            id,
            initialHeight: effectiveHeight,
            height: effectiveHeight,
            minHeight: effectiveMinHeight,
            scrollPos,
            open
        }
        return [...accumulator, newSubPanelState];
    }, [] as SubPanelState[])
}

const generateInitialPanelsState = (panels: PanelDefinition[]): PanelsState => {
    return panels.reduce((accumulator, panelDefinition) => {
        const {
            width = DEFAULT_PANEL_WIDTH, id, minWidth = MIN_PANEL_WIDTH, subPanels
        } = panelDefinition;

        const effectiveMinWidth = Math.max(MIN_PANEL_WIDTH, minWidth);
        const effectiveWidth = Math.max(width, effectiveMinWidth);
        const newPanelState: PanelState = {
            id,
            width: effectiveWidth,
            minWidth: effectiveMinWidth,
            subPanels: generateInitialSubPanelsState(subPanels)
        }
        return [...accumulator, newPanelState];
    }, [] as PanelsState)
}

export type SetPanelState = (panelIndex: number, open?: boolean, width?: number) => void
export type SetSubPanelState = (panelIndex: number, subPanelIndex: number, open?: boolean, height?: number, scrollPos?: number) => void
type UseCollapsiblePanelState = [
    PanelsState,
    SetPanelState,
    SetSubPanelState
];

export const useCollapsiblePanelState = (panels: PanelDefinition[]): UseCollapsiblePanelState => {
    const initialPanelsState = useMemo(() => generateInitialPanelsState(panels), [panels]);

    const [panelsState, setPanelsState] = useState(initialPanelsState);

    useEffect(() => {
        setPanelsState(initialPanelsState)
    }, [initialPanelsState, setPanelsState]);

    const setPanelState = useCallback((panelIndex: number, open?: boolean, width?: number) => {
        setPanelsState(getNewPanelState(panelsState, panelIndex, open, width))
    }, [setPanelsState, panelsState])

    const setSubPanelState = useCallback((panelIndex: number, subPanelIndex: number, open?: boolean, height?: number, scrollPos?: number) => {
        setPanelsState(getNewSubPanelState(panelsState, panelIndex, subPanelIndex, open, height, scrollPos))
    }, [setPanelsState, panelsState])

    return [panelsState, setPanelState, setSubPanelState];
}
