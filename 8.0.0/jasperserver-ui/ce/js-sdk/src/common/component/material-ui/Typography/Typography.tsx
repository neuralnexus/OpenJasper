import React, { forwardRef } from 'react';
import { Typography as MuiTypography, TypographyProps as MuiTypographyProps } from '@material-ui/core';

export type TypographyProps = Omit<MuiTypographyProps, 'variant'> & {
    variant?: MuiTypographyProps['variant'] | 'body3' | 'body4'
}

const getVariantAndClassName = (variant: TypographyProps['variant']): [MuiTypographyProps['variant'], string] => {
    switch (variant) {
    case 'body1':
        return ['body1', 'jr-mText mui']
    case 'body2':
        return ['body2', 'jr-mText jr-mTextSmall mui']
    case 'body3':
        return [undefined, 'jr-mText jr-mTextLarge mui']
    case 'body4':
        return [undefined, 'jr-mText jr-mTextXLarge mui']
    default:
        return [variant, 'mui']
    }
}

export const Typography = forwardRef<HTMLElement, TypographyProps>(({ variant, className = '', ...rest }, ref) => {
    const [effectiveVariant, variantClassName] = getVariantAndClassName(variant);

    return (
        <MuiTypography ref={ref} className={`${variantClassName} ${className}`} variant={effectiveVariant} {...rest} />
    )
})
