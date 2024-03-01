define(function() {

    var logLevels = {
        DEBUG: "debug"
    };

    var Log = function(id, logLevel) {
        this.id = id;
        this.logLevel = logLevel;
    };

    Log.prototype.debug = function(msg) {
        if (this.logLevel === logLevels.DEBUG) {
            console.log(this.logLevel + ": " + this.id + ". " + msg);
        }
    };

    return {
        loggers: {},
        register: function(id, logLevel) {
            var log;

            if (this.loggers[id]) {
                log = this.loggers[id];
            } else {
                log = new Log(id, logLevel);

                this.loggers[id] = log;
            }

            return log;
        }
    }
});