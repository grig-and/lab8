package commands;

import exceptions.UserNotFoundException;
import util.*;

/**
 * Command info class
 */
public class GetHistory implements Commandable {
    final public static String description = "";
    @Override
    public Response run(Request req) {
        switch (Auth.checkRequest(req)){
            case 404:
                return new Response(404);
            case 403:
                return new Response(403);
        }

        return new Response(DataBase.getInstance().getHistory());
    }

    public String getDescription() {
        return description;
    }
}
