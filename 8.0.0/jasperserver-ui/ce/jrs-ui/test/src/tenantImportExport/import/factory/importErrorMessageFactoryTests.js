import importErrorMessageFactory from 'src/tenantImportExport/import/factory/importErrorMessageFactory';
import awsSettings from 'src/settings/awsSettings.settings';
import importRestErrorCodesEnum from 'src/tenantImportExport/import/enum/importRestErrorCodesEnum';

describe('importErrorMessageFactory Tests.', () => {

    it('should return import.error.unexpected error', () => {
        const error =  importErrorMessageFactory.create('unknown.error.code');

        expect(error).toEqual('import.error.unexpected');
    });

    it('should return existing error', () => {
        const error =  importErrorMessageFactory.create('import.error.cancelled');

        expect(error).toEqual('import.error.cancelled');
    });

    it('should return import.decode.failed error', () => {
        const error =  importErrorMessageFactory.create(importRestErrorCodesEnum.IMPORT_DECODE_FAILED);

        expect(error).toEqual('import.decode.failed');
    });

    it('should return aws specific import.decode.failed error (JrsAmi)', () => {
        awsSettings.productTypeIsJrsAmi = true;

        const error =  importErrorMessageFactory.create(importRestErrorCodesEnum.IMPORT_DECODE_FAILED);

        expect(error).toEqual('import.decode.failed.aws');

        awsSettings.productTypeIsJrsAmi = false;
    });

    it('should return aws specific import.decode.failed error (MpAmi)', () => {
        awsSettings.productTypeIsMpAmi = true;

        const error =  importErrorMessageFactory.create(importRestErrorCodesEnum.IMPORT_DECODE_FAILED);

        expect(error).toEqual('import.decode.failed.aws');

        awsSettings.productTypeIsMpAmi = false;
    });
});