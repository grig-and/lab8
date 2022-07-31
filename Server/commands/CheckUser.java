package commands;

import content.Movie;
import exceptions.InvalidArgumentException;
import util.CollectionManager;
import util.DataBase;
import util.Request;
import util.Response;

/**
 * Command info class
 */
public class CheckUser implements Commandable {
    final public static String description = null;

    @Override
    public Response run(Request req) {
        if (DataBase.getInstance().getUser(req.getArg()) != null) {
            return new Response(200);
        } else {
            return new Response(404);
        }
    }

    public String getDescription() {
        return description;
    }

}
