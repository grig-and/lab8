package commands;

import content.Movie;
import exceptions.InvalidArgumentException;
import exceptions.UserNotFoundException;
import util.Auth;
import util.CollectionManager;
import util.Request;
import util.Response;

/**
 * Command remove_key class
 */
public class RemoveKey implements Commandable {
    CollectionManager collection;
    final public static String description = "удалить элемент из коллекции по его ключу";

    /**
     * Constructor of remove_key command
     *
     * @param collection CollectionManager instance
     */
    public RemoveKey(CollectionManager collection) {
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
        if (!collection.contains(req.getArg())) {
            throw new InvalidArgumentException("Элемента с таким ключом не существует");
        }
        return new Response(collection.removeKey(req.getArg(), req.getLogin()));
    }

    public String getDescription() {
        return description;
    }
}
