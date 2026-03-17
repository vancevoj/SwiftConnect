package dev.swiftconnect.config;

import java.util.LinkedHashMap;
import java.util.Map;

public class MacroAction {

    private String type = "transfer";
    private Map<String, String> options = new LinkedHashMap<>();

    public MacroAction() {}

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Map<String, String> getOptions() { return options; }
    public void setOptions(Map<String, String> options) { this.options = options; }
}
