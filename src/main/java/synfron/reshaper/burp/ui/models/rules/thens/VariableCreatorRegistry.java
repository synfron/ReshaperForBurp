package synfron.reshaper.burp.ui.models.rules.thens;

import synfron.reshaper.burp.core.vars.VariableSourceEntry;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VariableCreatorRegistry {

    private static final List<WeakReference<IVariableCreator>> creators = new ArrayList<>();

    public static synchronized void register(IVariableCreator variableCreator) {
        creators.add(new WeakReference<>(variableCreator));
    }

    public static synchronized List<VariableSourceEntry> getVariableEntries() {
        List<VariableSourceEntry> entries = new ArrayList<>(creators.size() + 5);
        Iterator<WeakReference<IVariableCreator>> iterator = creators.iterator();
        while (iterator.hasNext()) {
            WeakReference<IVariableCreator> listenerReference = iterator.next();
            if (listenerReference.get() != null) {
                entries.addAll(listenerReference.get().getVariableEntries());
            } else {
                iterator.remove();
            }
        }
        return entries;
    }
}
