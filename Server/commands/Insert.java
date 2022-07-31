package commands;

import content.Movie;
import exceptions.DBError;
import exceptions.InvalidArgumentException;
import exceptions.UserNotFoundException;
import util.*;

/**
 * Command insert class
 */
public class Insert implements Commandable {
    CollectionManager collection;
    final public static String description = "добавить новый элемент с заданным ключом";

    /**
     * Constructor of insert command
     *
     * @param collection CollectionManager instance
     */
    public Insert(CollectionManager collection) {
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

        if (req.getArg() == null) {
            throw new InvalidArgumentException("Эта команда требует аргумент: ключ элемента коллекции");
        }
        if (collection.contains(req.getArg())) {
            return new Response(409);
        }
        Movie obj = req.getObj();
        if (obj == null) {
            obj = Movie.prompt();
        }

        return new Response(collection.insert(req.getArg(), obj, req.getLogin()));
    }

    public String getDescription() {
        return description;
    }
}
