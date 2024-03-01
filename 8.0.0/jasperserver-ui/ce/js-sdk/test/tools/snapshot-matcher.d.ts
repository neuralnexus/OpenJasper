declare namespace jasmine {
    interface Matchers<T = any> {
        toEqualSnapshot(expected: T): T;
    }
}
