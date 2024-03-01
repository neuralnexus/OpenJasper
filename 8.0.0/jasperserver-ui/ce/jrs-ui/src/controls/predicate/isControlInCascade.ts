export default (
    control: {
       // masterDependencies?: string[],
        slaveDependencies?: string[],
       // selection?: any
    }
) => {
    const {
        // masterDependencies,
        slaveDependencies,
        // selection
    } = control;

    // const hasMasterDependencies = masterDependencies ? masterDependencies.length > 0 : false;
    const hasSlaveDependencies = slaveDependencies ? slaveDependencies.length > 0 : false;

    // return hasMasterDependencies || hasSlaveDependencies;
    return hasSlaveDependencies;
};
