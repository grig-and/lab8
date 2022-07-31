package commands;

import content.Movie;
import exceptions.InvalidArgumentException;
import exceptions.UserNotFoundException;
import util.Auth;
import util.Request;
import util.Response;

import java.util.Map;

/**
 * Command help class
 */
public class Help implements Commandable {
    final public static String description = "вывести справку по доступным командам";
    final public static String name = "help";

    /**
     * commands list
     */
    private Map<String, Commandable> commands;

    /**
     * Constructor of help command
     *
     * @param commands commands list
     */
    public Help(Map<String, Commandable> commands) {
        this.commands = commands;
    }


    @Override
    public Response run(Request req) throws InvalidArgumentException {
        switch (Auth.checkRequest(req)){
            case 404:
                return new Response(404);
            case 403:
                return new Response(403);
        }
        String resp = "";
        for (String key : commands.keySet()) {
            resp += key + ": " + commands.get(key).getDescription() + "\n";
        }
        return new Response(resp);
    }

    public String getDescription() {
        return description;
    }
}
