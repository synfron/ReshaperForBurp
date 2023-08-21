package synfron.reshaper.burp.core.vars;

public interface IListItemPlacement {

    static GetListItemPlacement toGet(SetListItemPlacement itemPlacement) {
        return switch (itemPlacement) {
            case Index -> GetListItemPlacement.Index;
            case First -> GetListItemPlacement.First;
            case Last -> GetListItemPlacement.Last;
            case All -> GetListItemPlacement.All;
            default -> null;
        };
    }

    static GetListItemPlacement toGet(DeleteListItemPlacement itemPlacement) {
        return switch (itemPlacement) {
            case Index -> GetListItemPlacement.Index;
            case First -> GetListItemPlacement.First;
            case Last -> GetListItemPlacement.Last;
            case All -> GetListItemPlacement.All;
        };
    }
}
