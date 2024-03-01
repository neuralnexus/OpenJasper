import { ButtonProps, PropTypes } from '@material-ui/core';

export const SizeToClass: {[key in Required<ButtonProps>['size']]: string} = {
    large: 'jr-mButtonLarge',
    small: 'jr-mButtonSmall',
    medium: ''
}

export type ButtonColor = PropTypes.Color | 'error' | 'warning';

export const ColorToClass: {[key in ButtonColor]: string} = {
    inherit: '',
    default: '',
    secondary: 'jr-mButtonSecondary',
    primary: 'jr-mButtonPrimary',
    error: 'jr-mButtonError',
    warning: 'jr-mButtonWarning'
}

export const VariantToClassName: {[key in Required<ButtonProps>['variant']]: string} = {
    contained: 'jr-MuiButton-contained',
    text: 'jr-MuiButton-text',
    outlined: 'jr-MuiButton-outlined'
}
