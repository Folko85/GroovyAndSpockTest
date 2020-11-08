import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class AuthSpecification extends Specification {

    @Shared def client = new RESTClient( "https://postman-echo.com/")
    @Shared def SUCCESS_RESPONSE = 200
    @Shared def UNAUTHORIZED_RESPONSE = 401
    @Shared def basicProperty, digestProperty, oauth1Property, basic, digest, oauth1

    def setupSpec() {     //эти гигантские строки вынесем, чтоб выглядело красиво
        basicProperty = "Basic cG9zdG1hbjpwYXNzd29yZA=="
        digestProperty = "Digest username=\"postman\"," +
                " realm=\"Users\"," +
                " nonce=\"ni1LiL0O37PRRhofWdCLmwFsnEtH1lew\"," +
                " uri=\"/digest-auth\"," +
                " response=\"254679099562cf07df9b6f5d8d15db44\"," +
                " opaque=\"\""
        oauth1Property = "OAuth oauth_consumer_key=\"RKCGzna7bv9YD57c\"," +
                "oauth_signature_method=\"PLAINTEXT\"," +
                "oauth_timestamp=\"1602626369\"," +
                "oauth_nonce=\"RohrSiqoTiH\"," +
                "oauth_version=\"1.0\"," +
                "oauth_signature=\"D%252BEdQ-gs%2524-%2525%25402Nu7%26\""
        basic = "/basic-auth"
        digest = "/digest-auth"
        oauth1 = "/oauth1"
    }

    @Unroll
    def "auth success test"() {
        expect:
        getResponseFrom(requestType, request) == result
        where:                                           //вот эта штука сильно сокращает казалось бы короткий код
        requestType | request       || result
        basic       | basicProperty || SUCCESS_RESPONSE
        digest      | digestProperty|| SUCCESS_RESPONSE
        oauth1      | oauth1Property|| SUCCESS_RESPONSE
    }

    @Unroll
    def "auth failure test"() {
        when:
        getResponseFrom(requestType, request)
        then:
        HttpResponseException e = thrown(HttpResponseException)
        e.response.status == result
        where:                               //вот эта штука сильно сокращает казалось бы короткий код
        requestType | request       || result
        basic       | null  || UNAUTHORIZED_RESPONSE
        digest      | null  || UNAUTHORIZED_RESPONSE
        oauth1      | null  || UNAUTHORIZED_RESPONSE
    }

    def getResponseFrom(String path, String property) throws IOException {
        client.headers['Authorization'] = property
        def response = client.get( path : path )
        return response.status
    }
}