package edu.utfpr.ct.localization;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Locale;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class HostLocalizationManager {

    private static HostLocalizationManager clm;

    private final HashMap<String, Localize> localizeMap;
    
    private final StringProperty lang;
    
    private final HashMap<String, String> langMap;

    private HostLocalizationManager() {
        localizeMap = new HashMap<>();
        langMap = new HashMap<>();
        
        File f = new File("resources" + File.separator + "lang");
        
        FileFilter fF = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".map");
            }
        };

        if (f.isDirectory()) {
            for (File l : f.listFiles(fF)) {
                    String lang = l.getName().substring(0, l.getName().length() - 4);
                    localizeMap.put(lang, new Localize(lang, true));
                    
                    langMap.put(localizeMap.get(lang).getTextFor(HostLocalizationKeys.LANGUAGE_NAME), lang);
            }
        }
        
        String langDefault = Locale.getDefault().getLanguage()+"_"+Locale.getDefault().getCountry();
        if(localizeMap.containsKey(langDefault)){
            lang = new SimpleStringProperty(localizeMap.get(langDefault).getTextFor(HostLocalizationKeys.LANGUAGE_NAME));
        }else{
            lang = new SimpleStringProperty(localizeMap.get("default").getTextFor(HostLocalizationKeys.LANGUAGE_NAME));
        }
    }

    public StringProperty getLang() {
        return lang;
    }

    public static HostLocalizationManager getInstance() {
        if (clm == null) {
            clm = new HostLocalizationManager();
        }

        return clm;
    }
    
    public String translateLanguage(String langName){
        return langMap.get(langName);
    }

    public Localize getClientFor(String lang) {
        lang = langMap.get(lang);
        
        if (!localizeMap.containsKey(lang)) {
            Localize loc = new Localize(lang, true);

            localizeMap.put(lang, loc);
        }

        return localizeMap.get(lang);
    }

    public static String[] getValidLanguages() {
        return getInstance().langMap.keySet().toArray(new String[0]);
    }
}
