import React, {
    ComponentType, FC, PropsWithChildren, ReactNode, useEffect
} from 'react';

export const JSON_FUNCTION_PLACEHOLDER = '_func_';
export const REACT_PLACEHOLDER = '_REACT_';

interface SerializedProps {
    json: any,
    reactElements: {[key: string]: ReactNode}
}

const serialize = (props: any, parentKeys: string[]): SerializedProps => {
    if (Array.isArray(props)) {
        return props.reduce((acc, element, index) => {
            const { json, reactElements } = serialize(element, parentKeys.concat(`[${index}]`));
            return {
                json: typeof json !== 'undefined' ? acc.json.concat(json) : acc.json,
                reactElements: {
                    ...acc.reactElements,
                    ...reactElements
                }
            };
        }, {
            json: [],
            reactElements: {}
        } as SerializedProps)
    }

    if (props instanceof HTMLElement) {
        return {
            json: props.innerHTML,
            reactElements: {}
        }
    }

    if (React.isValidElement(props)) {
        return {
            json: REACT_PLACEHOLDER,
            reactElements: {
                [parentKeys.join('')]: props
            }
        }
    }

    if (typeof props === 'object') {
        return Object.keys(props).reduce((acc, key) => {
            const value = props[key];
            const { json, reactElements } = serialize(value, parentKeys.concat(parentKeys.length > 0 ? `.${key}` : key));
            return {
                json: {
                    ...acc.json,
                    ...(typeof json !== 'undefined' ? { [key]: json } : {})
                },
                reactElements: {
                    ...acc.reactElements,
                    ...reactElements
                }
            };
        }, {
            json: {},
            reactElements: {}
        } as SerializedProps)
    }

    if (typeof props === 'function') {
        return {
            json: JSON_FUNCTION_PLACEHOLDER,
            reactElements: {}
        }
    }

    return {
        json: props,
        reactElements: {}
    } as SerializedProps
}

const serializeProps = (props: any) => {
    return Object.keys(props).reduce((acc, key) => {
        if (key !== 'children') {
            const { json, reactElements } = serialize(props[key], [key]);
            acc.json[key] = json;
            acc.reactElements = {
                ...acc.reactElements,
                ...reactElements
            }
        }

        return acc;
    }, { json: {}, reactElements: {} } as SerializedProps);
}

export const DefaultPropsSerializer = (props: any) => {
    const { json, reactElements } = serializeProps(props);
    const hasReactElements = Object.keys(reactElements).length > 0;

    return (
        <>
            <div data-name="props">{JSON.stringify(json)}</div>
            {hasReactElements && (
                <div data-name="reactNodeProps">
                    {Object.keys(reactElements).map((key) => {
                        return <div data-id={key} key={key}>{reactElements[key]}</div>
                    })}
                </div>
            )}
        </>
    );
};

export interface WrapperProps {
    id: string,
    props: any,
    propsSerializer?: ComponentType<any>
}

export const DefaultWrapper: FC<WrapperProps> = (p) => {
    const {
        id, props, propsSerializer = DefaultPropsSerializer, children
    } = p;

    const PropsSerializer = propsSerializer;

    return (
        <div data-id={id}>
            <PropsSerializer {...props} />
            {children}
        </div>
    )
}

interface FakeComponentOptions<T> {
    Wrapper?: ComponentType<WrapperProps>,
    // propsConsumer is executed before component mounted
    // this is handy because you do not have to use async tests
    propsConsumer?: (props: T, id: string) => void
    // executed after component mounted into DOM
    // can be used to test some props which are callbacks
    // which should be executed after component mounted
    componentDidMount?: (props: T, id: string) => void
}

export function createFakeComponent <T = any>(name: string|(() => string), options: FakeComponentOptions<T> = {}): ComponentType<T> {
    return (props: PropsWithChildren<T>) => {
        const componentId = typeof name === 'function' ? name() : name;
        const { Wrapper = DefaultWrapper, propsConsumer = () => {}, componentDidMount = () => {} } = options;

        propsConsumer({ ...props }, componentId)
        useEffect(() => {
            componentDidMount({ ...props }, componentId)
        }, [props, componentId, componentDidMount])

        return (
            <Wrapper id={componentId} props={props}>
                {props.children}
            </Wrapper>
        );
    };
}
