const customMatcher = {
    toEqualSnapshot: () => {
        return {
            compare: (actual:string, expected:string) => {
                return {
                    pass: (actual.replace(/[\r\n]+/gm, '\n') === expected.replace(/[\r\n]+/gm, '\n')),
                    message: `Expect  \n${actual}\n===== to equal =====\n${expected}`
                }
            }
        };
    }
};

export default customMatcher;
