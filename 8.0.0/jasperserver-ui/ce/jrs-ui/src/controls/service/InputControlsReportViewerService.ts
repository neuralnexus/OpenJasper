import $ from 'jquery';
import _ from 'underscore';

import {
    PaginatedValuesOptions, PaginatedValuesResponse,
    SelectedValuesResponse, SelectedOnlyResponse, SelectedOnlyOptions
} from '../rest/types/InputControlsServiceType';
import inputControlsToViewModelConverter from '../converter/inputControlsToViewModelConverter';
import InputControlTypeEnum from '../enum/inputControlTypeEnum';
import getControlPaginationOptionsByControlIdAndType from '../converter/getControlPaginationOptionsByControlIdAndType';
import RestParamsEnum from '../rest/enum/restParamsEnum';
import isQueryControl from '../predicate/isQueryControl';
import getControlsPaginationOptionsByControlId from '../converter/getControlsPaginationOptionsByControlId';
import { InputControlsServiceWithCacheInterface } from '../rest/InputControlsServiceWithCache';

interface ControlStructure {
    id: string,
    type: InputControlTypeEnum,
    slaveDependencies?: string[],
    masterDependencies?: string[]
}

interface Options {
    inputControlsService: InputControlsServiceWithCacheInterface
}

const getPaginatedValuesOptionsForQueryControls = (
    structure: ControlStructure[],
    controlsMap: {
        [key: string]: ControlStructure
    },
    mapper: (option: PaginatedValuesOptions) => PaginatedValuesOptions,
    callback?: (option: PaginatedValuesOptions[]) => void
) => {
    return structure.reduce((memo, control) => {
        if (isQueryControl(control)) {
            const newMemo: {
                [key: string]: PaginatedValuesOptions[]
            } = {
                ...memo,
                [control.id]: getControlsPaginationOptionsByControlId({
                    controlId: control.id,
                    controls: controlsMap
                }).map(mapper)
            };

            if (typeof callback !== 'undefined') {
                callback(newMemo[control.id]);
            }

            return newMemo;
        }

        return memo;
    }, {});
};

const getControlsPaginationOptionsByControlIdOnSelectionChange = (options: {
    controlId: string,
    value: string[],
    selection: {
        [key: string]: string[]
    },
    controls: {
        [key: string]: ControlStructure
    }
}) => {
    const {
        controlId,
        value,
        controls,
        selection
    } = options;

    const controlsWithValueOrDefaultSelection = _.reduce(controls, (controlMemo, control) => {
        let additionalOptions = {};

        if (control.id === controlId) {
            additionalOptions = {
                value: value.length > 0 ? value : [RestParamsEnum.NOTHING_SUBSTITUTION_VALUE]
            }
        } else {
            additionalOptions = {
                value: selection[control.id],
                select: RestParamsEnum.SELECTED_VALUES
            }
        }

        return {
            ...controlMemo,
            [control.id]: {
                masterDependencies: control.masterDependencies || [],
                slaveDependencies: control.slaveDependencies || [],
                type: control.type,
                ...additionalOptions
            }
        }
    }, {});

    return getControlsPaginationOptionsByControlId({
        controlId,
        controls: controlsWithValueOrDefaultSelection
    });
};

const fetchRequestParamsThroughURL = (structure: ControlStructure[], allRequestParameters: any) => {
    const fetchLabelOfControls: Array<string> = _.pluck(structure, 'id');
    const fetchKeysOfParams: Array<string> = _.keys(allRequestParameters);
    const requestedSelectionParams: any = {};
    const getControlIdInRequestParams: Array<string> = _.intersection(fetchLabelOfControls, fetchKeysOfParams);

    if (getControlIdInRequestParams.length > 0) {
        getControlIdInRequestParams.forEach((val) => {
            requestedSelectionParams[val] = allRequestParameters[val]
        });
    }
    return requestedSelectionParams
};

export default class InputControlsReportViewerService {
    private readonly inputControlsService: InputControlsServiceWithCacheInterface;

    constructor(options: Options) {
        this.inputControlsService = options.inputControlsService;
    }

    fetchInputControlsInitialState(uri: string, allRequestParameters: { [key: string]: string[] }) {
        // flow when user is setting values through uri.
        return this.inputControlsService.getInputControlsMetadata(uri).then((response) => {
            if (response) {
                const structure = inputControlsToViewModelConverter.metadataToViewModelConverter(response);
                return this.fetchInitialInputControlsValuesByUriAndStructure(uri, structure, allRequestParameters);
            }
            return $.Deferred().resolve(response);
        });
    }

    fetchInitialInputControlsValuesByUri(uri: string, structure: ControlStructure[], preSelectedData: { [key: string]: string[] }) {
        return this.fetchInitialInputControlsValuesByUriAndStructure(uri, structure, preSelectedData);
    }

    fetchInputControlsOptionsBySelectionAndUri(
        selection: {
            [key: string]: string[]
        },
        uri: string,
        structure: ControlStructure[]
    ) {
        const controlsMap: {
            [key: string]: ControlStructure
        } = structure.reduce((structureMemo, control) => {
            return {
                ...structureMemo,
                [control.id]: control
            };
        }, {});

        const paginationOptions = _.map(selection, (value, controlId) => {
            return getControlPaginationOptionsByControlIdAndType(
                controlId,
                controlsMap[controlId].type,
                {
                    value: value && value.length === 0 ? [RestParamsEnum.NOTHING_SUBSTITUTION_VALUE] : selection[controlId] || []
                }
            );
        });

        const paginationOptionsPerControl = getPaginatedValuesOptionsForQueryControls(
            structure,
            controlsMap,
            (option) => {
                return {
                    ...option,
                    value: selection[option.name] || []
                }
            }
        );

        return this.fetchInputControlsOptionsByPaginatedValuesOptionsAndUri({
            paginationOptions,
            paginationOptionsPerControl,
            uri
        }).then((response) => {
            return $.Deferred().resolve(response, paginationOptions, paginationOptionsPerControl);
        });
    }

    fetchInputControlsValuesOnControlSelectionChange(options: {
        controlId: string,
        value: string[],
        uri: string,
        structure: ControlStructure[],
        selection: {
            [key: string]: string[]
        },
        initialSelectedValues: {
            [key: string]: { value: string }[]
        }
    }) {
        const {
            controlId,
            value,
            uri,
            structure,
            selection
        } = options;

        const controlsMap: {
            [key: string]: ControlStructure
        } = structure.reduce((controlMemo, control) => {
            return {
                ...controlMemo,
                [control.id]: control
            };
        }, {});

        const paginationOptions = getControlsPaginationOptionsByControlIdOnSelectionChange({
            controlId,
            value,
            selection,
            controls: controlsMap
        });
        const newSelection: { [key: string]: string[] } = { ...selection };
        const selectedOnlyOptions: SelectedOnlyOptions = _.pick(newSelection, _.pluck(paginationOptions, 'name'));
        selectedOnlyOptions[controlId] = value;

        const newInitialValuesPromise = this.inputControlsService
            .getInputControlsOnlySelectedValue(uri, selectedOnlyOptions);

        return this.fetchInputControlsOptionsByPaginatedValuesOptionsAndUri({
            paginationOptions,
            uri
        }).then((response) => {
            return $.when<SelectedOnlyResponse>(newInitialValuesPromise).then((res) => {
                const { inputControlState } = res;
                const selectionPerControl = inputControlState && inputControlState.reduce((ICStateMemo, controlSelection) => {
                    return {
                        ...ICStateMemo,
                        [controlSelection.id]: controlSelection.options ? _.pluck(controlSelection.options, 'value') || []
                            : controlSelection.value
                    }
                }, {});
                return $.Deferred().resolve(response, selectionPerControl, paginationOptions)
            })
        });
    }

    fetchInitialInputControlsValuesByUriAndStructure(
        uri: string,
        structure: ControlStructure[],
        preSelectionData: { [key: string]: string[] }
    ) {
        const preSelectedData: { [key: string]: string[] } = fetchRequestParamsThroughURL(structure, preSelectionData);
        const initialPaginatedValuesOptions = structure.map((control) => {
            return getControlPaginationOptionsByControlIdAndType(
                control.id,
                control.type,
                preSelectedData[control.id] ? {
                    value: preSelectedData[control.id]
                }
                    : {
                        select: RestParamsEnum.SELECTED_VALUES
                    }
            );
        });

        const initialPaginatedValuesPromise = this.inputControlsService
            .getInputControlsPaginatedValues(uri, initialPaginatedValuesOptions);

        const selectedValuesPromise = this.inputControlsService
            .getInputControlsSelectedValues(uri);

        return $.when<
            PaginatedValuesResponse | [PaginatedValuesResponse],
            [SelectedValuesResponse]
        >(initialPaginatedValuesPromise, selectedValuesPromise)
            .then((
                paginatedValuesResponse,
                selectedValuesResponse
            ) => {
                const [
                    selectedValues
                ] = selectedValuesResponse;

                const paginatedValues = paginatedValuesResponse instanceof Array
                    ? paginatedValuesResponse[0]
                    : paginatedValuesResponse;

                let selection = {};
                selection = selectedValues ? selectedValues.selectedValue.reduce((selectedMemo, controlSelection) => {
                    return {
                        ...selectedMemo,
                        [controlSelection.id]: controlSelection.options || []
                    };
                }, {}) : {};

                const controlsMap = structure.reduce((structureMemo, control) => {
                    return {
                        ...structureMemo,
                        [control.id]: preSelectedData[control.id] ? {
                            ...control,
                            value: preSelectedData[control.id]
                        } : {
                            ...control,
                            select: RestParamsEnum.SELECTED_VALUES
                        }
                    };
                }, {});

                const paginationOptionsPerControl = getPaginatedValuesOptionsForQueryControls(
                    structure,
                    controlsMap,
                    (option) => {
                        return preSelectedData[option.name] ? {
                            ...option,
                            value: preSelectedData[option.name]
                        } : {
                            ...option,
                            select: RestParamsEnum.SELECTED_VALUES
                        }
                    },
                    (options) => {
                        this.inputControlsService.setCacheValueForControlPaginatedValues(
                            uri,
                            options,
                            paginatedValues
                        );
                    }
                );

                // case when selection is passing through URL
                if (!_.isEmpty(preSelectedData)) {
                    const newSelection: { [key: string]: string[] } = { ...selection };
                    let optionsForValues: { [key: string]: string[] } = {};
                    Object.keys(newSelection).forEach((ip) => {
                        optionsForValues[ip] = _.pluck(newSelection[ip], 'value');
                    });
                    optionsForValues = { ...optionsForValues, ...preSelectedData };
                    const newInitialValuesPromise = this.inputControlsService
                        .getInputControlsOnlySelectedValue(uri, optionsForValues);
                    return $.when<SelectedOnlyResponse>(newInitialValuesPromise).then((res) => {
                        const { inputControlState } = res;
                        selection = inputControlState && inputControlState.reduce((stateMemo, controlSelection) => {
                            return {
                                ...stateMemo,
                                [controlSelection.id]: controlSelection.options ? controlSelection.options.map((ip) => _.omit(ip, 'selected')) || []
                                    : controlSelection.value
                            };
                        }, {});

                        return $.Deferred().resolve({
                            structure,
                            selection,
                            paginationOptionsPerControl,
                            paginatedValuesResponse: paginatedValues
                        });
                    })
                }
                return $.Deferred().resolve({
                    structure,
                    selection,
                    paginationOptionsPerControl,
                    paginatedValuesResponse: paginatedValues
                });
            });
    }

    private fetchInputControlsOptionsByPaginatedValuesOptionsAndUri(options: {
        paginationOptions: PaginatedValuesOptions[],
        paginationOptionsPerControl?: {
            [key: string]: PaginatedValuesOptions[]
        },
        uri: string
    }) {
        const {
            paginationOptions,
            paginationOptionsPerControl = {},
            uri
        } = options;

        this.inputControlsService.clearCache();

        return this.inputControlsService.getInputControlsPaginatedValues(uri, paginationOptions)
            .then((response) => {
                paginationOptions.forEach((controlPaginationOptions) => {
                    const controlId = controlPaginationOptions.name;

                    if (paginationOptionsPerControl[controlId]) {
                        this.inputControlsService.setCacheValueForControlPaginatedValues(
                            uri,
                            paginationOptionsPerControl[controlId],
                            response
                        );
                    }
                });
                return response;
            });
    }
};
