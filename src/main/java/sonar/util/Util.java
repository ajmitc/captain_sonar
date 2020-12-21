package sonar.util;

import java.util.Date;
import java.util.Random;

public class Util {
    private static Random GEN = new Random(new Date().getTime());

    public static int getRandomInt(int min, int max){
        return min + GEN.nextInt(max - min);
    }

    private Util(){}
}
