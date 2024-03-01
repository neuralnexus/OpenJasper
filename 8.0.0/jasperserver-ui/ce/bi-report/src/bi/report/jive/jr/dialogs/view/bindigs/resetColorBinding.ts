/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

export default function getBinding() {
    return {
        el: {} as HTMLElement,
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        onClick: (e: Event) => {},

        init($element: JQuery, value: string, bindings: {
            [propName: string]: (value: null) => void;
        }) {
            const modelBinding = $element.data('model-attr');

            this.el = $element.get(0);

            $element.attr('disabled', 'disabled');

            this.onClick = (event: Event) => {
                event.stopPropagation();

                bindings[modelBinding](null);
                $element.attr('disabled', 'disabled');
            };

            this.el.addEventListener('click', this.onClick);
        },

        set($element: JQuery, value: string) {
            if (value) {
                $element.removeAttr('disabled');
                $element.prop('checked', true);
            }
        },

        clean() {
            this.el.removeEventListener('click', this.onClick);
        },
    };
}
