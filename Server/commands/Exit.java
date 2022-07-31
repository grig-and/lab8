package commands;

import content.Movie;
import exceptions.UserNotFoundException;
import util.Auth;
import util.CollectionManager;
import util.Request;
import util.Response;

/**
 * Command exit class
 */
public class Exit implements Commandable {
    CollectionManager collection;
    final public static String description = "завершить программу";

    public Exit(CollectionManager collection) {
        this.collection = collection;
    }

    @Override
    public Response run(Request req) {
        switch (Auth.checkRequest(req)){
            case 404:
                return new Response(404);
            case 403:
                return new Response(403);
        }
        return null;
    }

    public String getDescription() {
        return description;
    }
}
