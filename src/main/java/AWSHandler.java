import org.springframework.web.util.UriUtils;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;
import org.apache.commons.httpclient.util.EncodingUtil;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class AWSHandler {

    public String createUniqueId() {
        return Instant.now() + "";
    }

    public void sendMessage(String messageGroupId, String message) {

        String url = null;
        try {
            url = "https://sqs.us-east-2.amazonaws.com/494748058640/claren.fifo?" + "Action=SendMessage" + "&" + "MessageBody=" + UriUtils.encodePathSegment(message, "UTF-8") + "&" + "MessageGroupId=" + UriUtils.encodePathSegment(messageGroupId, "UTF-8");
            System.out.println(url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String accessKeyId = "";
        String secretAccessKey = "";
        HttpGet getter = new HttpGet(url);
        TreeMap<String, String> queryArgs = new TreeMap<>();
        queryArgs.put("Action", "SendMessage");
        queryArgs.put("MessageBody", message);
        queryArgs.put("MessageGroupId", messageGroupId);


        CloseableHttpClient httpclient = HttpClients.createDefault();
        /**
         * Add host without http or https protocol.
         * You can also add other parameters based on your amazon service requirement.
         */
        TreeMap<String, String> awsHeaders = new TreeMap<String, String>();
        awsHeaders.put("host", "sqs.us-east-2.amazonaws.com");
        AWSV4Auth aWSV4Auth = new AWSV4Auth.Builder(accessKeyId, secretAccessKey)
                .regionName("us-east-2")
                .serviceName("sqs") // es - elastic search. use your service name
                .httpMethodName("GET") //GET, PUT, POST, DELETE, etc...
                .canonicalURI("/494748058640/claren.fifo") //end point
                .queryParametes(queryArgs) //query parameters if any
                .awsHeaders(awsHeaders) //aws header parameters
                .payload(null) // payload if any
                .debug() // turn on the debug mode
                .build();

        /* Get header calculated for request */
        Map<String, String> header = aWSV4Auth.getHeaders();
        for (Map.Entry<String, String> entrySet : header.entrySet()) {
            String key = entrySet.getKey();
            String value = entrySet.getValue();

            System.out.println(key + "=" + value + "\n");
            /* Attach header in your request */
            /* Simple get request */
            if (key == "Authorization")
                getter.addHeader(key, value);
            else if (key == "x-amz-date")
                getter.addHeader(key, value);
        }

        try {
            CloseableHttpResponse response1 = httpclient.execute(getter);
            System.out.println(EntityUtils.toString(response1.getEntity(), "UTF-8"));
            System.out.println();
        } catch (IOException e) {
            System.out.println("Exception encountered");
            e.printStackTrace();
        }
    }

//    private String encodeParameter(String param){
//        try {
//
////            return URLEncoder.encode(param, "UTF-8");
//        } catch (Exception e) {
////            return URLEncoder.encode(param);
//        }
//    }

    public static void main(String[] args) {
        AWSHandler awsHandler = new AWSHandler();
        awsHandler.sendMessage("1", "Hello world whats good");
    }
}