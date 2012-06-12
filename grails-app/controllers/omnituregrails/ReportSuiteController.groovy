package omnituregrails


import java.security.MessageDigest
import sun.misc.BASE64Encoder
import sun.misc.CharacterEncoder
import grails.converters.*
import java.text.SimpleDateFormat;

class ReportSuiteController {

    def passwordCodec

    String USERNAME = "cflanagan:New York Magazine";
    String ENDPOINT = "https://beta-api.omniture.com/admin/1.3/rest/"; // san jose beta


    def index() {
        

        def method = "Company.GetReportSuites"
        def data = "{\"rs_types\":[\"standard\"]}"

        render getResult(method, data)
        
    }

    def getHeader = {

        byte[] nonceB = generateNonce();
        String nonce = base64Encode(nonceB);
        String created = generateTimestamp();
        String password64 = getBase64Digest(nonceB, created.getBytes("UTF-8"), PASSWORD.getBytes("UTF-8"));

        def header = new StringBuffer("UsernameToken Username=\"");
        header << USERNAME
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

    def mostpopular() {

        def method = "Report.QueueTrended"
        def data = params['data']

    }

    def getReport() {

    }

    def getResult = { method, data ->

        def url = new URL(ENDPOINT + "?method=" + method);

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
}
