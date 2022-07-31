package commands;

import content.Movie;
import exceptions.InvalidArgumentException;
import exceptions.UserNotFoundException;
import util.Auth;
import util.CollectionManager;
import util.Request;
import util.Response;

/**
 * Command remove_greater class
 */
public class    RemoveGreater implements Commandable {
    CollectionManager collection;
    final public static String description = "удалить из коллекции все элементы, превышающие заданный";

    /**
     * Constructor of remove_greater command
     *
     * @param collection CollectionManager instance
     */
    public RemoveGreater(CollectionManager collection) {
        this.collection = collection;
    }


    @Override
    public Response run(Request req) throws InvalidArgumentException {
        switch (Auth.checkRequest(req)) {
            case 404:
                return new Response(404);
            case 403:
                return new Response(403);
        }
        try {
            return new Response("" + collection.removeGreater(Integer.parseInt(req.getArg()), req.getLogin()), 200);
        } catch (NumberFormatException e) {
            return new Response(400);
        }

    }

    public String getDescription() {
        return description;
    }
}
