import React, { FC } from 'react';
import { List, ListItem } from '@material-ui/core';
import { Resizable } from 're-resizable';
import {
    PanelState, SetSubPanelState, getLastOpenSubPanelIndex, SubPanelState
} from './useCollapsiblePanelState';
import {
    OnExpandCollapseClick,
    OnResizeCallback, OnResizeStartCallback, OnScrollCallback, PanelDefinition, SubPanelDefinition
} from './collapsiblePanelTypes';
import { SubPanel } from './SubPanel';

export interface SubPanelsListProps {
    panelIndex: number,
    panelState: PanelState,
    panel: PanelDefinition,
    setSubPanelState: SetSubPanelState,
    onResizeStart: OnResizeStartCallback,
    onResize: OnResizeCallback
    onScroll: OnScrollCallback,
    onCollapseClick: OnExpandCollapseClick
}

interface RenderSubPanelProps {
    subPanelDefinition: SubPanelDefinition,
    subPanelState: SubPanelState,
    setSubPanelState: SetSubPanelState,
    panelIndex: number,
    subPanelIndex: number,
    onScroll: OnScrollCallback,
    onCollapseClick: OnExpandCollapseClick
}

const renderSubPanel = (props: RenderSubPanelProps) => {
    const {
        subPanelDefinition,
        subPanelState,
        setSubPanelState,
        panelIndex,
        subPanelIndex,
        onScroll,
        onCollapseClick
    } = props;

    const {
        content, render, label, id, slots
    } = subPanelDefinition;

    const { initialHeight, scrollPos } = subPanelState;

    const handleClose = () => {
        const result = onCollapseClick({ type: 'subpanel', id })
        if (typeof result === 'undefined' || result) {
            setSubPanelState(panelIndex, subPanelIndex, false, initialHeight);
        }
    };

    const handleScroll = (newScrollPos: number) => {
        onScroll({ id, scrollPos: newScrollPos });
        setSubPanelState(panelIndex, subPanelIndex, undefined, undefined, newScrollPos);
    };

    if (render) {
        return render({
            onScroll: handleScroll,
            onClose: handleClose,
            label,
            id,
            scrollPos
        })
    }

    return (
        <SubPanel
            label={label}
            slots={slots}
            onClose={handleClose}
            onScroll={handleScroll}
            scrollPos={scrollPos}
            id={id}
        >
            {content}
        </SubPanel>
    )
}

export const SubPanelsList: FC<SubPanelsListProps> = ({
    panelState, panel, panelIndex, setSubPanelState, onResize, onResizeStart, onScroll, onCollapseClick
}) => {
    const lastOpenSubPanelIndex = getLastOpenSubPanelIndex(panelState);

    return (
        <List disablePadding className="jr-mPanel-sections mui" component="div" style={{ overflow: 'hidden' }}>
            {
                panel.subPanels.map((subPanelItem, subPanelIndex) => {
                    const subPanelState = panelState.subPanels[subPanelIndex];
                    const isLastVisibleSubPanel = subPanelIndex === lastOpenSubPanelIndex;
                    const listItemStyle = isLastVisibleSubPanel ? {} : { height: '100%' };

                    if (!subPanelState.open) {
                        return undefined
                    }

                    const listItem = (
                        <ListItem
                            key={subPanelState.id}
                            data-name={subPanelItem.id}
                            disableGutters
                            className="jr-mPanel-section mui"
                            style={listItemStyle}
                            component="div"
                        >
                            {renderSubPanel({
                                onScroll,
                                subPanelDefinition: subPanelItem,
                                subPanelState,
                                setSubPanelState,
                                panelIndex,
                                subPanelIndex,
                                onCollapseClick
                            })}
                        </ListItem>
                    );

                    if (isLastVisibleSubPanel) {
                        return listItem
                    }

                    return (
                        <Resizable
                            enable={{
                                bottom: true
                            }}
                            handleClasses={{ bottom: 'jr-mPanel-resizerVertical mui' }}
                            size={{
                                width: '100%',
                                height: subPanelState.height,
                            }}
                            minHeight={subPanelState.minHeight}
                            style={{
                                marginBottom: '8px',
                                width: '100%'
                            }}
                            key={subPanelState.id}
                            onResize={(event, direction, elementRef, delta) => onResize({
                                type: 'subpanel',
                                id: subPanelState.id,
                                delta: delta.height
                            })}
                            onResizeStart={() => onResizeStart({
                                type: 'subpanel',
                                id: subPanelState.id
                            })}
                            onResizeStop={(event, direction, elementRef, delta) => {
                                setSubPanelState(panelIndex, subPanelIndex, undefined, subPanelState.height + delta.height);
                            }}
                        >
                            {listItem}
                        </Resizable>
                    )
                })
            }
        </List>
    )
}
