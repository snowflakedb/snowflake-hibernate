package net.snowflake.hibernate.dialect;

public enum TableType {
    STANDARD(null),
    HYBRID("HYBRID");

    private final String tableModifier;

    TableType(String tableModifier) {
        this.tableModifier = tableModifier;
    }

    String createTableStatement() {
        if (tableModifier == null) {
            return "CREATE TABLE";
        } else {
            return "CREATE " + tableModifier + " TABLE";
        }
    }
}
