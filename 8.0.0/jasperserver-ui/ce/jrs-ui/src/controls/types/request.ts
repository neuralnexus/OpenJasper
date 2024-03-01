interface RequestOptions {
    type: string,
    headers: {
        Accept?: string,
        'Content-Type'?: string
    },
    url: string,
    data?:string
}

// eslint-disable-next-line
export default RequestOptions;
