package com.example.translator.Helper;

/**
 * Created by M Noman on 09-Nov-19.
 */

public class LanguageRemindHelper {

    private String inputLanguage=null;
    private String outputLanguage=null;

    private static final LanguageRemindHelper instance=new LanguageRemindHelper();

    LanguageRemindHelper(){}

    public static LanguageRemindHelper getInstance(){return instance;}

    public String getInputLanguage(){return inputLanguage;}
    public String getOutputLanguage(){return outputLanguage;}

    public void setInputLanguage(String inputLanguage){this.inputLanguage=inputLanguage;}
    public void setOutputLanguage(String outputLanguage){this.outputLanguage=outputLanguage;}
}
