package util;

import content.Coordinates;
import javafx.util.Duration;

import java.io.Serializable;

public class HistoryValue implements Serializable {
    private static final long serialVersionUID = 8243L;
    //class to store pairs of String and Coordinates
    private String key;
    private Coordinates value;
    private Duration duration;

    public HistoryValue(String key, Coordinates value, Duration duration) {
        this.key = key;
        this.value = value;
        this.duration = duration;
    }
    public HistoryValue(String key, Coordinates value) {
        this.key = key;
        this.value = value;
        this.duration = Duration.millis(0);
    }

    public String getKey() {
        return key;
    }

    public Coordinates getValue() {
        return value;
    }

    public Duration getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "HistoryValue{" +
                "key='" + key + '\'' +
                ", value=" + value +
                ", duration=" + duration +
                '}';
    }
}
