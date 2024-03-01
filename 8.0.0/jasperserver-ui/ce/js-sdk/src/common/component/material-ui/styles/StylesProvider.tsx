import React, { FC, ComponentType } from 'react';
import {
    StylesProvider as MuiStylesProvider, ThemeProvider as MuiThemeProvider, createMuiTheme
} from '@material-ui/core/styles';
import { ThemeProviderProps } from '@material-ui/styles/ThemeProvider/ThemeProvider';
import { DefaultTheme } from '@material-ui/styles/defaultTheme';
import { GenerateId } from 'jss';
import { StylesProviderProps } from '@material-ui/styles/StylesProvider/StylesProvider';
import createGenerateClassName from './generateClassName/createGenerateClassName';

export const GenerateClassNameOptions = {
    seed: 'jr'
};

const generateClassName = createGenerateClassName(GenerateClassNameOptions);

export const CreateMuiThemeOptions = {};

const muiTheme = createMuiTheme(CreateMuiThemeOptions);

export function createStylesProvider<Theme = DefaultTheme>(
    classNameGenerator: GenerateId,
    theme: ThemeProviderProps<Theme>['theme'],
    StylesProvider: ComponentType<StylesProviderProps> = MuiStylesProvider,
    ThemeProvider: ComponentType<ThemeProviderProps<Theme>> = MuiThemeProvider
): FC {
    return ({ children }) => (
        <StylesProvider generateClassName={classNameGenerator} injectFirst>
            <ThemeProvider theme={theme}>
                {children}
            </ThemeProvider>
        </StylesProvider>
    )
}

export const StylesProvider = createStylesProvider(generateClassName, muiTheme);
