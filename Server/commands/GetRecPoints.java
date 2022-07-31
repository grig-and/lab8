package commands;

import exceptions.UserNotFoundException;
import util.*;

/**
 * Command info class
 */
public class GetRecPoints implements Commandable {
    final public static String description = "";
    @Override
    public Response run(Request req) {
        switch (Auth.checkRequest(req)){
            case 404:
                return new Response(404);
            case 403:
                return new Response(403);
        }
        return new Response(DataBase.getInstance().getRecPoints());
    }

    public String getDescription() {
        return description;
    }
}
