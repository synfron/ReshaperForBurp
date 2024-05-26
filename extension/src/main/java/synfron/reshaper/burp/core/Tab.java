package synfron.reshaper.burp.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Tab {
    HttpRules("HTTP Rules"),
    WebSocketRules ("WebSocket Rules"),
    GlobalVariables("Global Variables"),
    Logs("Logs"),
    Settings("Settings", false);

    private String name;
    private boolean hideable = true;

    Tab(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
    
    public static Tab byName(String name) {
        return Arrays.stream(Tab.values()).filter(tab -> tab.name.equals(name)).findFirst().orElse(null);
    }
}
