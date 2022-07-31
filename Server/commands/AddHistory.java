package commands;

import exceptions.UserNotFoundException;
import util.*;

/**
 * Command info class
 */
public class AddHistory implements Commandable {
    final public static String description = "";
    @Override
    public Response run(Request req) {
        switch (Auth.checkRequest(req)){
            case 404:
                return new Response(404);
            case 403:
                return new Response(403);
        }
        if (DataBase.getInstance().addHistory(req.getArg())){
            return new Response(201);
        } else {
            return new Response(500);
        }

    }

    public String getDescription() {
        return description;
    }
}
