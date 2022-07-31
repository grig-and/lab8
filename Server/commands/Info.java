package commands;

import content.Movie;
import exceptions.UserNotFoundException;
import util.Auth;
import util.CollectionManager;
import util.Request;
import util.Response;

/**
 * Command info class
 */
public class Info implements Commandable {
    CollectionManager collection;
    final public static String description = "вывести в стандартный поток вывода информацию о коллекции";

    /**
     * Constructor of info command
     *
     * @param collection CollectionManager instance
     */
    public Info(CollectionManager collection) {
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
        return new Response(collection.getInfo(), 200);
    }

    public String getDescription() {
        return description;
    }
}
