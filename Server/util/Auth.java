package util;

import exceptions.UserNotFoundException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Auth {
    private static DataBase db = DataBase.getInstance();
    private static final String pepper = "q1*]gW9@,7";
    private static String id = "";
    private static String VK_SECRET = "";
    private static String VK_TOKEN = "";
    private static String OK_APP_KEY = "";
    private static String OK_SIGN = "";

    public static int checkRequest(Request req) {
        return login(req.getLogin(), req.getPassword());
    }

    private static void getTokens(){
        try {
            Scanner sc = new Scanner(new FileReader("./vk_ok.txt"));
            VK_SECRET = sc.nextLine().trim();
            VK_TOKEN = sc.nextLine().trim();
            OK_APP_KEY = sc.nextLine().trim();
            OK_SIGN = sc.nextLine().trim();
        } catch (FileNotFoundException | NoSuchElementException e) {
            System.out.println("VK/OK token file error");
        }
    }

    public static int login(String username, String password) {
        getTokens();
        System.out.println("login: " + username);
        id = username;
        if (username.indexOf("vk_") == 0) {
            return checkVK(password);
        } else if (username.indexOf("ok_") == 0) {
            return checkOK(password);
        }
        User user = db.getUser(username);
        if (user == null) {
            return 404;
        }
        String hash = hash(password, user.getSalt());
        return hash.equals(user.getHash()) ? 200 : 403;
    }

    public static int register(String username, String password) {
        if (db.getUser(username) != null) {
            return 409;
        }
        String salt = generateSalt();
        return db.addUser(username, hash(password, salt), salt);
    }

    private static String generateSalt() {
        String salt = "";
        for (int i = 0; i < 10; i++) {
            salt += (char) (Math.random() * 26 + 97);
        }
        return salt;
    }

    public static String hash(String password, String salt) {
        String hash = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-384");
            byte[] digest = md.digest((pepper + password + salt).getBytes());

            String hex = "";
            for (int i = 0; i < digest.length; i++) {
                hash += Integer.toHexString(digest[i] & 0xff);
            }
            return hash;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }

    public static int checkVK(String token) {
//        String VK_API_URL = "https://api.vk.com/method/secure.checkToken?token=" + token + "&client_secret=ASoPRmtjVkL7FBQ97Ytc&access_token=b4a27399b4a27399b4a27399f4b4dfe517bb4a2b4a27399d641639897916c964a092a4a&v=5.131";
        String VK_API_URL = "https://api.vk.com/method/secure.checkToken?token=" + token + "&client_secret=" + VK_SECRET + "&access_token=" + VK_TOKEN + "&v=5.131";

        try {
            URL obj = new URL(VK_API_URL);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            String resp = response.toString();
            Pattern pat = Pattern.compile("\"user_id\":(\\d+)");
            Matcher m = pat.matcher(resp);

            m.find();
            String id = m.group(1);
            System.out.println(id);
            Auth.id = "vk_" + id;
            register(Auth.id, token);
            return 200;
        } catch (IOException e) {
            e.printStackTrace();
            return 500;
        }
    }

    public static int checkOK(String token) {
//        String OK_API_URL = "https://api.ok.ru/fb.do?application_key=CFNPFJKGDIHBABABA&format=json&method=users.getCurrentUser&sig=57208e1b95ef46798c1fa062bb992c92&access_token=" + token;
        String OK_API_URL = "https://api.ok.ru/fb.do?application_key=" + OK_APP_KEY + "&format=json&method=users.getCurrentUser&sig=" + OK_SIGN + "&access_token=" + token;
        try {
            URL obj = new URL(OK_API_URL);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            String resp = response.toString();
            Pattern pat = Pattern.compile("\"uid\":\"(\\d+)");
            Matcher m = pat.matcher(resp);

            m.find();
            String id = m.group(1);
            System.out.println(id);
            Auth.id = "ok_" + id;
            register(Auth.id, token);
            return 200;
        } catch (IOException e) {
            e.printStackTrace();
            return 500;
        }
    }

    public static String getID() {
        return id;
    }
}
