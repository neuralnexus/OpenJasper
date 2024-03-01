import extractRootLevelDataFromHtmlResponse from 'src/bi/repository/util/extractRootLevelDataFromHtmlResponse';

describe('extractRootLevelDataFromHtmlResponse Tests.', () => {
    it('should extract data from html div', () => {
        const dataHtml = '  <div id="id">{"children": [{"label": "<label>", "id": "1"}]}</div>  ';

        expect(extractRootLevelDataFromHtmlResponse(dataHtml))
            .toEqual({
                children: [
                    {
                        label: '<label>',
                        id: '1',
                    },
                ],
            });
    });

    it('should not extract data from html div', () => {
        const dataHtml = '';

        expect(extractRootLevelDataFromHtmlResponse(dataHtml)).toEqual({});
    });
});
