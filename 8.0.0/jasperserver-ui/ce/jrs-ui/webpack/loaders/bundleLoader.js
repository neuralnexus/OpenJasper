const bundleNameRegex = /(.*)\.properties\.(js|ts)/g;

module.exports = function (content, map, meta) {
    const bundleFile = this.resourcePath.substr(this.context.length + 1);
    const bundleName = bundleFile.replace(bundleNameRegex, "$1");

    const moduleContent = `
        import {store, MERGED_BUNDLES_NAME} from 'bundleStore';
        
        let bundle;
        
        if ('${bundleName}' !== MERGED_BUNDLES_NAME) {
            store['${bundleName}'] = store['${bundleName}'] || {};
            bundle = store['${bundleName}'];
        } else {
            bundle = Object.keys(store).reduce((acc, key) => {
                return {
                    ...acc,
                    ...store[key]
                }
            }, {});
        }
                                     
        export default bundle;
    `;

    this.callback(null, moduleContent, null, meta);
};