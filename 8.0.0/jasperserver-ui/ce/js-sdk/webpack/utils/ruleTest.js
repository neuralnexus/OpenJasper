module.exports = function (match) {
    return function (path) {
        return path.replace(/\\/g, '/').indexOf(match) >= 0;
    }
}