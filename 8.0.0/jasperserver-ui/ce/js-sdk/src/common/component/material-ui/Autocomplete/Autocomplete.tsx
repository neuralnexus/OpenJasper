import React, { ReactElement, Ref } from 'react';
import MuiAutocomplete, { AutocompleteProps as MuiAutocompleteProps } from '@material-ui/lab/Autocomplete';
import { Paper } from '@material-ui/core';

function AutoCompleteFunc<
    T,
    Multiple extends boolean | undefined = undefined,
    DisableClearable extends boolean | undefined = undefined,
    FreeSolo extends boolean | undefined = undefined
    >(props: MuiAutocompleteProps< T, Multiple, DisableClearable, FreeSolo>, ref: Ref<HTMLDivElement>) {
    return (
        <MuiAutocomplete
            ref={ref}
            PaperComponent={({ children }) => (
                <Paper elevation={8}>{children}</Paper>
            )}
            {...props}
        />
    )
}
export const Autocomplete = React.forwardRef(AutoCompleteFunc) as
    <
        T,
        Multiple extends boolean | undefined = undefined,
        DisableClearable extends boolean | undefined = undefined,
        FreeSolo extends boolean | undefined = undefined
        >(props: MuiAutocompleteProps<T, Multiple, DisableClearable, FreeSolo> & React.RefAttributes<HTMLDivElement>) => ReactElement;
