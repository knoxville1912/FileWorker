package program;

public enum Separator {
    COMMA(","),
    TABULATION("   "),
    SEMICOLON(";");

    private final String separator;

    Separator(String separator) {
        this.separator = separator;
    }

    public String getSeparator() {
        return separator;
    }
}
