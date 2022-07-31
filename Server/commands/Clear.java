package commands;

import content.Movie;
import exceptions.UserNotFoundException;
import util.Auth;
import util.CollectionManager;
import util.Request;
import util.Response;

/**
 * Command clear class
 */
public class Clear implements Commandable {
    CollectionManager collection;
    final public static String description = "очистить коллекцию";

    /**
     * Constructor of clear command
     *
     * @param collection CollectionManager instance
     */
    public Clear(CollectionManager collection) {
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
        return new Response(collection.clear(req.getLogin()));
    }

    public String getDescription() {
        return description;
    }
}
