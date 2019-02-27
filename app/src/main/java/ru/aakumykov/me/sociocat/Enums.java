package ru.aakumykov.me.sociocat;

public final class Enums {
    private Enums(){}

    public static enum CardEditMode {
        CREATE,
        EDIT
    }

    public static CardEditMode cardEditModeFromString(String arg) {
        switch (arg) {
            case "CREATE":
                return CardEditMode.CREATE;
            case "EDIT":
                return CardEditMode.EDIT;
            default:
                return null;
        }
    }
}
