import React, { Component, ReactElement } from 'react';
import { mount as enzymeMount, MountRendererProps } from 'enzyme';

import { StylesProvider } from '../../src/common/component/material-ui/styles/StylesProvider';

export function materialUIMount<C extends Component, P = C['props'], S = C['state']>(node: ReactElement<P>, options?: MountRendererProps) {
    const wrappedNode = (
        <StylesProvider>
            {node}
        </StylesProvider>
    )

    return enzymeMount<C, P, S>(wrappedNode, options);
}
