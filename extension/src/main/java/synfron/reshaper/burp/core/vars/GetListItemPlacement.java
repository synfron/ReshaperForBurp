package synfron.reshaper.burp.core.vars;

import lombok.Getter;

public enum GetListItemPlacement implements IListItemPlacement {
    First,
    Last,
    All,
    Index(true),
    Size;

    @Getter
    private final boolean hasIndexSetter;

    GetListItemPlacement() {
        hasIndexSetter = false;
    }

    GetListItemPlacement(boolean hasIndexSetter) {
        this.hasIndexSetter = hasIndexSetter;
    }
}
