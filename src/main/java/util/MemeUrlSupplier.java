package util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;


/**
 * MemeUrlSuppliers produce urls of memes.
 * this is basically the interface with the reddit API.
 */
public class MemeUrlSupplier implements Supplier<String> {

    public static MemeUrlSupplier create(String baseURL, String config) {
        return new redditURLs(baseURL, config);
    }

    protected String baseURL;
    protected LinkedList<String> memeQueue = new LinkedList<>();

    @Override
    public String get() {
        String next = memeQueue.pop();
        memeQueue.addLast(next);
        return next;
    }

    public List<String> getMemeList() {
        return new ArrayList<String>(memeQueue);
    }



    private static class redditURLs extends MemeUrlSupplier{
        private redditURLs(String subreddit, String memeType){

            String url = "https://reddit.com/r/"+subreddit+"/"+memeType+".json?sort="+memeType+"&limit=100";

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Agent", "Mozilla/5.0:DoYouEvenMeme? (by /u/TestUser)");

            HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);


            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse(response.getBody());
            JsonObject data1 = jsonObject.get("data").getAsJsonObject();
            JsonArray children = data1.get("children").getAsJsonArray();

            for(JsonElement child : children){
                JsonObject data2 = child.getAsJsonObject().get("data").getAsJsonObject();
                String memeUrl = data2.get("url").getAsString();
                if(memeUrl.endsWith(".jpg") || memeUrl.endsWith(".png") || memeUrl.endsWith(".gif")){
                    memeQueue.add(memeUrl);
                }
            }

        }


    }

}
