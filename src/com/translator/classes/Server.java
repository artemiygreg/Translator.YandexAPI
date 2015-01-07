package com.translator.classes;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import jsonrpc.JSONRPCClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Created by Арем on 11.09.14.
 */
public class Server {
    private App app = App.getInstance();
    //private String host = "translate.yandex.net/api/v1.5/tr.json/translate";
    private String host;
    private Integer port;
    private String path = "/api/v1.5/tr.json/translate?key=trnsl.1.1.20140911T143053Z.518c1492b0f3cca1.aed11ef1146ff578b227f319b5ad24676e3d3ef2";
    private String protocol = "https://";
    private Boolean connect = false;
    private String method;
    private Boolean status_request;
    private JSONRPCClient client;
    private String request;
    private TextView tv_result;
    private JSONObject response = null;
    public String lang_translate;
    private ProgressDialog prog;


    public Server(){

    }

    public Boolean connections() throws Exception {
        try {
            URL url = new URL("yandex.ru");
            URLConnection connection = new URL(protocol, host, 80, "").openConnection();
            connection.setConnectTimeout(10000);
            connection.connect();
            connect = true;
            Log.e("connect", "succes");
        } catch (Exception e) {
            connect = false;
        }
        return connect;
    }
    public Boolean send(String text_translate, String lang_translate, List<NameValuePair> request_array, String host){
            this.host = host;
            request = protocol + host + path + "&text="+text_translate+"&lang=ru-en&format=plain&options=1";
            new AsyncTaskSend(text_translate, lang_translate, request_array).execute();

        return status_request;
    }
    private void queryData(List<NameValuePair> request_array, String host){
        try {
            Log.e("queryData", "host: " + host);
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(host);
            Log.e("nameValuePairs", request_array.toString());
            post.setEntity(new UrlEncodedFormEntity(request_array, "UTF-8"));
            HttpResponse httpResponse = client.execute(post);
            String responseString;
            HttpEntity responseEntity = httpResponse.getEntity();
            if(responseEntity != null) {
                responseString = EntityUtils.toString(responseEntity, "UTF-8");
                response = new JSONObject(responseString);
                Log.e("response", response.toString());
            }
        }
        catch (Exception e){
            Log.e("queryData", "Exception: " + e.getMessage());
        }
    }
    public Boolean translate_text(String text_translate, TextView tv_result, String lang_translate, ProgressDialog prog){
        this.tv_result = tv_result;
        this.lang_translate = lang_translate;
        this.prog = prog;
        this.method = "translate_text";

        List<NameValuePair> request_array = new ArrayList<NameValuePair>();
        request_array.add(new BasicNameValuePair("key", "trnsl.1.1.20140911T143053Z.518c1492b0f3cca1.aed11ef1146ff578b227f319b5ad24676e3d3ef2"));
        request_array.add(new BasicNameValuePair("text", text_translate));
        request_array.add(new BasicNameValuePair("lang", lang_translate));
        request_array.add(new BasicNameValuePair("format", "plain"));
        request_array.add(new BasicNameValuePair("options", "1"));
        Log.e("request_array", request_array.toString());

        return send(text_translate, lang_translate, request_array, "https://translate.yandex.net/api/v1.5/tr.json/translate");
    }
    public Boolean groupedTranslation(String text_translate, TextView tv_result, ProgressDialog prog){
            this.tv_result = tv_result;
            this.prog = prog;
            this.method = "groupedTranslation";

            List<NameValuePair> request_array = new ArrayList<NameValuePair>();
            request_array.add(new BasicNameValuePair("key", "dict.1.1.20140915T085821Z.deba93b6ed91b933.5cd6ee9e039a2d1407f8d48c9498d472567194e3"));
            request_array.add(new BasicNameValuePair("lang", "en-ru"));
            request_array.add(new BasicNameValuePair("text", text_translate));

        return send(text_translate, lang_translate, request_array, "https://dictionary.yandex.net/api/v1/dicservice.json/lookup");
    }
    public Boolean parseDate(JSONObject json, String method){
        try {
            Log.e("parseDate", "json: " + json.toString());
            Log.e("parseDate", "call");
            if(method.equals("translate_text")) {
                if(json.getInt("code") == 200) {
                    Log.e("Sukerka", json.getJSONArray("text").getString(0));
                    tv_result.setText(json.getJSONArray("text").getString(0));

                    return true;
                }
            }
            else if(method.equals("groupedTranslation")) {
                if(json.has("head")) {
                    for(int i = 0; i <= json.length(); i++) {
                        if(i == 0) {
                            tv_result.append("Примеры: \n");
                        }
                        tv_result.append(json.getJSONArray("def").getJSONObject(0).getJSONArray("tr").getJSONObject(0).getJSONArray("ex").getJSONObject(i).getJSONArray("tr").getJSONObject(0).getString("text") + " - ");
                        tv_result.append(json.getJSONArray("def").getJSONObject(0).getJSONArray("tr").getJSONObject(0).getJSONArray("ex").getJSONObject(i).getString("text") + "\n");
                    }
                    for(int i = 0; i <= json.length(); i++) {
                        if(i == 0) {
                            tv_result.append("\nЗначения: ");
                        }
                        tv_result.append(json.getJSONArray("def").getJSONObject(0).getJSONArray("tr").getJSONObject(0).getJSONArray("mean").getJSONObject(i).getString("text") + ", ");
                    }
                    /*for(int i = 0; i <= json.length(); i++) {
                        if(i == 0) {
                            tv_result.append("\nСинонимы: ");
                        }
                        tv_result.append(json.getJSONArray("def").getJSONObject(0).getJSONArray("tr").getJSONObject(0).getJSONArray("syn").getJSONObject(i).getString("text") + ", ");
                    }*/
                }
                return true;
            }
        } catch (JSONException e) {
            Log.e("parseDate", "Exception: " + e.getMessage());
        }
        return false;
    }
    class AsyncTaskSend extends AsyncTask<Void, Void, Boolean> {
        String text_translate;
        String obj;
        String lang_translate;
        List<NameValuePair> request_array;

        public AsyncTaskSend(String text_translate, String lang_translate, List<NameValuePair> request_array){
            this.text_translate = text_translate;
            this.lang_translate = lang_translate;
            this.request_array = request_array;
        }
        @Override
        protected void onPreExecute(){
            prog.show();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Log.e("AsyncTask","run");
                //if(connections()) {
                    queryData(request_array, host);
                    return true;
                //}
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            Log.e("onPost", "run");
            //super.onPostExecute(result);
            if(result != null) {
                Log.e("result", result.toString());
                if(result) {
                    //app.Alert("OnPost", "Succes");
                    status_request = parseDate(response, method);
                    prog.cancel();
                }
            }
        }
    }
}
