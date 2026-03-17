package dev.swiftconnect.config;

import java.util.LinkedHashMap;
import java.util.Map;

public class SwiftConfig {

    private String lang = "en_US";
    private Map<String, MacroEntry> macros = new LinkedHashMap<>();

    public SwiftConfig() {}

    public String getLang() { return lang; }
    public void setLang(String lang) { this.lang = lang; }

    public Map<String, MacroEntry> getMacros() { return macros; }
    public void setMacros(Map<String, MacroEntry> macros) { this.macros = macros; }
}
