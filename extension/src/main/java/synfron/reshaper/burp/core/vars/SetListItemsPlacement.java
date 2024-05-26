package synfron.reshaper.burp.core.vars;

import lombok.Getter;

public enum SetListItemsPlacement {
    AddFirst("Add First"),
    AddLast("Add Last"),
    Overwrite("Overwrite");

    @Getter
    private final String name;

    SetListItemsPlacement(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
