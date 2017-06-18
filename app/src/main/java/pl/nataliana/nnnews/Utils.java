package pl.nataliana.nnnews;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static pl.nataliana.nnnews.MainActivity.LOG_TAG;


public class Utils {

    public Utils() {
    }

    public static List<Info> fetchNewsData(String requestUrl){

        //Create the URL of the requestURL by the createURL method
        URL url = createUrl(requestUrl);

        String jsonResponse = null;

        try {

            //Try to create the HTTP request with the request URL
            jsonResponse = makeHttpRequest(url);
        }catch(IOException e){

            //If it fails, print the error to the log
            Log.e(LOG_TAG, "HTTP request failure", e);
        }

        //Create a list of the results by the extractDataFromJson method
        List<Info> results = extractDataFromJson(jsonResponse);

        //Return the List of News objects
        return results;
    }

    private static List<Info> extractDataFromJson(String jsonResponse){

        //If the response is empty, return early
        if (TextUtils.isEmpty(jsonResponse)){
            return null;
        }

        //Create a new list for the news
        List<Info> news = new ArrayList<>();

        try {

            //Try to the create a new JSONObject from the response and search for the result list of news
            JSONObject response = new JSONObject(jsonResponse);
            JSONObject newsResponse = response.getJSONObject("response");
            JSONArray newsArray = newsResponse.getJSONArray("results");

            //Loop through the results
            for (int i = 0; i < newsArray.length(); i++) {

                //Get the title, section, url and date information of the current news
                JSONObject currentNews = newsArray.getJSONObject(i);
                String title = currentNews.getString("webTitle");
                String section = currentNews.getString("sectionName");
                String url = currentNews.getString("webUrl");
                String rawDate = currentNews.getString("webPublicationDate");
                String date = rawDate.substring(0, 10);

                //Create a new Info object from the data and add it to the list
                news.add(new Info(title, section, url, date));

            }

        }catch (JSONException e){

            //If it fails, print the error to the log
            Log.e(LOG_TAG, "Fetching data from JSON failed", e);
        }

        return news;

    }

    private static String makeHttpRequest(URL requestUrl) throws IOException {

        String jsonResponse = "";

        //If there is no request URL, return early
        if (requestUrl == null) {
            return jsonResponse;
        }

        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {

            //Try to make an HTTP connection with the request URL
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("GET");

            //Send the request
            connection.connect();

            if (connection.getResponseCode() == 200){
                inputStream = connection.getInputStream();
                jsonResponse = readDataFromStream(inputStream);
            }
            else {

                Log.e(LOG_TAG, "Server response error: " + connection.getResponseCode());
            }
        } catch (IOException e) {

            Log.e(LOG_TAG, "Connection failed", e);
        }finally {

            //Disconnect the connection if it isn't disconnected yet
            if (connection != null){
                connection.disconnect();
            }

            //Close the InputStream if it isn't closed yet
            if (inputStream != null){
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    private static String readDataFromStream(InputStream inputStream) throws IOException{

        //Creates a new StringBuilder
        StringBuilder output = new StringBuilder();

        if (inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line = bufferedReader.readLine();
            while (line != null){
                output.append(line);
                line = bufferedReader.readLine();
            }
        }

        return output.toString();
    }

    private static URL createUrl(String urlString) {

        URL url = null;

        //Try to create a valid URL from the String
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {

            //If it fails, print the error to the log
            Log.e(LOG_TAG, "URL creation failed", e);
        }

        return url;
    }

}