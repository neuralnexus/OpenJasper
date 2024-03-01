export type IconSize = 'small' | 'medium' | 'large';

export const SizeToClass: {[key in IconSize]: string} = {
    small: 'jr-mIconSmall',
    medium: '',
    large: 'jr-mIconLarge'
}
