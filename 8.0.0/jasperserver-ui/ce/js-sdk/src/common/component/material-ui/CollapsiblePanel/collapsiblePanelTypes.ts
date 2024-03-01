import { ReactNode } from 'react';

export interface SubPanelSlots {
    headerAction?: ReactNode
}

export interface SubPanelRenderProps {
    onClose: () => void,
    onScroll: (scrollPos: number) => void,
    label: string,
    id?: string,
    scrollPos: number
}

export interface SubPanelDefinition {
    label: string,
    id: string,
    height?: number,
    minHeight?: number,
    open?: boolean,
    scrollPos?: number,
    slots?: SubPanelSlots
    content?: ReactNode,
    render?: (props: SubPanelRenderProps) => ReactNode
}

export interface PanelDefinition {
    width?: number,
    minWidth?: number,
    id: string
    subPanels: SubPanelDefinition[]
}

export type SubPanelOpenStateDefinition = Required<Pick<SubPanelDefinition, 'id' | 'open'>>

export interface PanelOpenStateDefinition extends Pick<PanelDefinition, 'id'> {
    subPanels: SubPanelOpenStateDefinition[]
}

export type SubPanelHeightStateDefinition = Required<Pick<SubPanelDefinition, 'id' | 'height'>>

export interface PanelSizeStateDefinition extends Pick<PanelDefinition, 'id' | 'width'> {
    subPanels: SubPanelHeightStateDefinition[]
}

export type Anchor = 'left' | 'right';

export type PanelOrSubPanelType = 'panel' | 'subpanel';

export interface PanelOrSubPanelActionProps {
    type: PanelOrSubPanelType,
    id: string
}

export interface OnResizeCallbackProps extends PanelOrSubPanelActionProps {
    delta: number
}

export interface OnScrollCallbackProps {
    id: string,
    scrollPos: number
}

export type OnScrollCallback = (props: OnScrollCallbackProps) => void

export type OnResizeCallback = (props: OnResizeCallbackProps) => void

export type OnResizeStartCallback = (props: PanelOrSubPanelActionProps) => void

export type OnResizeStopCallback = (state: PanelSizeStateDefinition[]) => void

export type OnExpansionStateChangedCallback = (state: PanelOpenStateDefinition[]) => void

export type OnExpandCollapseClick = (props: PanelOrSubPanelActionProps) => boolean | undefined | void

export type MaxWidth = number | (() => number)

export const DIRECTION_CLASS: {[key in Anchor]: {panelDirectionClass: string, panelsDirectionClass: string}} = {
    right: {
        panelDirectionClass: 'jr-mPanelRight',
        panelsDirectionClass: 'jr-mPanelsRight'
    },
    left: {
        panelDirectionClass: 'jr-mPanelLeft',
        panelsDirectionClass: 'jr-mPanelsLeft'
    }
}
