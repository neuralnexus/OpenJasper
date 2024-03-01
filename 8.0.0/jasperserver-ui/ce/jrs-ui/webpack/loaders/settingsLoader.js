const settingsNameRegex = /(.*)\.settings\.(js|ts)/g;

module.exports = function (content, map, meta) {
    const settingsFile = this.resourcePath.substr(this.context.length + 1);
    const settingsName = settingsFile.replace(settingsNameRegex, "$1");

    const moduleContent = `
        import settingsStore from 'settingsStore';
        settingsStore['${settingsName}'] = settingsStore['${settingsName}'] || {};
        export default settingsStore['${settingsName}'];
    `;

    this.callback(null, moduleContent, null, meta);
};