package xyz.nyist.utils;

import java.util.Random;

/**
 * @Author : fucong
 * @Date: 2021-03-30 15:30
 * @Description :
 */
public class CimUtil {

    private static final String STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String getRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(STR.charAt(number));
        }
        return sb.toString();
    }
}
