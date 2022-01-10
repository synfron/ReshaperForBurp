package synfron.reshaper.burp.core.utils;

public interface IItemPlacement {

    static DeleteItemPlacement toDelete(SetItemPlacement itemPlacement) {
        return switch (itemPlacement) {
            case First -> DeleteItemPlacement.First;
            case Last -> DeleteItemPlacement.Last;
            case All -> DeleteItemPlacement.All;
            default -> null;
        };
    }

    static GetItemPlacement toGet(SetItemPlacement itemPlacement) {
        return switch (itemPlacement) {
            case First -> GetItemPlacement.First;
            case Last, Only, All, New -> GetItemPlacement.Last;
            default -> null;
        };
    }

    static SetItemPlacement toSet(DeleteItemPlacement itemPlacement) {
        return switch (itemPlacement) {
            case First -> SetItemPlacement.First;
            case Last -> SetItemPlacement.Last;
            case All -> SetItemPlacement.All;
            default -> null;
        };
    }
}
