package commands;

import util.Request;

/**
 * Command exit class
 */
public class Exit extends Commandable {
    final public static String name = "exit";
    final public static String description = "завершить программу";

    @Override
    public Request getRequest(String arg) {
        return new Request("exit");
    }

    public String getDescription() {
        return description;
    }
}
