import InputControlTypeEnum from '../enum/inputControlTypeEnum';

interface InputControlsMetadata {
    inputControl: {
        uri: string,
        id: string,
        type: InputControlTypeEnum,
        slaveDependencies?: string[],
        masterDependencies?: string[]
    }[]
}

export default {
    metadataToViewModelConverter(metadata: InputControlsMetadata) {
        return metadata.inputControl.map((control) => ({
            ...control,
            uri: control.uri.replace('repo:', '')
        }));
    }
};
