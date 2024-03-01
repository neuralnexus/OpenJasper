declare module 'js-sdk/src/common/util/i18nMessage';

export interface I18nMessageFunc {
    (messageKey: string, ...params: string[]): string
}

interface I18nMessage {
    create: (bundle: {[key: string]: string}) => I18nMessageFunc
}

declare const i18nMessage: I18nMessage;
export default i18nMessage;
