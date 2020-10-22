import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import spock.lang.Shared
import spock.lang.Specification

class AuthSpecification extends Specification {

    @Shared def client = new RESTClient( "https://postman-echo.com/")
    def SUCCESS_RESPONSE = 200
    def UNAUTHORIZED_RESPONSE = 401

    def setupSpec() {   //не стал выносить сюда url, для разнообразия

    }

    def "success basic auth"() {
        setup:                             //BDD - особенность Spock
        def basic = "/basic-auth"
        when:
        def x = getResponseFrom(basic, "Basic cG9zdG1hbjpwYXNzd29yZA==")
        then:
        x == SUCCESS_RESPONSE
    }

    def "failure basic auth"() {
        setup:
        def basic = "/basic-auth"    //все эти дефы больше похожи на JS
        when:
        getResponseFrom(basic, null)
        then:
        HttpResponseException e = thrown(HttpResponseException)   // Для http-билдера 401 - экзепшен.
        e.response.status == UNAUTHORIZED_RESPONSE
    }

    def "success digest auth"() {
        setup:
        def digest = "/digest-auth"
        when:
        def x = getResponseFrom(digest,"Digest username=\"postman\"," +
                " realm=\"Users\"," +
                " nonce=\"ni1LiL0O37PRRhofWdCLmwFsnEtH1lew\"," +
                " uri=\"/digest-auth\"," +
                " response=\"254679099562cf07df9b6f5d8d15db44\"," +
                " opaque=\"\"")
        then:
        x == SUCCESS_RESPONSE
    }

    def "failure digest auth"() {
        setup:
        def digest = "/digest-auth"
        when:
        getResponseFrom(digest, null)
        then:
        HttpResponseException e = thrown(HttpResponseException)
        e.response.status == UNAUTHORIZED_RESPONSE
    }

    def "success oauth"() {
        setup:
        def oauth1 = "/oauth1"
        when:
        def x = getResponseFrom(oauth1,"OAuth oauth_consumer_key=\"RKCGzna7bv9YD57c\"," +
                "oauth_signature_method=\"PLAINTEXT\"," +
                "oauth_timestamp=\"1602626369\"," +
                "oauth_nonce=\"RohrSiqoTiH\"," +
                "oauth_version=\"1.0\"," +
                "oauth_signature=\"D%252BEdQ-gs%2524-%2525%25402Nu7%26\"")
        then:
        x == SUCCESS_RESPONSE
    }

    def "failure oauth"() {
        setup:
        def oauth1 = "/oauth1"
        when:
        getResponseFrom(oauth1, null)
        then:
        HttpResponseException e = thrown(HttpResponseException)
        e.response.status == UNAUTHORIZED_RESPONSE
    }
          //поскольку любой джава-код является верным груви-кодом, то просто оставим этот метод неизменным
    def getResponseFrom(String path, String property) throws IOException {
        client.headers['Authorization'] = property
        def response = client.get( path : path )
        return response.status
    }

}