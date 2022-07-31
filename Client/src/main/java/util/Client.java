package util;

import content.Movie;
import controllers.Main;
import exceptions.NetException;
import util.CommandManager;
import util.IO;
import util.NetManager;
import util.Request;

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

/**
 * Main class
 */
public class Client {
    public static void start() {
        IO io = new IO();
        NetManager net = null;
        try {
//            net = new NetManager(InetAddress.getLoopbackAddress(), Integer.parseInt(args[0]));
            net = new NetManager(InetAddress.getLoopbackAddress(), 33508);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            IO.error("Неверно указан порт");
            System.exit(1);
        }


        NetManager finalNet = net;
//        new Thread(() -> {
            if (!finalNet.connect()) {
                IO.error("Сервер не отвечает");
                while (!finalNet.connect()) {
                    finalNet.connect();
                }

            }


    }

    private static void resend(Request res, NetManager net) {
//        System.out.println("Попытка переподключения");
        while (!net.connect()) {
        }
        try {
//            System.out.println("Повторная отправка");
//            System.out.println(net.exchangeString(res));
            net.exchangeString(res);
        } catch (NetException | ClassNotFoundException | IOException ex) {
            IO.error("Ошибка при повторной отправке");
        }
    }

}
