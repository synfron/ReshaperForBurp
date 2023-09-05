package synfron.reshaper.burp.core.vars;

import lombok.Getter;

public enum DeleteListItemPlacement implements IListItemPlacement {
    First,
    Last,
    Index(true),
    All;

    @Getter
    private final boolean hasIndexSetter;

    DeleteListItemPlacement() {
        hasIndexSetter = false;
    }

    DeleteListItemPlacement(boolean hasIndexSetter) {
        this.hasIndexSetter = hasIndexSetter;
    }
}
