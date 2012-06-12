package omnituregrails


import java.security.MessageDigest
import sun.misc.BASE64Encoder
import sun.misc.CharacterEncoder
import grails.converters.*
import java.text.SimpleDateFormat;
import groovy.json.*

class ReportSuiteController {

    def index() {
  
        redirect(action: "list", params: params)

    }

    def list() {
       
        def method = "Company.GetReportSuites"
        def data = [
            rs_types: 'standard',
        ]

        render getResult(method, data.toString())
        
    }

    def mostpopular() {

        def today = new Date().format("yyyy-MM-dd")
        def date = params['date']?: today.previous() // sensible default value
        def count = params['count']?:"5"

        // This is a multistep process involing requesting the report
        // checking to see whether it is ready
        // getting the value of the report

        // Step 1. Requesting the report
        def json = new JsonBuilder()
        json.reportDescription {
            reportSuiteID params['reportsuiteid']
            dateFrom date
            dateTo date
            metrics ([
                {
                    id "pageViews"
                },
                {
                    id "totalPageViews"
                }
            ])
            elements ([
                    {
                      id "page"
                    },
                    {
                       top count
                    }
            ])
        }

        def data = json.toString()
        def result = getResult("Report.QueueRanked", data) // to give us page ranking
        def slurper = new JsonSlurper()
        def reportID = slurper.parseText(result.toString()).reportID


        // Step 2. Check the report status
        def reportId = [
            reportID: reportID
        ] as JSON
        def status
        def queuedReport
        def tries = 0
        while(status != "done" && tries < 10 || status == "failed") {
            queuedReport = getResult("Report.GetStatus", reportId.toString())
            status = slurper.parseText(queuedReport.toString()).status
            tries++
        }

        // Step 3. print the report to the screen
        def reportResults
        if(status == "done") {
            reportResults = getResult("Report.GetReport", reportId.toString())

        } else {
            reportResults = [
                status: "report generation failed"
            ]
            
        }

        render reportResults

    }


    def getResult = { method, data ->

        def endpoint = grailsApplication.config.omniture.endpoint

        def url = new URL(endpoint + "?method=" + method);

        def connection = url.openConnection();
        connection.addRequestProperty("X-WSSE", getHeader());

        connection.setDoOutput(true);
        def wr = new OutputStreamWriter(connection.getOutputStream());
        wr.write(data);
        wr.flush();

        InputStream ins = connection.getInputStream();
        def text = new JSON().parse(ins, "UTF-8")
        return text as JSON

    }


    def getHeader = {

        def username = grailsApplication.config.omniture.username
        def password = grailsApplication.config.omniture.password

        byte[] nonceB = generateNonce();
        String nonce = base64Encode(nonceB);
        String created = generateTimestamp();
        String password64 = getBase64Digest(nonceB, created.getBytes("UTF-8"), password.getBytes("UTF-8"));

        def header = new StringBuffer("UsernameToken Username=\"");
        header << username
        header << "\", "
        header << "PasswordDigest=\""
        header << password64.trim()
        header << "\", "
        header << "Nonce=\""
        header << nonce.trim()
        header << "\", "
        header << "Created=\""
        header << created
        header << "\""
        return header.toString()
    }

    def getBase64Digest = {nonce, created, password  ->

        MessageDigest md = MessageDigest.getInstance('SHA')
	md.update(nonce)
        md.update(created)
        md.update(password)
	return (new BASE64Encoder()).encode(md.digest())

    }

    def generateTimestamp = {
        return new Date().format("yyyy-MM-dd'T'HH:mm:ss'Z'")
    }

    def generateNonce = {
        return String.format('%tQ', new Date()).getBytes('UTF-8')
    }

    def base64Encode = { toEncode ->
        return toEncode.encodeBase64().toString()
    }

}
