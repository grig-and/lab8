package util;

import content.Movie;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.TreeMap;

public class Response implements Serializable {
    private static final long serialVersionUID = 8243L;

    public String getMsg() {
        return msg;
    }

    private String msg;
    private TreeMap<String, Movie> movies;
    private LinkedList<HistoryValue> history;

    public int getCode() {
        return code;
    }

    private int code;

    public Response(String msg) {
        this.msg = msg;
        this.code = 0;
    }

    public Response(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }

    public Response(int code) {
        this.code = code;
    }

    public Response(TreeMap<String, Movie> movies) {
        this.movies = movies;
        this.code = 200;
    }
    public Response(LinkedList<HistoryValue> history) {
        this.history = history;
        this.code = 200;
    }

    public TreeMap<String, Movie> getMovies() {
        return movies;
    }
    public LinkedList<HistoryValue> getHistory() {
        return history;
    }

    @Override
    public String toString() {
        return "Response{" +
                "msg='" + msg + '\'' +
                ", movies=" + movies +
                ", history=" + history +
                ", code=" + code +
                '}';
    }
}
