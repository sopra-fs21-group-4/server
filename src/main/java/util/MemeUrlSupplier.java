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


    private static class TestVersion extends MemeUrlSupplier {

        private TestVersion() {

            baseURL = "https://homepages.cae.wisc.edu/~ece533/images/";
            String[] images = { "airplane.png", "baboon.png", "barbara.bmp", "boat.png",
                    "cat.png", "fruits.png", "frymire.png", "girl.png", "goldhill.bmp",
                    "lena.bmp", "monarch.png", "mountain.png", "peppers.png", "pool.png",
                    "sails.png", "serrano.png", "tulips.png", "watch.png", "zelda.png"
            };
            for (String image : images) memeQueue.add(baseURL+image);
        }
    }

    /**
     * doesn't work
     */
    private static class XMLCrawler extends MemeUrlSupplier {
        private XMLCrawler(String subreddit, String config) {
            String urlString = String.format("http://www.reddit.com/r/%s%s.xml", subreddit, config);
            String xml = null;


            try {

                URL url = new URL(urlString);
                URLConnection conn = url.openConnection();

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(conn.getInputStream());
                xml = doc.toString();
            } catch (Exception e) {
                e.printStackTrace();

                memeQueue.add("https://img.memecdn.com/pewdiepie-404_o_1938429.jpg");
                return;
            }

            int index;
            while ((index = xml.indexOf("src=&quot;")) > 0) {
                xml = xml.substring(index+10);
                String src = xml.substring(0, xml.indexOf("&quot;"));
                src = src.replaceAll("amp;", "");
                memeQueue.add(src);
            }
        }

    }


    private static class redditURLs extends MemeUrlSupplier{
        private redditURLs(String subreddit, String memeType){

            String url = "https://reddit.com/r/"+subreddit+"/"+memeType+".json?sort="+memeType+"&limit=100";

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Agent", "Mozilla/5.0:MyownApp (by /u/Yakumani)"); // TODO

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
