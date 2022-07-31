package commands;

import exceptions.UserNotFoundException;
import util.Auth;
import util.DataBase;
import util.Request;
import util.Response;

/**
 * Command info class
 */
public class Register implements Commandable {
    final public static String description = null;

    @Override
    public Response run(Request req) {
        return new Response(Auth.register(req.getLogin(), req.getPassword()));
    }

    public String getDescription() {
        return description;
    }

}
