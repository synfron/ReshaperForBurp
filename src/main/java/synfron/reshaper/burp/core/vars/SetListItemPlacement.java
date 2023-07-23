package synfron.reshaper.burp.core.vars;

import lombok.Getter;

public enum SetListItemPlacement implements IListItemPlacement {
    First("First"),
    Last("Last"),
    Index("Index", true, false),
    AddFirst("Add First"),
    AddLast("Add Last"),
    All("All", false, true);

    @Getter
    private final String name;
    @Getter
    private final boolean hasIndexSetter;
    @Getter
    private final boolean hasDelimiterSetter;

    SetListItemPlacement(String name) {
        this.name = name;
        hasIndexSetter = false;
        hasDelimiterSetter = false;
    }

    SetListItemPlacement(String name, boolean hasIndexSetter, boolean hasDelimiterSetter) {
        this.name = name;
        this.hasIndexSetter = hasIndexSetter;
        this.hasDelimiterSetter = hasDelimiterSetter;
    }


    @Override
    public String toString() {
        return name;
    }
}
