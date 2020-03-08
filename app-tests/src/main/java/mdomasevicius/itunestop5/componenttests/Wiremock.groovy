package mdomasevicius.itunestop5.componenttests

class Wiremock {

    private final static HttpClient httpClient = new HttpClient('http://localhost:7000')

    static void resetRequestLog() {
        def response = httpClient.delete('/__admin/requests')
        assert response.status == 200
    }

    static FluentResponse requestLog() {
        def response = httpClient.get('/__admin/requests')
        response.status == 200
        return response
    }
}
