package util;

import java.util.LinkedList;
import java.util.function.Supplier;


/**
 * MemeUrlSuppliers produce urls of memes.
 * this is basically the interface with the reddit API.
 */
public class MemeUrlSupplier implements Supplier<String> {

    public static MemeUrlSupplier create(String baseURL, String config) {
        // TODO
        return new TestVersion();
    }

    protected String baseURL;
    protected LinkedList<String> memeQueue = new LinkedList<>();

    @Override
    public String get() {
        return baseURL + memeQueue.pop();
    }


    private static class TestVersion extends MemeUrlSupplier {

        private TestVersion() {
            baseURL = "https://homepages.cae.wisc.edu/~ece533/images/";
            String[] images = { "airplane.png", "baboon.png", "barbara.bmp", "boat.png",
                    "cat.png", "fruits.png", "frymire.png", "girl.png", "goldhill.bmp",
                    "lena.bmp", "monarch.png", "mountain.png", "peppers.png", "pool.png",
                    "sails.png", "serrano.png", "tulips.png", "watch.png", "zelda.png"
            };
            for (String image : images) memeQueue.add(image);
        }
    }

}
