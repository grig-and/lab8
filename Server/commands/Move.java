package commands;

import exceptions.InvalidArgumentException;
import exceptions.UserNotFoundException;
import util.Auth;
import util.CollectionManager;
import util.Request;
import util.Response;

/**
 * Command remove_key class
 */
public class Move implements Commandable {
    CollectionManager collection;
    final public static String description = "";


    public Move(CollectionManager collection) {
        this.collection = collection;
    }

    @Override
    public Response run(Request req) throws InvalidArgumentException {
        String[] args = req.getArg().split(";");
        if (args.length != 3) {
            throw new InvalidArgumentException("Некорректное количество аргументов");
        }
        switch (Auth.checkRequest(req)){
            case 404:
                return new Response(404);
            case 403:
                return new Response(403);
        }
        if (!collection.contains(args[0])) {
            throw new InvalidArgumentException("Элемента с таким ключом не существует");
        }

        if (collection.move(args[0], args[1], args[2], req.getLogin())) {
            return new Response(200);
        } else {
            return new Response(500);
        }
    }

    public String getDescription() {
        return description;
    }
}
