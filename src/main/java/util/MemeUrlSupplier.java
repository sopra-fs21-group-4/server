package util;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.function.Supplier;


/**
 * TODO dead class
 * MemeUrlSuppliers produce urls of memes.
 * this is basically the interface with the reddit API.
 */
public class MemeUrlSupplier implements Supplier<String> {

    public static MemeUrlSupplier create(String baseURL, String config) {
        return new TestVersion();
    }

    protected String baseURL;
    protected LinkedList<String> memeQueue = new LinkedList<>();

    @Override
    public String get() {
        String next = memeQueue.pop();
        memeQueue.addLast(next);
        return next;
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

}
