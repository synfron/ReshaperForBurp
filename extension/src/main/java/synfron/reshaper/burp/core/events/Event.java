package synfron.reshaper.burp.core.events;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Event<A> {
    private List<WeakReference<IEventListener<A>>> listeners;

    public synchronized void clearListeners() {
        if (listeners != null) {
            listeners = null;
        }
    }

    public synchronized void remove(IEventListener<A> listener) {
        if (listeners != null) {
            listeners = listeners.stream()
                    .filter(listenerReference -> listenerReference.get() != null && !Objects.equals(listenerReference.get(), listener))
                    .collect(Collectors.toCollection(LinkedList::new));
            if (listeners.isEmpty()) {
                listeners = null;
            }
        }
    }

    public synchronized void add(IEventListener<A> listener) {
        LinkedList<WeakReference<IEventListener<A>>> listeners = this.listeners == null ? new LinkedList<>() : this.listeners.stream()
                .filter(listenerReference -> listenerReference.get() != null)
                .collect(Collectors.toCollection(LinkedList::new));
        listeners.add(new WeakReference<>(listener));
        this.listeners = listeners;
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
}
