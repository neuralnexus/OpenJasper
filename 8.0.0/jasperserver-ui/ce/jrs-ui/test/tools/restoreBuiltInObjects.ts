const arrayFrom = Array.from;
let envArrayFrom: ArrayConstructor['from'];

export default {
    enableNativeImplementation() {
        envArrayFrom = Array.from;

        Array.from = arrayFrom;
    },

    restoreEnvironment() {
        Array.from = envArrayFrom;
    },
};
