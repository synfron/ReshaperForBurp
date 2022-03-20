package synfron.reshaper.burp.ui.utils;

public interface IPrompter<T extends IPrompterModel> {
    void open(T model);
}
