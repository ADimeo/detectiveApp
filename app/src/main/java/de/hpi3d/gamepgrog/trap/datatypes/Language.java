package de.hpi3d.gamepgrog.trap.datatypes;

import java.util.Locale;

public class Language implements UserData {

    private String languageCode;


    public static Language getCurrentLanguage() {
        String languageString = Locale.getDefault().getLanguage();
        return new Language(languageString);
    }

    public Language(String languageCode) {
        this.languageCode = languageCode;
    }


    @Override
    public String[] requiredPermission() {
        // No permission required.
        // TODO: Does this say no permission required?
        return new String[0];
    }
}
