package com.javabaas.shell.util;

import com.javabaas.javasdk.JBUtils;
import jline.console.ConsoleReader;
import org.fusesource.jansi.Ansi;

import java.util.List;

/**
 * Created by zangyilin on 2017/9/28.
 */
public class ASKUtil {
    public static int askNumber(List<String> values, String message, int defalut) {
        try {
            String input = ask(values, message, defalut);
            if (JBUtils.isEmpty(input)) {
                return defalut;
            }
            int result = Integer.parseInt(input);
            return result;
        } catch (Exception e) {
            System.out.println("输入不正确，请重试");
            return askNumber(values, message, defalut);
        }

    }

    private static String ask(List<String> values, String message, int defalut) {
        try {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(message));
            for (int i = 1; i <= values.size(); i ++) {
                if (defalut == i) {
                    System.out.println(Ansi.ansi().fg(Ansi.Color.YELLOW).a(" " + i + " : " + values.get(i - 1)));
                } else {
                    System.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a(" " + i + " : " + values.get(i - 1)));
                }
            }
            ConsoleReader consolereader = new ConsoleReader();
            return consolereader.readLine(" " + ">");
        } catch (Exception e) {
            return null;
        }

    }

}
