import React, { forwardRef } from 'react';
import FormHelperText, { FormHelperTextProps as MuiFormHelperTextProps } from '@material-ui/core/FormHelperText';

export type FormHelperTextProps = Partial<MuiFormHelperTextProps> & {text?: string}

export const FormHelper = forwardRef<HTMLDivElement, FormHelperTextProps>((props, ref) => {
    const { className = '', text, ...rest } = props || {};
    return (
        <>
            {text && (
                <FormHelperText
                    ref={ref}
                    className={`jr-mInput-helper mui ${className}`}
                    {...rest}
                >
                    {text}
                </FormHelperText>
            )}
        </>
    )
});
