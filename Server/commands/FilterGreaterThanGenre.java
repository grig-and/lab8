package commands;

import content.Movie;
import content.MovieGenre;
import exceptions.InvalidArgumentException;
import exceptions.InvalidParameterException;
import exceptions.UserNotFoundException;
import util.Auth;
import util.CollectionManager;
import util.Request;
import util.Response;

/**
 * Command filter_greater_than_genre class
 */
public class FilterGreaterThanGenre implements Commandable {
    CollectionManager collection;
    final public static String description = "вывести элементы, значение поля genre которых больше заданного";

    /**
     * Constructor of filter_greater_than_genre command
     *
     * @param collection CollectionManager instance
     */
    public FilterGreaterThanGenre(CollectionManager collection) {
        this.collection = collection;
    }

    @Override
    public Response run(Request req) throws InvalidArgumentException {
        switch (Auth.checkRequest(req)){
            case 404:
                return new Response(404);
            case 403:
                return new Response(403);
        }
        if (req.getArg() == null) {
            throw new InvalidArgumentException("Необходим параметр - жанр");
        }

        try {
            return new Response(collection.filterGreaterThanGenre(MovieGenre.parse(req.getArg())));
        } catch (InvalidParameterException e) {
            throw new InvalidArgumentException("Нет такого жанра");
        }

    }

    public String getDescription() {
        return description;
    }
}
