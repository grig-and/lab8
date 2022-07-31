package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Telegram {
    private String token = "";
    private static long id = 491117935;
    private static long lastMsgId = 0;

    public void sendMessage(String msg) {
    }

    public String[] getCMD() {
        return null;
    }

    private String match(String str, String pt) {
        Pattern pat = Pattern.compile(pt);
        Matcher m = pat.matcher(str);
        String res = "";
        while (m.find()) {
            res = m.group(1);
        }
        return res;
    }
}
