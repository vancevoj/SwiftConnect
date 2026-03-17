package dev.swiftconnect.config;

import java.util.ArrayList;
import java.util.List;

public class MacroEntry {

    private String description = "";
    private String permission = "";
    private List<String> aliases = new ArrayList<>();
    private List<MacroAction> actions = new ArrayList<>();

    public MacroEntry() {}

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPermission() { return permission; }
    public void setPermission(String permission) { this.permission = permission; }

    public List<String> getAliases() { return aliases; }
    public void setAliases(List<String> aliases) { this.aliases = aliases; }

    public List<MacroAction> getActions() { return actions; }
    public void setActions(List<MacroAction> actions) { this.actions = actions; }
}
