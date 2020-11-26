package synfron.reshaper.burp.core.events;

public interface IEventListener<T> {
    void invoke(T args);
}
