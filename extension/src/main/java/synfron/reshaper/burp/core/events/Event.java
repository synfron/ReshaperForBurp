package synfron.reshaper.burp.core.events;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Event<A> {
    private List<WeakReference<IEventListener<A>>> listeners;

    public synchronized void clearListeners() {
        if (listeners != null) {
            listeners.clear();
        }
    }

    public synchronized void remove(IEventListener<A> listener) {
        if (listeners != null) {
            listeners.removeIf(listenerReference -> listenerReference.get() == null || listenerReference.get().equals(listener));
            if (listeners.isEmpty()) {
                listeners = null;
            }
        }
    }

    public synchronized void add(IEventListener<A> listener) {
        getListeners().add(new WeakReference<>(listener));
    }

    public synchronized void invoke(A args) {
        if (listeners != null) {
            Iterator<WeakReference<IEventListener<A>>> iterator = listeners.iterator();
            while (iterator.hasNext()) {
                WeakReference<IEventListener<A>> listenerReference = iterator.next();
                if (listenerReference.get() != null) {
                    listenerReference.get().invoke(args);
                } else {
                    iterator.remove();
                }
            }
        }
    }

    private List<WeakReference<IEventListener<A>>> getListeners() {
        if (listeners == null) {
            listeners = new LinkedList<>();
        }
        return listeners;
    }
}
