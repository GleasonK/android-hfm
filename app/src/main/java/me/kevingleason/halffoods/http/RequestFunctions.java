package me.kevingleason.halffoods.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.jar.Attributes;

import me.kevingleason.halffoods.util.HFConfig;

/**
 * RequestFunctions provides static methods to place requests.
 *  May need to look into the harmful aspect of using a static HttpClient
 */
public class RequestFunctions {
    private static HttpClient mHttpClient;

    public RequestFunctions(){}

    private static HttpClient getHttpClient() {
        if (mHttpClient == null) {
            mHttpClient = new DefaultHttpClient();
            final HttpParams params = mHttpClient.getParams();
            ClientConnectionManager mgr = mHttpClient.getConnectionManager();
            HttpConnectionParams.setConnectionTimeout(params, HFConfig.HTTP_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, HFConfig.HTTP_TIMEOUT);
            ConnManagerParams.setTimeout(params, HFConfig.HTTP_TIMEOUT);
            mHttpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()),params);
        }
        return mHttpClient;
    }

    public static String executeHttpPost(String url, ArrayList<NameValuePair> postParameters) throws IOException {
        BufferedReader in = null;
        try {
            HttpClient client = getHttpClient();
            HttpPost request = new HttpPost(url);
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);
            request.setEntity(formEntity);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            String result = sb.toString();
            response.getEntity().consumeContent();
            return result;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String executeHttpPostHeaders(String toUrl, ArrayList<NameValuePair> headers, ArrayList<NameValuePair> postParameters) throws IOException {
        String line;
        StringBuffer jsonString = new StringBuffer();
        try {

            URL url = new URL(toUrl);

            JSONObject jsonObject = new JSONObject();
            for(NameValuePair nvp : postParameters ){
                jsonObject.put(nvp.getName(),nvp.getValue());
            }
            String payload = jsonObject.toString();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Content-type","application/json");
            connection.setRequestProperty("Accept","application/json");

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(payload);
            writer.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
            br.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonString.toString();
    }


    /**
     * Performs an HTTP GET request to the specified url.
     *
     * @param url The web address to post the request to
     * @return The result of the request
     * @throws Exception
     */
    public static String executeHttpGet(String url) throws IOException, URISyntaxException {
        BufferedReader in = null;
        try {
            HttpClient client = getHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(url));
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();

            String result = sb.toString();
            return result;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isAnyNetworkAvailable(final Context context) {
        boolean status = false;
        final ConnectivityManager connManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connManager) {
            NetworkInfo[] allNetworks = connManager.getAllNetworkInfo();
            if (null != allNetworks) {
                for (NetworkInfo info : allNetworks) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        status = true;
                        break;
                    }
                }
            }
        }
        return status;
    }

    public static boolean isOnline(final Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
