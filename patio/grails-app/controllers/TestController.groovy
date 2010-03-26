class TestController {
    def index = {
        def json
        withHttp(uri: 'http://localhost:8083') {
            json = get(path: '/mifos/i/v1/clients')
        }
        [ clients: json ]
    }

    def viewClient = {
        def json
        withHttp(uri: 'http://localhost:8083') {
            json = get(path: "/mifos/i/v1/client/${params.id}")
        }
        [ client: json ]
    }
}
