import React, { forwardRef } from 'react';
import FormHelperText, { FormHelperTextProps as MuiFormHelperTextProps } from '@material-ui/core/FormHelperText';

export type FormErrorTextProps = Partial<MuiFormHelperTextProps> & {text?: string}

export const FormError = forwardRef<HTMLDivElement, FormErrorTextProps>((props, ref) => {
    const { className = '', text, ...rest } = props || {};
    return (
        <>
            {text && (
                <FormHelperText
                    ref={ref}
                    className={`jr-mInput-error mui ${className}`}
                    {...rest}
                >
                    {text}
                </FormHelperText>
            )}
        </>
    )
});
