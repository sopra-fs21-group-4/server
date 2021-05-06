package ch.uzh.ifi.hase.soprafs21.service;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import ch.uzh.ifi.hase.soprafs21.constant.MemeType;

public class RedditOAuth {

    /**
     * Gets all memes and from reddit via get and formats them accrodingly
     * 
     * @param subreddit
     * @param memeType
     * @return memeList
     * @throws IOException
     */
    public String[] getMemes(String subreddit, MemeType memeType) throws IOException {
        if (!isValidSubreddit(subreddit)) {
            String[] reee = { "You entered the wrong subbreddit." };
            return reee;
        }
        // Getting the data from reddit with predefined params as "json"
        String url = "http://www.reddit.com/r/" + subreddit + "/" + memeType.name() + ".json";
        String json = (String) get(url, memeType.toString());

        // parsing the data collected
        try {
            // we got a random page back dont need that in my life
            if (json.startsWith("<")) {
                return null;
            }
            int index = 0;
            int buffer = 0;
            ArrayList<Integer> startList = new ArrayList<Integer>();
            while (buffer != -1) {
                buffer = json.indexOf("url", index);
                if (buffer != -1) {
                    startList.add(buffer + 9);
                    index = buffer + 1;
                }
            }
            String[] memeList = new String[startList.size()];
            for (int i = 0; i < startList.size(); i++) {
                memeList[i] = json.substring(startList.get(i), json.indexOf(",", startList.get(i)));
                memeList[i] = memeList[i].replaceAll("\\", "");
                memeList[i] = memeList[i].replaceAll("\"", "");
                memeList[i] = memeList[i].replaceAll(",", "");
                memeList[i] = memeList[i].replaceAll(" ", "");
            }
            return memeList;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    /**
     * checks if subbredit is according to reddit rules
     * 
     * @param subreddit
     * @return
     */
    public boolean isValidSubreddit(String subreddit) {
        return subreddit.length() <= 22 && subreddit.length() >= 0 && !subreddit.matches("^.*[^a-zA-Z0-9\\+ ].*$");
    }

    /**
     * actually collects data from reddit by using params given and returns them as
     * a string
     * 
     * @param request       url of subreddit
     * @param urlParameters memeType
     * @return response
     * @throws IOException
     */
    private String get(String request, String urlParameters) throws IOException {
        request = request + "?" + urlParameters;
        URL url = new URL(request);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "text/plain");
        connection.setRequestProperty("charset", "utf-8");
        connection.connect();

        StringBuilder response = new StringBuilder();
        String line;
        BufferedReader reader;

        if (connection.getResponseCode() == 200) {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        connection.disconnect();
        return response.toString();
    }

}