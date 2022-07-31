package util;

import controllers.Main;
import exceptions.NetException;
import javafx.scene.control.Alert;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class NetManager {
    private int port;
    InetAddress host;
    public Socket sock;
    public static OutputStream os;
    public static InputStream is;


    private static NetManager net;

    public NetManager(InetAddress host, int port) {
        this.host = host;
        this.port = port;
        this.net = this;
    }

    public static NetManager get() {
        return net;
    }

    public boolean connect() {
        try {
            sock = new Socket(host, port);
            os = sock.getOutputStream();
            is = sock.getInputStream();
            IO.green("Соединение установлено");
            return true;
        } catch (ConnectException e) {
            return false;
        } catch (IllegalArgumentException e) {
            IO.error("Неверно указан порт");
            System.exit(1);
            return false;
        } catch (IOException | NullPointerException e) {
            return false;
        }
    }

    public static void send(Request req) throws NetException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(req);
        try {
            os.write(baos.toByteArray());
        } catch (SocketException e) {
//            net.connect();
            throw new NetException("Ошибка подключения: " + e.getLocalizedMessage());

        } catch (IOException e) {
            throw new NetException("Ошибка IO: " + e.getLocalizedMessage());
        }
    }

    public static Response read() throws ClassNotFoundException {
        try {
            ByteBuffer buf = ByteBuffer.allocate(1024 * 128);
            is.read(buf.array());
            ByteArrayInputStream bais = new ByteArrayInputStream(buf.array());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (Response) ois.readObject();
        } catch (IOException e) {
            IO.error("Ошибка чтения");
        }
        return null;
    }


    public void close() throws IOException {
        os.close();
        is.close();
        sock.close();
    }

    public String exchangeString(Request res) throws NetException, IOException, ClassNotFoundException {
        send(res);
        String msg = ((Response) read()).getMsg();
        return msg;
    }

    public static Response exchange(Request res) {
        try {
            send(res);
            Response resp = (Response) read();
            return resp;
        } catch (NetException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Response exchange(Request res, Runnable cb) {
        try {
            send(res);
            Response resp = (Response) read();
            cb.run();
            return resp;
        } catch (NetException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void asyncExchange(Request res, Consumer<Response> cb) {
        new Thread(() -> {
        try {
            send(res);
            Response resp = (Response) read();
            cb.accept(resp);
        } catch (NetException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        }).start();
    }

    public static void asyncExchange(Request res) {
        asyncExchange(res, (code) -> {
        });
    }

    public static int checkPwd(String login, String pwd) throws NetException, IOException, ClassNotFoundException {
        Auth.setLogin(login);
        Auth.setPwd(pwd);
        Request req = new Request("login");
        Response resp = net.exchange(req);
        int code = resp.getCode();
        if (code == 200) {
            Auth.setLogin(resp.getMsg());
        }
        return code;
    }

    public static boolean register(String login, String pwd) {
        if (!isUserExist(login)) {
            if (addUser(login, pwd)) {
                Auth.setLogin(login);
                Auth.setPwd(pwd);
                return true;
            }
            ;
        }
        return false;
    }

    private String getPwd() {
        IO.out("Введите пароль: ");
        String pwd = IO.nextLine().trim();
        if (pwd.length() >= 8) {
            return pwd;
        } else {
            IO.error("В пароле должно быть от 8 символов");
            getPwd();
        }
        return null;
    }

    public static boolean addUser(String login, String pwd) {
        Request req = new Request("register");
        req.setLogin(login);
        req.setPassword(pwd);
        return net.exchange(req).getCode() == 201;
    }


    public static boolean isUserExist(String login) {
        return net.exchange(new Request("check_user", login)).getCode() == 200;
    }
}
