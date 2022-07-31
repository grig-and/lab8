package commands;

import content.Movie;
import exceptions.InvalidArgumentException;
import exceptions.UserNotFoundException;
import util.Auth;
import util.CollectionManager;
import util.Request;
import util.Response;

/**
 * Command remove_greater_key class
 */
public class RemoveGreaterKey implements Commandable {
    CollectionManager collection;
    final public static String description = "удалить из коллекции все элементы, ключ которых превышает заданный";

    /**
     * Constructor of remove_greater_key command
     *
     * @param collection CollectionManager instance
     */
    public RemoveGreaterKey(CollectionManager collection) {
        this.collection = collection;
    }


    @Override
    public Response run(Request req) throws InvalidArgumentException {
        if (req.getArg() == null){
            throw new InvalidArgumentException("Эта команда требует аргумент: ключ элемента коллекции");
        }
        switch (Auth.checkRequest(req)){
            case 404:
                return new Response(404);
            case 403:
                return new Response(403);
        }
        return new Response("" + collection.removeGreaterKey(req.getArg(), req.getLogin()), 200);
    }

    public String getDescription() {
        return description;
    }
}
