package commands;

import content.Movie;
import exceptions.InvalidArgumentException;
import exceptions.UserNotFoundException;
import util.Auth;
import util.CollectionManager;
import util.Request;
import util.Response;

/**
 * Command replace_if_greater class
 */
public class ReplaceIfGreater implements Commandable {
    CollectionManager collection;

    final public static String description = "заменить значение по ключу, если новое значение больше старого";

    /**
     * Constructor of replace_if_greater command
     *
     * @param collection CollectionManager instance
     */
    public ReplaceIfGreater(CollectionManager collection) {
        this.collection = collection;
    }

    @Override
    public Response run(Request req) throws InvalidArgumentException {

        if (req.getArg() == null) {
            throw new InvalidArgumentException("Эта команда требует аргумент: ключ элемента коллекции");
        }
        switch (Auth.checkRequest(req)){
            case 404:
                return new Response(404);
            case 403:
                return new Response(403);
        }
        if (!collection.contains(req.getArg())) {
            throw new InvalidArgumentException("Элемент с таким ключом не существует");
        }

        if (collection.replaceIfGreater(req.getArg(), req.getObj(), req.getLogin())) {
            return new Response(200);
        } else {
            return new Response(400);
        }
    }

    public String getDescription() {
        return description;
    }
}
