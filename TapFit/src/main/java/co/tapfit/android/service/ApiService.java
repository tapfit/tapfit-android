package co.tapfit.android.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteBaseService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zackmartinsek on 9/7/13.
 */
public class ApiService extends IntentService {

    private static final String NAME = ApiService.class.getSimpleName();
    private static final String TAG = NAME;

    public static final int GET    = 0x1;
    public static final int POST   = 0x2;
    public static final int PUT    = 0x3;
    public static final int DELETE = 0x4;

    public static final String URL = "url";
    public static final String HTTP_VERB = "http_verb";
    public static final String PARAMS = "params";
    public static final String RESULT_RECEIVER = "result_receiver";
    public static final String REST_RESULT = "rest_result";

    public static final String CONNECTION_ERROR = "connection_error";

    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private HttpClient client = setUpHttpClient();

    public ApiService() {
        super(NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        final Bundle extras = intent.getExtras();

        if (extras == null || !extras.containsKey(URL) || !extras.containsKey(RESULT_RECEIVER)) {
            // Extras contain our ResultReceiver and data is our REST action.
            // So, without these components we can't do anything useful.
            Log.e(TAG, "You did not pass extras or data with the Intent.");

            return;
        }

        // We default to GET if no verb was specified.
        final int verb = extras.getInt(HTTP_VERB, GET);
        Bundle params = extras.getParcelable(PARAMS);
        final ResultReceiver receiver = extras.getParcelable(RESULT_RECEIVER);
        Uri action = Uri.parse(extras.getString(URL));

        try {
            // Here we define our base request model which we will
            // send to our REST service via HttpClient.
            HttpRequestBase request = null;

            // Let's build our request based on the HTTP verb we were
            // given.
            switch (verb) {
                case GET: {
                    request = new HttpGet();
                    attachUriWithQuery(request, action, params);
                }
                break;

                case DELETE: {
                    request = new HttpDelete();
                    attachUriWithQuery(request, action, params);
                }
                break;

                case POST: {
                    request = new HttpPost();
                    request.setURI(new URI(action.toString()));

                    // Attach form entity if necessary. Note: some REST APIs
                    // require you to POST JSON. This is easy to do, simply use
                    // postRequest.setHeader('Content-Type', 'application/json')
                    // and StringEntity instead. Same thing for the PUT case
                    // below.
                    HttpPost postRequest = (HttpPost) request;

                    if (params != null) {
                        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(paramsToList(params));
                        postRequest.setEntity(formEntity);
                    }
                }
                break;

                case PUT: {
                    request = new HttpPut();
                    request.setURI(new URI(action.toString()));

                    // Attach form entity if necessary.
                    HttpPut putRequest = (HttpPut) request;

                    if (params != null) {
                        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(paramsToList(params));
                        putRequest.setEntity(formEntity);
                    }
                }
                break;
            }

            if (request != null) {

                // Let's send some useful debug information so we can monitor things
                // in LogCat.
                Log.d(TAG, "Executing request: "+ verbToString(verb) +": "+ action.toString());
                final HttpRequestBase finalRequest = request;
                // Finally, we send our request using HTTP. This is the synchronous
                // long operation that we need to run on this thread.
                threadPool.execute(new Runnable() {
                    public void run() {

                        try {
                            //finalRequest.addHeader("Accept", GlobalVariables.ACCEPT_HEADER);
                            HttpResponse response;
                            response = client.execute(finalRequest);

                            HttpEntity responseEntity = response.getEntity();
                            StatusLine responseStatus = response.getStatusLine();
                            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;

                            // Our ResultReceiver allows us to communicate back the results to the caller. This
                            // class has a method named send() that can send back a code and a Bundle
                            // of data. ResultReceiver and IntentService abstract away all the IPC code
                            // we would need to write to normally make this work.
                            if (responseEntity != null) {
                                Bundle resultData = new Bundle();
                                resultData.putString(REST_RESULT, EntityUtils.toString(responseEntity));
                                receiver.send(statusCode, resultData);
                            } else {
                                receiver.send(statusCode, null);
                            }
                        } catch (ClientProtocolException e) {
                            Log.e(TAG, "There was a problem when sending the request.", e);
                            receiver.send(0, null);
                        } catch (IOException e) {
                            Log.e(TAG, "There was a problem when sending the request.", e);
                            receiver.send(0, null);
                        }
                    }
                });
            }
        }
        catch (URISyntaxException e) {
            Log.e(TAG, "URI syntax was incorrect. "+ verbToString(verb) +": "+ action.toString(), e);
            receiver.send(0, null);
        }
        catch (UnsupportedEncodingException e) {
            Log.e(TAG, "A UrlEncodedFormEntity was created with an unsupported encoding.", e);
            receiver.send(0, null);
        } catch (IOException e) {
            Log.e(TAG, "URI syntax was incorrect. "+ verbToString(verb) +": "+ action.toString(), e);
            receiver.send(0, null);
        }

    }

    public static void attachUriWithQuery(HttpRequestBase request, Uri uri, Bundle params) {
        try {
            if (params == null) {
                // No params were given or they have already been
                // attached to the Uri.
                request.setURI(new URI(uri.toString()));
            }
            else {
                Uri.Builder uriBuilder = uri.buildUpon();

                // Loop through our params and append them to the Uri.
                for (BasicNameValuePair param : paramsToList(params)) {
                    uriBuilder.appendQueryParameter(param.getName(), param.getValue());
                }

                uri = uriBuilder.build();
                request.setURI(new URI(uri.toString()));
            }
        }
        catch (URISyntaxException e) {
            Log.e(TAG, "URI syntax was incorrect: "+ uri.toString(), e);
        }
    }

    private static List<BasicNameValuePair> paramsToList(Bundle params) {
        ArrayList<BasicNameValuePair> formList = new ArrayList<BasicNameValuePair>(params.size());

        for (String key : params.keySet()) {
            Object value = params.get(key);

            // We can only put Strings in a form entity, so we call the toString()
            // method to enforce. We also probably don't need to check for null here
            // but we do anyway because Bundle.get() can return null.
            if (value != null) formList.add(new BasicNameValuePair(key, value.toString()));
        }

        return formList;
    }

    private static String verbToString(int verb) {
        switch (verb) {
            case GET:
                return "GET";

            case POST:
                return "POST";

            case PUT:
                return "PUT";

            case DELETE:
                return "DELETE";
        }

        return "";
    }

    private HttpClient setUpHttpClient()
    {
        DefaultHttpClient ret = null;

        //SETS UP PARAMETERS
        HttpParams params = new BasicHttpParams();
        //REGISTERS SCHEMES FOR BOTH HTTP AND HTTPS
        SchemeRegistry registry = new SchemeRegistry();

        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, registry);
        ret = new DefaultHttpClient(manager, params);
        return ret;
    }
}
