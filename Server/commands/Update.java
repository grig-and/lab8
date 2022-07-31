package commands;

import content.Movie;
import exceptions.DBError;
import exceptions.InvalidArgumentException;
import exceptions.UserNotFoundException;
import util.Auth;
import util.CollectionManager;
import util.Request;
import util.Response;

/**
 * Command update class
 */
public class Update implements Commandable {
    CollectionManager collection;

    final public static String description = "обновить значение элемента коллекции, id которого равен заданному";

    /**
     * Constructor of update command
     *
     * @param collection CollectionManager instance
     */
    public Update(CollectionManager collection) {
        this.collection = collection;
    }

    @Override
    public Response run(Request req) throws InvalidArgumentException, DBError {
        switch (Auth.checkRequest(req)){
            case 404:
                return new Response(404);
            case 403:
                return new Response(403);
        }
        long id;
        try {
            id = Long.parseLong(req.getArg());
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("Эта команда требует аргумент: id");
        }
        if (!collection.containsID(id)) {
            throw new InvalidArgumentException("Элемента с таким id нет");
        }
        return new Response(collection.update(id, req.getObj(), req.getLogin()));
    }

    public String getDescription() {
        return description;
    }
}
