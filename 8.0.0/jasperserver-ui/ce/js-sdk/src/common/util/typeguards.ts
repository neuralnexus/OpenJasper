export const hasProperty = <T, Prop extends string>(obj: T, prop: Prop): obj is T & Record<Prop, unknown> => Object.prototype.hasOwnProperty.call(obj, prop)
