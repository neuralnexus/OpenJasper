// @ts-ignore
import XRegExp from 'xregexp';

const hexColorChecker = XRegExp('^#([a-fA-F0-9]{3}|[a-fA-F0-9]{6})$');
const rgbaColorChecker = XRegExp('^rgba\\(\\s*([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\s*,\\s*([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\s*,\\s*([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\s*,\\s*((1)|(1\\.0+)|(0)|(0\\.[0-9]+))\\s*\\)$');
const rgbColorChecker = XRegExp('^rgb\\(\\s*([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\s*,\\s*([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\s*,\\s*([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\s*\\)$');

export const isValidColorFn = (color: string) => {
    return hexColorChecker.test(color) || rgbaColorChecker.test(color) || rgbColorChecker.test(color);
}
