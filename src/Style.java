//ANSI color codes
public enum Style {
    RESET("\u001B[0m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001b[38;5;165m"),
    LIGHT_BLUE("\u001B[36m"),
    ORANGE("\u001b[38;5;202m"),
    WHITE("\u001B[37m");

    private String text;

    Style(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
