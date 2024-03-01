import nested from '@material-ui/styles/ThemeProvider';

const pseudoClasses = [
    'checked',
    'disabled',
    'error',
    'focused',
    'focusVisible',
    'required',
    'expanded',
    'selected',
];

export default function createGenerateClassName(options = {}) {
    const {disableGlobal = false, seed = ''} = options;
    const seedPrefix = seed === '' ? '' : `${seed}-`;
    let ruleCounter = 1;

    const getNextCounterId = () => {
        return ruleCounter;
    };

    return (rule, styleSheet) => {
        const name = styleSheet.options.name;

        // Is a global static MUI style?
        if (name && name.indexOf('Mui') === 0 && !styleSheet.options.link && !disableGlobal) {
            // We can use a shorthand class name, we never use the keys to style the components.
            if (pseudoClasses.indexOf(rule.key) !== -1) {
                return `${seedPrefix}Mui-${rule.key}`;
            }

            const prefix = `${seedPrefix}${name}-${rule.key}`;

            if (!styleSheet.options.theme[nested] || seed !== '') {
                return prefix;
            }

            return `${prefix}-${getNextCounterId()}`;
        }

        // if (process.env.NODE_ENV === 'production') {
        //   return `${seedPrefix}${productionPrefix}${getNextCounterId()}`;
        // }

        const suffix = `${rule.key}-${getNextCounterId()}`;

        // Help with debuggability.
        if (styleSheet.options.classNamePrefix) {
            return `${seedPrefix}${styleSheet.options.classNamePrefix}-${suffix}`;
        }

        return `${seedPrefix}${suffix}`;
    };
}
