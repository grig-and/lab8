package commands;

import content.Movie;
import exceptions.DBError;
import exceptions.InvalidArgumentException;
import util.Request;
import util.Response;

/**
 * Interface for commands
 */
public interface Commandable {
    /**

     * @throws InvalidArgumentException
     */
    Response run(Request req) throws InvalidArgumentException, DBError;
    /**
     * @return description for Help command
     */
    String getDescription();
}
