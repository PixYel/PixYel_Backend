/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.connection;

/**
 *
 * @author Administrator
 */
public class LogTest {

    public static void main(String[] args) {
        test("bla");
        test("blafrg");
        test("blargewrgf");
        test("blfa");
    }
    static int maxLengthOfClassName = 0;

    public static void test(String className) {
        int currentLengthOfClassName = className.length();
        String result = "";
        if ((currentLengthOfClassName = className.length()) > maxLengthOfClassName) {
            maxLengthOfClassName = currentLengthOfClassName;
        }
        for (int i = 0; i < (maxLengthOfClassName - currentLengthOfClassName) / 2; i++) {
            result += (" ");
        }
        result += ("[" + className + "]");
        if (maxLengthOfClassName % 2 == 0) {
            for (int i = 0; i < (maxLengthOfClassName - currentLengthOfClassName) / 2; i++) {
                result += (" ");
            }
        } else {
            for (int i = 0; i < ((maxLengthOfClassName - currentLengthOfClassName) / 2) + 1; i++) {
                result += (" ");
            }
        }
        System.out.println(result);
    }
}
