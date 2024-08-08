package synfron.reshaper.burp.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum WorkspaceTab {
    HttpRules("HTTP Rules"),
    WebSocketRules ("WebSocket Rules"),
    GlobalVariables("Global Variables"),
    Logs("Logs"),
    Settings("Settings", false);

    private String name;
    private boolean hideable = true;

    WorkspaceTab(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
    
    public static WorkspaceTab byName(String name) {
        return Arrays.stream(WorkspaceTab.values()).filter(tab -> tab.name.equals(name)).findFirst().orElse(null);
    }
}
