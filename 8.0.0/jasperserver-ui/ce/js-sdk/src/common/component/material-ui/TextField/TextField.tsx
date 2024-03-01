import React, { forwardRef, HTMLAttributes, ReactNode } from 'react';
import {
    TextField as MuiTextField,
    TextFieldProps as MuiTextFieldProps,
    FormHelperText,
    FormHelperTextProps as MuiFormHelperTextProps
} from '@material-ui/core';
import {
    INLINE_CLASS,
    InputSize, InputWidth, SizeToClass, WidthToClass
} from '../types/InputTypes';

export type TextFieldProps = Omit<MuiTextFieldProps, 'size' | 'error'> & {
    size?: InputSize,
    width?: InputWidth,
    inline?: boolean,
    error?: string | boolean,
    errorId?: string,
    textFieldClassName?: string,
    FormErrorTextProps?: Partial<MuiFormHelperTextProps>,
    TextFieldPrefix?: ReactNode,
    TextFieldSuffix?: ReactNode,
    WrapperProps?: HTMLAttributes<HTMLDivElement> & {[key: string]: any}
}

export const TextField = forwardRef<HTMLDivElement, TextFieldProps>(({
    size = 'medium',
    width = 'normal',
    InputLabelProps = {},
    InputProps = {},
    FormHelperTextProps = {},
    FormErrorTextProps = {},
    SelectProps = {},
    className = '',
    textFieldClassName = '',
    select,
    inline,
    id,
    error,
    errorId,
    helperText,
    TextFieldPrefix,
    TextFieldSuffix,
    WrapperProps,
    ...rest
}, ref) => {
    const inlineClass = inline ? INLINE_CLASS : '';
    const inputSelectClass = select ? 'jr-mInputSelect' : '';
    const { classes: inputLabelPropsClasses = {}, ...InputLabelPropsRest } = InputLabelProps;
    const { classes: inputPropsClasses = {}, ...InputPropsRest } = InputProps;
    const { classes: selectPropsClasses = {}, ...SelectPropsRest } = SelectProps;
    const { classes: helperTextPropsClasses = {}, ...HelperTextPropsRest } = FormHelperTextProps;
    const { className: errorTextPropsClassName = '', ...ErrorTextPropsRest } = FormErrorTextProps;

    const hasError = Boolean(error);
    const hasErrorText = hasError && typeof error === 'string';
    const errorTextId = errorId ?? (hasError && id ? `${id}-error-text` : undefined);
    const helperTextId = helperText && id ? `${id}-helper-text` : undefined;

    const inputLabelProps = {
        classes: { root: `jr-mInput-label mui ${inputLabelPropsClasses?.root ?? ''}`, ...inputLabelPropsClasses },
        disableAnimation: true,
        ...InputLabelPropsRest
    };

    const inputAriaDescribedBy = [helperTextId, errorTextId].reduce((acc, textId) => {
        const accWithSpace = acc ? `${acc} ` : '';
        return textId ? `${accWithSpace}${textId}` : acc;
    }, '');

    const inputProps = select ? {} : {
        classes: { input: `jr-mInput-text mui ${inputPropsClasses?.input ?? ''}`, ...inputPropsClasses },
        ...(inputAriaDescribedBy ? { 'aria-describedby': inputAriaDescribedBy } : {}),
        'aria-invalid': hasError,
        ...InputPropsRest
    };

    const selectProps = {
        classes: { root: `jr-mInput-select mui ${selectPropsClasses?.root ?? ''}`, ...selectPropsClasses },
        ...(inputAriaDescribedBy ? { 'aria-describedby': inputAriaDescribedBy } : {}),
        ...SelectPropsRest
    };

    const helperTextProps = {
        classes: { root: `jr-mInput-helper mui ${helperTextPropsClasses?.root ?? ''}`, ...helperTextPropsClasses },
        ...HelperTextPropsRest
    };

    return (
        <div ref={ref} className={className} {...WrapperProps}>
            {TextFieldPrefix}
            <MuiTextField
                id={id}
                select={select}
                error={hasError}
                helperText={helperText}
                variant="outlined"
                className={`jr-mInput jr-mInputText mui ${inputSelectClass} ${SizeToClass[size]} ${inlineClass} ${WidthToClass[width]} ${textFieldClassName}`}
                InputLabelProps={inputLabelProps}
                InputProps={inputProps}
                SelectProps={selectProps}
                FormHelperTextProps={helperTextProps}
                {...rest}
            />
            {TextFieldSuffix}
            {hasErrorText && (
                <FormHelperText className={`jr-mInput-error mui ${errorTextPropsClassName}`} id={errorTextId} {...ErrorTextPropsRest}>
                    {error}
                </FormHelperText>
            )}
        </div>
    )
})
