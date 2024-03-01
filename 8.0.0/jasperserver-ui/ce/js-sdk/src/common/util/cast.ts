export const cast = <R, T = any>(a: T): R => {
    return (a as unknown) as R
}
