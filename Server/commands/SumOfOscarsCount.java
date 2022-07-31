package commands;

import content.Movie;
import exceptions.UserNotFoundException;
import util.Auth;
import util.CollectionManager;
import util.Request;
import util.Response;

/**
 * Command sum_of_oscars_count class
 */
public class SumOfOscarsCount implements Commandable {
    CollectionManager collection;

    final public static String description = "вывести сумму значений поля oscarsCount для всех элементов коллекции";

    /**
     * Constructor of sum_of_oscars_count command
     *
     * @param collection CollectionManager instance
     */
    public SumOfOscarsCount(CollectionManager collection) {
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
        return new Response("" + collection.getSumOscars(), 200);
    }

    public String getDescription() {
        return description;
    }
}
