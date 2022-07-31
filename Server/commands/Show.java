package commands;

import content.Movie;
import exceptions.UserNotFoundException;
import util.Auth;
import util.CollectionManager;
import util.Request;
import util.Response;

/**
 * Command show class
 */
public class Show implements Commandable {
    CollectionManager collection;
    final public static String description = "вывести в стандартный поток вывода все элементы коллекции в строковом представлении";

    /**
     * Constructor of show command
     *
     * @param collection CollectionManager instance
     */
    public Show(CollectionManager collection) {
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
        return new Response(collection.getAll());
    }

    public String getDescription() {
        return description;
    }
}
