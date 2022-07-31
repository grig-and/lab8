package commands;

import exceptions.UserNotFoundException;
import util.*;

/**
 * Command info class
 */
public class ClearHistory implements Commandable {
    final public static String description = "";

    @Override
    public Response run(Request req) {
        switch (Auth.checkRequest(req)){
            case 404:
                return new Response(404);
            case 403:
                return new Response(403);
        }
        boolean ch = DataBase.getInstance().clearHistory();
        boolean cr = DataBase.getInstance().clearRecPoints();
        if (ch && cr) {
            return new Response(200);
        } else {
            return new Response(404);
        }
    }

    public String getDescription() {
        return description;
    }
}
