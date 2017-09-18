/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utfpr.ct.localization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;

/**
 *
 * @author henrique
 */
public class LocalizeClient {

    private final String language;
    private HashMap<String, String> stringMapping;

    public LocalizeClient() {
        language = Locale.getDefault().getLanguage();
        changeLanguage(language);
    }

    public String getTextFor(String keyString) {
        String ret = stringMapping.get(keyString);

        if (ret == null) {
            ret = "Error when tried to grab String, check Localization package or language definition file.";
        }

        return ret;
    }

    public String getTextForKey(String keyString) {
        return getTextFor(keyString);
    }

//    public final void changeLanguage(String language) throws IllegalArgumentException{
    public final void changeLanguage(String language){
        File f = new File("lang" + File.separator + "web" + File.separator + language + ".map");

        if (!f.exists()) {
            f = new File("lang" + File.separator + "web" + File.separator + "default.map");
//            throw new IllegalArgumentException(language + " is not a valid language identifier or the language is unavailable.");
        }

        stringMapping = new HashMap<>();
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(f);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            String s;
            while ((s = reader.readLine()) != null) {
                String[] ss = s.split("<>");

                if (ss.length == 2) {
                    stringMapping.put(ss[0], ss[1]);
                }
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                }
            }
        }
    }
}
