package mdomasevicius.itunestop5.itunes;

public class ITunesApiNot200Exception extends RuntimeException {
    public ITunesApiNot200Exception(String message) {
        super(message);
    }
}
