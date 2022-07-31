package commands;

import exceptions.UserNotFoundException;
import util.Auth;
import util.DataBase;
import util.Request;
import util.Response;

/**
 * Command info class
 */
public class Login implements Commandable {
    final public static String description = null;

    @Override
    public Response run(Request req) {
        int code = Auth.checkRequest(req);
        if (code == 200) {
            return new Response(Auth.getID(), 200);
        } else {
            return new Response(code);
        }
    }

    public String getDescription() {
        return description;
    }

}
