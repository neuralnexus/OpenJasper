import sinon, { SinonSandbox } from 'sinon';
import InputControlsDataProvider from 'src/controls/dataprovider/InputControlsDataProvider';
import {
    InputControlsServiceType
} from 'src/controls/rest/types/InputControlsServiceType';

import { getInputControlsServiceMock } from '../mock/inputContolsServiceMock';

describe('InputControlsDataProvider Tests', () => {
    let inputControlsDataProvider: InputControlsDataProvider;
    let inputControlsService: InputControlsServiceType;
    let sandbox: SinonSandbox;

    beforeEach(() => {
        sandbox = sinon.createSandbox();

        inputControlsService = getInputControlsServiceMock();

        inputControlsDataProvider = new InputControlsDataProvider({
            controlId: 'controlId',
            inputControlsService
        });
    });

    afterEach(() => {
        sandbox.restore();
    });

    it('should get paginated controls data', (done) => {
        const spy = sandbox.spy(inputControlsService, 'getInputControlsPaginatedValues');

        inputControlsDataProvider.getData('/report/uri', [
            {
                name: 'controlId',
                offset: 0,
                limit: 100,
                criteria: 'a'
            }
        ]).then((resp) => {
            expect(spy).toHaveBeenCalledWith('/report/uri', [
                {
                    name: 'controlId',
                    offset: 0,
                    limit: 100,
                    criteria: 'a'
                }
            ]);

            expect(resp).toEqual({
                data: [
                    {
                        selected: true,
                        value: 'value',
                        label: 'label'
                    }
                ],
                total: 100
            });

            done();
        });
    });
});
