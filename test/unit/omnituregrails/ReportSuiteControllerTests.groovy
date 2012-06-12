package omnituregrails


import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import static groovyx.net.http.ContentType.JSON
import groovy.json. JsonBuilder
import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ReportSuiteController)
class ReportSuiteControllerTests {


    def void test_get_report_suite() {

        def http = new HTTPBuilder("http://localhost:8080")
        http.request(Method.valueOf("GET"), JSON) {
            uri.path = '/OmnitureGrails/reportSuite/list/'
            response.success = {resp, json ->
                json.report_suites.each { rs ->
                    assert rs.rsid != ""
                }
            }
        }
    }

    def void testGetMostPopular() {
        def http = new HTTPBuilder("http://localhost:8080")
        http.request(Method.valueOf("GET"), JSON) {
            uri.path = '/OmnitureGrails/reportSuite/mostpopular/nymvulture/2012-06-12/5/'
            response.success = {resp, json ->
                assert json.status == "done"
            }
        }

    }
}
