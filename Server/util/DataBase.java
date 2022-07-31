package util;

import content.*;
import exceptions.DBError;
import javafx.util.Duration;
import org.postgresql.util.PSQLException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.TreeMap;

public class DataBase {
    private static DataBase instance = null;
    Connection conn;

    private DataBase() {
        try {
            String user = "";
            String password = "";
            try {
                Scanner sc = new Scanner(new FileReader("./cred.txt"));
                user = sc.nextLine().trim();
                password = sc.nextLine().trim();
            } catch (FileNotFoundException | NoSuchElementException e) {
                System.out.println("DB login file not found");
            }
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/studs", user, password);
            PreparedStatement st = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "login VARCHAR(20) PRIMARY KEY," +
                            "hash VARCHAR(100) NOT NULL," +
                            "salt VARCHAR(10) NOT NULL" +
                            ");" +

                            "CREATE TABLE IF NOT EXISTS movies (" +
                            "key VARCHAR(20) NOT NULL UNIQUE," +
                            "id SERIAL PRIMARY KEY," +
                            "owner VARCHAR(20) references users(login) ON DELETE SET NULL," +
                            "name VARCHAR(20) NOT NULL," +
                            "creation_date TIMESTAMP NOT NULL," +

                            "coord_x FLOAT," +
                            "coord_y INTEGER NOT NULL," +

                            "oscars_count INTEGER NOT NULL," +
                            "genre VARCHAR(10)," +
                            "mpaa_rating VARCHAR(5)," +
                            "operator_name VARCHAR(20) NOT NULL," +

                            "operator_height BIGINT NOT NULL," +
                            "operator_passport_id VARCHAR(37) NOT NULL," +
                            "operator_hair_color VARCHAR(6)," +
                            "operator_nationality VARCHAR(14)" +
                            ");" +

                            "CREATE TABLE IF NOT EXISTS history (" +
                            "key VARCHAR(20) NOT NULL," +
                            "x FLOAT," +
                            "y INTEGER NOT NULL" +
                            ");" +

                            "CREATE TABLE IF NOT EXISTS recpoints (" +
                            "key VARCHAR(20) NOT NULL," +
                            "x FLOAT," +
                            "y INTEGER NOT NULL" +
                            ");"
            );
            st.executeUpdate();

        } catch (PSQLException e) {
            Log.getLog().error("Ошибка подключения к базе");
            System.exit(1);
        } catch (SQLException e) {
            Log.getLog().error(e);
        }
    }


    public static DataBase getInstance() {
        if (instance == null) {
            instance = new DataBase();
        }
        return instance;
    }

    public TreeMap<String, Movie> getAll() {
        TreeMap<String, Movie> movies = new TreeMap<>();

        try {
            ResultSet res = conn.prepareStatement("SELECT * FROM movies").executeQuery();
            while (res.next()) {
                Movie movie = new Movie(
                        res.getInt("id"),
                        res.getDate("creation_date").toLocalDate(),
                        res.getString("name"),
                        new Coordinates(res.getFloat("coord_x"), res.getInt("coord_y")),
                        res.getInt("oscars_count"),
                        res.getString("genre") == null ? null : MovieGenre.valueOf(res.getString("genre")),
                        res.getString("mpaa_rating") == null ? null : MpaaRating.valueOf(res.getString("mpaa_rating")),
                        new Person(
                                res.getString("operator_name"),
                                res.getLong("operator_height"),
                                res.getString("operator_passport_id"),
                                res.getString("operator_hair_color") == null ? null : Color.valueOf(res.getString("operator_hair_color")),
                                res.getString("operator_nationality") == null ? null : Country.valueOf(res.getString("operator_nationality"))
                        )
                );
                movie.setOwner(res.getString("owner"));
                movies.put(res.getString("key"), movie);
            }
        } catch (SQLException | NullPointerException e) {

        }
        return movies;
    }

    public long add(String key, Movie movie, String login) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO movies(key, owner, name, creation_date, " +
                            "coord_x, coord_y, oscars_count, " +
                            "genre, mpaa_rating, " +
                            "operator_name, operator_height, operator_passport_id, operator_hair_color, operator_nationality)" +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                            "RETURNING id");
            ps.setString(1, key);
            ps.setString(2, login);
            ps.setString(3, movie.getName());
            ps.setDate(4, Date.valueOf(movie.getCreationDate()));
            ps.setDouble(5, movie.getCoordinates().getX());
            ps.setInt(6, movie.getCoordinates().getY());
            ps.setLong(7, movie.getOscarsCount());
            setNullableEnum(ps, 8, movie.getGenre());
            setNullableEnum(ps, 9, movie.getRating());
            ps.setString(10, movie.getOperator().getName());
            ps.setLong(11, movie.getOperator().getHeight());
            ps.setString(12, movie.getOperator().getPassportID());
            setNullableEnum(ps, 13, movie.getOperator().getHairColor());
            setNullableEnum(ps, 14, movie.getOperator().getNationality());
            ResultSet res = ps.executeQuery();
            res.next();
            return res.getLong("id");
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }

    private void setNullableEnum(PreparedStatement ps, int place, Enum en) throws SQLException {
        if (en != null) {
            ps.setString(place, en.name());
        } else {
            ps.setNull(place, java.sql.Types.NULL);
        }
    }

    public User getUser(String login) {
        try {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM \"users\" WHERE login = ?");
            st.setString(1, login);
            ResultSet res = st.executeQuery();
            if (res.next()) {
                return new User(login, res.getString("hash"), res.getString("salt"));
            }
        } catch (SQLException e) {

        }
        return null;
    }

    public boolean isUserExist(String login) {
        try {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM \"users\" WHERE login = ?");
            st.setString(1, login);
            ResultSet res = st.executeQuery();
            return res.next();
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean isMovieExistById(long id, String login) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM movies WHERE id = ? AND owner = ?");
            ps.setLong(1, id);
            ps.setString(2, login);
            ResultSet res = ps.executeQuery();
            return res.next();
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean isMovieExistByKey(String key) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM movies WHERE 'key' = ?");
            ps.setString(1, key);
            ResultSet res = ps.executeQuery();
            return res.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public long insert(String key, Movie movie, String login) throws DBError {
        if (isMovieExistByKey(key)) {
            throw new DBError("Уже есть элемент с таким ключом");
        }
        return add(key, movie, login);
    }

    public boolean update(Long id, Movie movie, String login) throws DBError {
        if (!isMovieExistById(id, login)) {
            throw new DBError("Нет доступного элемента с таким id");
        }

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE movies SET name = ?, creation_date = ?, " +
                            "coord_x = ?, coord_y = ?, oscars_count = ?, " +
                            "genre = ?, mpaa_rating = ?, " +
                            "operator_name = ?, operator_height = ?, operator_passport_id = ?, operator_hair_color = ?, operator_nationality = ? " +
                            "WHERE id = ? AND owner = ?");

            ps.setString(1, movie.getName());
            ps.setDate(2, Date.valueOf(movie.getCreationDate()));
            ps.setDouble(3, movie.getCoordinates().getX());
            ps.setInt(4, movie.getCoordinates().getY());
            ps.setLong(5, movie.getOscarsCount());
            setNullableEnum(ps, 6, movie.getGenre());
            setNullableEnum(ps, 7, movie.getRating());
            ps.setString(8, movie.getOperator().getName());
            ps.setLong(9, movie.getOperator().getHeight());
            ps.setString(10, movie.getOperator().getPassportID());
            setNullableEnum(ps, 11, movie.getOperator().getHairColor());
            setNullableEnum(ps, 12, movie.getOperator().getNationality());
            ps.setLong(13, id);
            ps.setString(14, login);
            int res = ps.executeUpdate();

            return res > 0;
        } catch (Exception e) {
            return false;
        }

    }

    public int addUser(String login, String hash, String salt) {
        try {
            if (isUserExist(login)) {
                return 409;
            }
            PreparedStatement st = conn.prepareStatement("INSERT INTO \"users\" (login, hash, salt) VALUES (?, ?, ?)");
            st.setString(1, login);
            st.setString(2, hash);
            st.setString(3, salt);
            return st.executeUpdate() > 0 ? 201 : 500;
        } catch (SQLException e) {
            return 500;
        }
    }

    public int clear(String login) {
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM movies WHERE owner = ?");
            ps.setString(1, login);
            int res = ps.executeUpdate();
            return res > 0 ? 200 : 404;
        } catch (SQLException e) {
            return 500;
        }
    }

    public boolean removeGreater(Integer oscarsCount, String login) {
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM movies WHERE oscars_count > ? AND owner = ?");
            ps.setInt(1, oscarsCount);
            ps.setString(2, login);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean removeGreaterKey(String key, String login) {
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM movies WHERE LENGTH(key) > ? AND owner = ?");
            ps.setInt(1, key.length());
            ps.setString(2, login);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {

            return false;
        }
    }

    public boolean removeKey(String key, String login) {
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM movies WHERE key = ? AND owner = ?");
            ps.setString(1, key);
            ps.setString(2, login);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean replaceIfGreater(String key, Movie movie, String login) throws DBError {
        if (!isMovieExistByKey(key)) {
            throw new DBError("Нет доступного элемента с таким id");
        }

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE movies SET name = ?, creation_date = ?, " +
                            "coord_x = ?, coord_y = ?, oscars_count = ?, " +
                            "genre = ?, mpaa_rating = ?, " +
                            "operator_name = ?, operator_height = ?, operator_passport_id = ?, operator_hair_color = ?, operator_nationality = ? " +
                            "WHERE 'key' = ? AND oscars_count > ? AND owner = ?");

            ps.setString(1, movie.getName());
            ps.setDate(2, Date.valueOf(movie.getCreationDate()));
            ps.setDouble(3, movie.getCoordinates().getX());
            ps.setInt(4, movie.getCoordinates().getY());
            ps.setLong(5, movie.getOscarsCount());
            setNullableEnum(ps, 6, movie.getGenre());
            setNullableEnum(ps, 7, movie.getRating());
            ps.setString(8, movie.getOperator().getName());
            ps.setLong(9, movie.getOperator().getHeight());
            ps.setString(10, movie.getOperator().getPassportID());
            setNullableEnum(ps, 11, movie.getOperator().getHairColor());
            setNullableEnum(ps, 12, movie.getOperator().getNationality());
            ps.setString(13, key);
            ps.setInt(14, movie.getOscarsCount());
            ps.setString(15, login);
            int res = ps.executeUpdate();

            return res > 0;
        } catch (Exception e) {
            return false;
        }

    }

    public boolean move(String key, float xP, int yP, String login) {
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE movies SET coord_x = ?, coord_y = ? WHERE key = ? AND owner = ?");
            ps.setDouble(1, xP);
            ps.setInt(2, yP);
            ps.setString(3, key);
            ps.setString(4, login);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean addHistory(String arg) {
        String[] args = arg.split(";");
        String key = args[0];
        float x;
        int y;
        try {
            x = Float.parseFloat(args[1]);
            y = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            return false;
        }

        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO history (key, x, y) VALUES (?, ?, ?)");
            ps.setString(1, key);
            ps.setFloat(2, x);
            ps.setInt(3, y);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }

    }

    public boolean addRecPoint(String arg) {
        String[] args = arg.split(";");
        String key = args[0];
        float x;
        int y;
        try {
            x = Float.parseFloat(args[1]);
            y = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            return false;
        }

        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO recpoints (key, x, y) VALUES (?, ?, ?)");
            ps.setString(1, key);
            ps.setFloat(2, x);
            ps.setInt(3, y);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean clearHistory() {
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM history");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean clearRecPoints() {
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM recpoints");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public LinkedList<HistoryValue> getHistory() {
        LinkedList<HistoryValue> history = new LinkedList<>();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM history");
            ResultSet rs = ps.executeQuery();
            Duration d = Duration.ZERO;
            while (rs.next()) {
                d = d.add(Duration.millis(500));
                history.add(new HistoryValue(rs.getString("key"), new Coordinates(rs.getFloat("x"), rs.getInt("y")), d));
            }
        } catch (SQLException e) {
            return null;
        }
        return history;
    }

    public LinkedList<HistoryValue> getRecPoints() {
        LinkedList<HistoryValue> recPoints = new LinkedList<>();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM recpoints");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                recPoints.add(new HistoryValue(rs.getString("key"), new Coordinates(rs.getFloat("x"), rs.getInt("y"))));
            }
        } catch (SQLException e) {
            return null;
        }
        return recPoints;
    }
}
