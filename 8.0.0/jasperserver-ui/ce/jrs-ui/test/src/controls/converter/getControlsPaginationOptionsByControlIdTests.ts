import getControlsPaginationOptionsByControlId from 'src/controls/converter/getControlsPaginationOptionsByControlId';
import InputControlTypeEnum from 'src/controls/enum/inputControlTypeEnum';
import RestParamsEnum from 'src/controls/rest/enum/restParamsEnum';

describe('getControlsPaginationOptionsByControlIdTests Tests', () => {

    describe('should get pagination options for', () => {
        /*
            case : 1
        */
        it('control 2 in 1 -> 2 -> 3 hierarchy', () => {
            const result = getControlsPaginationOptionsByControlId({
                controlId: '2',
                controls: {
                    1: {
                        type: InputControlTypeEnum.MULTI_SELECT,
                        slaveDependencies: ['2', '3']
                    },
                    2: {
                        type: InputControlTypeEnum.MULTI_SELECT,
                        masterDependencies: ['1'],
                        slaveDependencies: ['3']
                    },
                    3: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        masterDependencies: ['1', '2']
                    }
                }
            });

            expect(result).toEqual([
                {
                    name: '1',
                    offset: 0,
                    limit: 100
                },
                {
                    name: '2',
                    offset: 0,
                    limit: 100
                },
                {
                    name: '3',
                    offset: 0,
                    limit: 100
                }
            ]);
        });

        it('control 2 in 1 -> 2 -> 3 hierarchy if control 3 is a multi select checkbox', () => {
            const result = getControlsPaginationOptionsByControlId({
                controlId: '2',
                controls: {
                    1: {
                        type: InputControlTypeEnum.MULTI_SELECT,
                        slaveDependencies: ['2', '3']
                    },
                    2: {
                        type: InputControlTypeEnum.MULTI_SELECT,
                        masterDependencies: ['1'],
                        slaveDependencies: ['3']
                    },
                    3: {
                        type: InputControlTypeEnum.MULTI_SELECT_CHECKBOX,
                        masterDependencies: ['1', '2']
                    }
                }
            });

            expect(result).toEqual([
                {
                    name: '1',
                    offset: 0,
                    limit: 100
                },
                {
                    name: '2',
                    offset: 0,
                    limit: 100
                },
                {
                    name: '3',
                    offset: 0
                }
            ]);
        });

        it('control 2 in 1 -> 2 -> 3 hierarchy with select and value properties', () => {
            const result = getControlsPaginationOptionsByControlId({
                controlId: '2',
                controls: {
                    1: {
                        type: InputControlTypeEnum.MULTI_SELECT,
                        slaveDependencies: ['2', '3'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    2: {
                        type: InputControlTypeEnum.MULTI_SELECT,
                        masterDependencies: ['1'],
                        slaveDependencies: ['3'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    3: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        masterDependencies: ['1', '2'],
                        select: RestParamsEnum.SELECTED_VALUES
                    }
                }
            });

            expect(result).toEqual([
                {
                    name: '1',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '2',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '3',
                    offset: 0,
                    limit: 100,
                    select: RestParamsEnum.SELECTED_VALUES
                }
            ]);
        });

        // case 2
        it('control 3 in 1 -> 2 -> 3 hierarchy with select and value properties', () => {
            const result = getControlsPaginationOptionsByControlId({
                controlId: '3',
                controls: {
                    1: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        slaveDependencies: ['2'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    2: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        masterDependencies: ['1'],
                        slaveDependencies: ['3'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    3: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        masterDependencies: ['2'],
                        value: ['value'],
                        slaveDependencies: ['4'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    4: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        masterDependencies: ['3'],
                        select: RestParamsEnum.SELECTED_VALUES
                    }
                }
            });

            expect(result).toEqual([
                {
                    name: '1',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '2',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '3',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '4',
                    offset: 0,
                    limit: 100,
                    select: RestParamsEnum.SELECTED_VALUES
                }
            ]);
        });
        it('control 4 in non-cascade selection', () => {
            const result = getControlsPaginationOptionsByControlId({
                controlId: '4',
                controls: {
                    1: {
                        type: InputControlTypeEnum.MULTI_SELECT,
                        slaveDependencies: ['2'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    2: {
                        type: InputControlTypeEnum.MULTI_SELECT,
                        masterDependencies: ['1'],
                        slaveDependencies: ['3'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    3: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        masterDependencies: ['2'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    4: {
                        type: InputControlTypeEnum.MULTI_SELECT_CHECKBOX,
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    5: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    }
                }
            });

            expect(result.length).toEqual(5);
        });

        // case 3
        it('control 2 in 1 -> 2 -> 3 hierarchy with select and value properties', () => {
            const result = getControlsPaginationOptionsByControlId({
                controlId: '2',
                controls: {
                    1: {
                        type: InputControlTypeEnum.MULTI_SELECT,
                        slaveDependencies: ['2,4,5'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    2: {
                        type: InputControlTypeEnum.MULTI_SELECT,
                        masterDependencies: ['1'],
                        slaveDependencies: ['3'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    3: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        masterDependencies: ['2'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    4: {
                        type: InputControlTypeEnum.MULTI_SELECT_CHECKBOX,
                        value: ['value'],
                        masterDependencies: ['1'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    5: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        value: ['value'],
                        masterDependencies: ['1'],
                        select: RestParamsEnum.SELECTED_VALUES
                    }
                }
            });

            expect(result).toEqual([
                {
                    name: '1',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '2',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '3',
                    offset: 0,
                    limit: 100,
                    select: RestParamsEnum.SELECTED_VALUES
                },
                {
                    name: '4',
                    offset: 0,
                    value: ['value']
                },
                {
                    name: '5',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                }
            ]);
        });
        it('control 2 in 1 -> 2 -> 3 hierarchy , control 3 has previous selction', () => {
            const result = getControlsPaginationOptionsByControlId({
                controlId: '2',
                controls: {
                    1: {
                        type: InputControlTypeEnum.MULTI_SELECT,
                        slaveDependencies: ['3'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    2: {
                        type: InputControlTypeEnum.MULTI_SELECT,
                        slaveDependencies: ['3'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    3: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        masterDependencies: ['1', '2'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    }
                }
            });

            expect(result).toEqual([
                {
                    name: '1',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '2',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '3',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                }
            ]);
        });
        it('control 1 in 1 -> 2 -> 3 -> 4 hierarchy with select and value properties', () => {
            const result = getControlsPaginationOptionsByControlId({
                controlId: '1',
                controls: {
                    1: {
                        type: InputControlTypeEnum.MULTI_SELECT,
                        slaveDependencies: ['2', '3'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    2: {
                        type: InputControlTypeEnum.MULTI_SELECT,
                        slaveDependencies: ['4'],
                        masterDependencies: ['1'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    3: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        masterDependencies: ['1'],
                        slaveDependencies: ['4'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    4: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        masterDependencies: ['3', '2'],
                        select: RestParamsEnum.SELECTED_VALUES
                    }
                }
            });

            expect(result).toEqual([
                {
                    name: '1',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '2',
                    offset: 0,
                    limit: 100,
                    select: RestParamsEnum.SELECTED_VALUES
                },
                {
                    name: '3',
                    offset: 0,
                    limit: 100,
                    select: RestParamsEnum.SELECTED_VALUES
                },
                {
                    name: '4',
                    offset: 0,
                    limit: 100,
                    select: RestParamsEnum.SELECTED_VALUES
                }
            ]);
        });
        it('control 2 in 1 -> 2 -> 3 -> 4 hierarchy with select and value properties', () => {
            const result = getControlsPaginationOptionsByControlId({
                controlId: '2',
                controls: {
                    1: {
                        type: InputControlTypeEnum.MULTI_SELECT,
                        slaveDependencies: ['2', '3'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    2: {
                        type: InputControlTypeEnum.MULTI_SELECT,
                        slaveDependencies: ['4'],
                        masterDependencies: ['1'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    3: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        masterDependencies: ['1'],
                        slaveDependencies: ['4'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    4: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        masterDependencies: ['3', '2'],
                        select: RestParamsEnum.SELECTED_VALUES
                    }
                }
            });

            expect(result).toEqual([
                {
                    name: '1',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '2',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '3',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '4',
                    offset: 0,
                    limit: 100,
                    select: RestParamsEnum.SELECTED_VALUES
                }
            ]);
        });
        it('control 3 in 1 -> 2 -> 3 -> 4 hierarchy with select and value properties', () => {
            const result = getControlsPaginationOptionsByControlId({
                controlId: '3',
                controls: {
                    1: {
                        type: InputControlTypeEnum.MULTI_SELECT,
                        slaveDependencies: ['2', '3'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    2: {
                        type: InputControlTypeEnum.MULTI_SELECT,
                        slaveDependencies: ['4'],
                        masterDependencies: ['1'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    3: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        masterDependencies: ['1'],
                        slaveDependencies: ['4'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    4: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        masterDependencies: ['3', '2'],
                        select: RestParamsEnum.SELECTED_VALUES
                    }
                }
            });

            expect(result).toEqual([
                {
                    name: '1',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '2',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '3',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '4',
                    offset: 0,
                    limit: 100,
                    select: RestParamsEnum.SELECTED_VALUES
                }
            ]);
        });
        it('control 4 in 1 -> 2 -> 3 -> 4 hierarchy and has value', () => {
            const result = getControlsPaginationOptionsByControlId({
                controlId: '4',
                controls: {
                    1: {
                        type: InputControlTypeEnum.MULTI_SELECT,
                        slaveDependencies: ['2', '3'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    2: {
                        type: InputControlTypeEnum.MULTI_SELECT,
                        slaveDependencies: ['4'],
                        masterDependencies: ['1'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    3: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        masterDependencies: ['1'],
                        slaveDependencies: ['4'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    4: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        masterDependencies: ['3', '2'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    }
                }
            });

            expect(result).toEqual([
                {
                    name: '1',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '2',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '3',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '4',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                }
            ]);
        });
        // case 5
        it('control 1 in 1 -> 2 -> 3 -> 4->5 hierarchy with select and value properties', () => {
            const result = getControlsPaginationOptionsByControlId({
                controlId: '1',
                controls: {
                    1: {
                        type: InputControlTypeEnum.MULTI_SELECT,
                        slaveDependencies: ['3'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    2: {
                        type: InputControlTypeEnum.MULTI_SELECT,
                        slaveDependencies: ['3'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    3: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        masterDependencies: ['1', '2'],
                        slaveDependencies: ['4', '5'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    4: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        masterDependencies: ['3'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    5: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        masterDependencies: ['3'],
                        select: RestParamsEnum.SELECTED_VALUES
                    }
                }
            });

            expect(result).toEqual([
                {
                    name: '1',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '2',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '3',
                    offset: 0,
                    limit: 100,
                    select: RestParamsEnum.SELECTED_VALUES
                },
                {
                    name: '4',
                    offset: 0,
                    limit: 100,
                    select: RestParamsEnum.SELECTED_VALUES
                },
                {
                    name: '5',
                    offset: 0,
                    limit: 100,
                    select: RestParamsEnum.SELECTED_VALUES
                }
            ]);
        });
        it('control 2 in 1 -> 2 -> 3 -> 4->5 hierarchy with select and value properties', () => {
            const result = getControlsPaginationOptionsByControlId({
                controlId: '2',
                controls: {
                    1: {
                        type: InputControlTypeEnum.MULTI_SELECT,
                        slaveDependencies: ['3'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    2: {
                        type: InputControlTypeEnum.MULTI_SELECT,
                        slaveDependencies: ['3'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    3: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        masterDependencies: ['1', '2'],
                        slaveDependencies: ['4', '5'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    4: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        masterDependencies: ['3'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    5: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        masterDependencies: ['3'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    }
                }
            });

            expect(result).toEqual([
                {
                    name: '1',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '2',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '3',
                    offset: 0,
                    limit: 100,
                    select: RestParamsEnum.SELECTED_VALUES
                },
                {
                    name: '4',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '5',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                }
            ]);
        });
        it('control 3 in 1 -> 2 -> 3 -> 4->5 hierarchy with select and value properties and child contorl has previous selection', () => {
            const result = getControlsPaginationOptionsByControlId({
                controlId: '3',
                controls: {
                    1: {
                        type: InputControlTypeEnum.MULTI_SELECT,
                        slaveDependencies: ['3'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    2: {
                        type: InputControlTypeEnum.MULTI_SELECT,
                        slaveDependencies: ['3'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    3: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        masterDependencies: ['1', '2'],
                        slaveDependencies: ['4', '5'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    4: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        masterDependencies: ['3'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    },
                    5: {
                        type: InputControlTypeEnum.SINGLE_SELECT,
                        masterDependencies: ['3'],
                        value: ['value'],
                        select: RestParamsEnum.SELECTED_VALUES
                    }
                }
            });

            expect(result).toEqual([
                {
                    name: '1',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '2',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '3',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '4',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                },
                {
                    name: '5',
                    offset: 0,
                    limit: 100,
                    value: ['value']
                }
            ]);
        });
    });
});
