package sonar.view;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ImageUtil {
    private static Logger logger = Logger.getLogger(ImageUtil.class.getName());
    private static final String IMAGE_DIR = "image/";

    private static Map<String, Image> cache = new HashMap<>();

    public static Image get(String name, Integer w, Integer h){
        return get(name, w, h, name);
    }

    public static Image get(String filename, Integer w, Integer h, String key){
        if (cache.containsKey(key))
            return cache.get(key);
        try {
            Image bi = ImageIO.read(ImageUtil.class.getClassLoader().getResourceAsStream(IMAGE_DIR + filename));
            if (w != null || h != null){
                int iw = bi.getWidth(null);
                int ih = bi.getHeight(null);
                if (h == null){
                    double scale = ((double) w) / ((double) iw);
                    h = (int) (ih * scale);
                }
                else if (w == null){
                    double scale = h / ih;
                    w = (int) (iw * scale);
                }
                bi = bi.getScaledInstance(w, h, BufferedImage.SCALE_SMOOTH);
            }
            cache.put(key, bi);
            return bi;
        }
        catch (Exception e){
            logger.severe("Unable to load image '" + IMAGE_DIR + filename + "': " + e);
        }
        return null;
    }

    public static Image get(String name){
        return get(name, null, null, name);
    }

    public static Image get(String name, Integer w){
        return get(name, w, null, name);
    }

    public static Image get(String name, Integer w, String key){
        return get(name, w, null, key);
    }

    public static Image get(String filename, String key){
        return get(filename, null, null, key);
    }

    private ImageUtil(){}
}
