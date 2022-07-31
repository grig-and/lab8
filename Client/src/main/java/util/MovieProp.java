package util;

import content.Color;
import content.Country;
import content.MovieGenre;
import content.MpaaRating;
import javafx.beans.property.*;

import java.time.LocalDate;
import java.util.Objects;

public class MovieProp {

    private LongProperty ID ;
    private ObjectProperty<MpaaRating> Mpaa;

    public void setX(float x) {
        this.X.set(x);
    }

    public void setY(int y) {
        this.Y.set(y);
    }

    private FloatProperty X;
    private IntegerProperty Y;
    private ObjectProperty<LocalDate> date;
    private ObjectProperty<MovieGenre> genre;
    private StringProperty key;
    private StringProperty name;
    private ObjectProperty<Color> operatorHColor;
    private LongProperty operatorHeight;
    private StringProperty operatorName;
    private ObjectProperty<Country> operatorNationality;
    private StringProperty operatorPassport;
    private IntegerProperty oscars;
    private StringProperty owner;


    public MovieProp(String key,
                     String owner,
                     String name,
                     long ID,
                     float X,
                     int Y,
                     int oscars,
                     LocalDate date,
                     MpaaRating Mpaa,
                     MovieGenre genre,
                     String operatorName,
                     long operatorHeight,
                     Color operatorHColor,
                     Country operatorNationality,
                     String operatorPassport) {
        this.ID = new SimpleLongProperty(ID);
        this.Mpaa = new SimpleObjectProperty<>(Mpaa);
        this.X = new SimpleFloatProperty(X);
        this.Y = new SimpleIntegerProperty(Y);
        this.date = new SimpleObjectProperty<>(date);
        this.genre = new SimpleObjectProperty<>(genre);
        this.key = new SimpleStringProperty(key);
        this.name = new SimpleStringProperty(name);
        this.operatorHColor = new SimpleObjectProperty<>(operatorHColor);
        this.operatorHeight = new SimpleLongProperty(operatorHeight);
        this.operatorName = new SimpleStringProperty(operatorName);
        this.operatorNationality = new SimpleObjectProperty<>(operatorNationality);
        this.operatorPassport = new SimpleStringProperty(operatorPassport);
        this.oscars = new SimpleIntegerProperty(oscars);
        this.owner = new SimpleStringProperty(owner);
    }

    public long getID() {
        return ID.get();
    }

    public LongProperty IDProperty() {
        return ID;
    }

    public MpaaRating getMpaa() {
        return Mpaa.get();
    }

    public ObjectProperty<MpaaRating> mpaaProperty() {
        return Mpaa;
    }

    public float getX() {
        return X.get();
    }

    public FloatProperty xProperty() {
        return X;
    }

    public int getY() {
        return Y.get();
    }

    public IntegerProperty yProperty() {
        return Y;
    }

    public LocalDate getDate() {
        return date.get();
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public MovieGenre getGenre() {
        return genre.get();
    }

    public ObjectProperty<MovieGenre> genreProperty() {
        return genre;
    }

    public String getKey() {
        return key.get();
    }

    public StringProperty keyProperty() {
        return key;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public Color getOperatorHColor() {
        return operatorHColor.get();
    }

    public ObjectProperty<Color> operatorHColorProperty() {
        return operatorHColor;
    }

    public long getOperatorHeight() {
        return operatorHeight.get();
    }

    public LongProperty operatorHeightProperty() {
        return operatorHeight;
    }

    public String getOperatorName() {
        return operatorName.get();
    }

    public StringProperty operatorNameProperty() {
        return operatorName;
    }

    public Country getOperatorNationality() {
        return operatorNationality.get();
    }

    public ObjectProperty<Country> operatorNationalityProperty() {
        return operatorNationality;
    }

    public String getOperatorPassport() {
        return operatorPassport.get();
    }

    public StringProperty operatorPassportProperty() {
        return operatorPassport;
    }

    public int getOscars() {
        return oscars.get();
    }

    public IntegerProperty oscarsProperty() {
        return oscars;
    }

    public String getOwner() {
        return owner.get();
    }

    public StringProperty ownerProperty() {
        return owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieProp movieProp = (MovieProp) o;
        return ID.equals(movieProp.ID) && Objects.equals(Mpaa, movieProp.Mpaa) && Objects.equals(X, movieProp.X) && Y.equals(movieProp.Y) && date.equals(movieProp.date) && Objects.equals(genre, movieProp.genre) && key.equals(movieProp.key) && name.equals(movieProp.name) && Objects.equals(operatorHColor, movieProp.operatorHColor) && operatorHeight.equals(movieProp.operatorHeight) && operatorName.equals(movieProp.operatorName) && Objects.equals(operatorNationality, movieProp.operatorNationality) && operatorPassport.equals(movieProp.operatorPassport) && oscars.equals(movieProp.oscars) && owner.equals(movieProp.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, Mpaa, X, Y, date, genre, key, name, operatorHColor, operatorHeight, operatorName, operatorNationality, operatorPassport, oscars, owner);
    }

    @Override
    public String toString() {
        return "MovieProp{" +
                "ID=" + ID +
                ", Mpaa=" + Mpaa +
                ", X=" + X +
                ", Y=" + Y +
                ", date=" + date +
                ", genre=" + genre +
                ", key=" + key +
                ", name=" + name +
                ", operatorHColor=" + operatorHColor +
                ", operatorHeight=" + operatorHeight +
                ", operatorName=" + operatorName +
                ", operatorNationality=" + operatorNationality +
                ", operatorPassport=" + operatorPassport +
                ", oscars=" + oscars +
                ", owner=" + owner +
                '}';
    }
}
