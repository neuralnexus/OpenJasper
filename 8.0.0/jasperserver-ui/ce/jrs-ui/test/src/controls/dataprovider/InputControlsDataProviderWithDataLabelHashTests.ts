import $ from 'jquery';
import sinon, { SinonSandbox } from 'sinon';
import InputControlsDataProviderWithDataLabelHash from 'src/controls/dataprovider/InputControlsDataProviderWithDataLabelHash';

import {
    InputControlsDataProviderType
} from 'src/controls/dataprovider/types/InputControlsDataProviderType';
import { PaginatedControlOption } from '../../../../src/controls/rest/types/InputControlsServiceType';

describe('InputControlsDataProviderWithDataLabelHash Tests', () => {
    let inputControlsDataProviderWithDataLabelHash: InputControlsDataProviderWithDataLabelHash;
    let inputControlsDataProvider: InputControlsDataProviderType;
    let sandbox: SinonSandbox;

    beforeEach(() => {
        sandbox = sinon.createSandbox();

        inputControlsDataProvider = {
            getData() {
                return ($.Deferred().resolve({
                    data: [
                        {
                            value: '1',
                            label: '1'
                        },
                        {
                            value: '2',
                            label: '2'
                        }
                    ],
                    total: 2
                }).then((result) => {
                    return result;
                }) as unknown as Promise<{
                    data: PaginatedControlOption[],
                    total: number
                }>);
            }
        };

        inputControlsDataProviderWithDataLabelHash = new InputControlsDataProviderWithDataLabelHash({
            inputControlsDataProvider
        });
    });

    afterEach(() => {
        sandbox.restore();
    });

    it('should get label by value after data fetch', (done) => {
        const spy = sandbox.spy(inputControlsDataProvider, 'getData');

        inputControlsDataProviderWithDataLabelHash.getData('/report/uri', [
            {
                name: 'controlId',
                offset: 0,
                limit: 100,
                criteria: 'a'
            }
        ]).then(() => {
            expect(spy).toHaveBeenCalledWith('/report/uri', [
                {
                    name: 'controlId',
                    offset: 0,
                    limit: 100,
                    criteria: 'a'
                }
            ]);

            expect(inputControlsDataProviderWithDataLabelHash.getLabelByValue('1'))
                .toEqual('1');

            done();
        });
    });
});
