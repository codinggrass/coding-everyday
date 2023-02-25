package com.codinggrass.learn.java.exception;

/**
 * Learn Java from https://www.liaoxuefeng.com/
 *
 * @author liaoxuefeng
 */
public class Main {

    public static void main(String[] args) {
        String a = "12";
        String b = "x9";
        // TODO: 捕获异常并处理
        try {
            int c = stringToInt(a);
            int d = stringToInt(b);
            System.out.println(c * d);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    static int stringToInt(String s) throws NumberFormatException {
        return Integer.parseInt(s);
    }
}
