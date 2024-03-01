declare module 'js-sdk/src/jrs.config';

interface JRSConfigItem {
    [key: string] : string
}

interface JRSConfigs {
    userLocale: string,
    userTimezone: string,
    [key: string]: string | JRSConfigItem
}
declare const jrsConfigs: JRSConfigs;
export default jrsConfigs;
