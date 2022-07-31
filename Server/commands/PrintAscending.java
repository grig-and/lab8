package commands;

import content.Movie;
import exceptions.UserNotFoundException;
import util.Auth;
import util.CollectionManager;
import util.Request;
import util.Response;

/**
 * Command print_ascending class
 */
public class PrintAscending implements Commandable {
    CollectionManager collection;
    final public static String description = "вывести элементы коллекции в порядке возрастания";

    /**
     * Constructor of print_ascending command
     *
     * @param collection CollectionManager instance
     */
    public PrintAscending(CollectionManager collection) {
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
        return new Response(collection.getAscending());
    }

    public String getDescription() {
        return description;
    }
}
