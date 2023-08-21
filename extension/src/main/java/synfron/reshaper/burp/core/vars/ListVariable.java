package synfron.reshaper.burp.core.vars;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.utils.CollectionUtils;
import synfron.reshaper.burp.core.utils.TextUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ListVariable extends Variable {

    @Getter
    private List<Object> values = new ArrayList<>();

    @Getter @Setter
    private String delimiter = "\n";

    private ListVariable() {
        this(null);
    }

    public ListVariable(String name) {
        super(name);
    }

    @Override
    public Object getValue() {
        return values.stream().filter(Objects::nonNull).map(TextUtils::toString).collect(Collectors.joining(delimiter));
    }

    @Override
    public Object getValue(GetListItemPlacement itemPlacement, Integer index) {
        if (index != null) {
            return CollectionUtils.elementAtOrDefault(values, index, null);
        } else if (itemPlacement == null) {
            return getValue();
        }
        return switch (itemPlacement) {
            case First -> CollectionUtils.firstOrDefault(values);
            case Last -> CollectionUtils.lastOrDefault(values);
            case All -> getValue();
            case Size -> values.size();
            default -> null;
        };
    }

    @Override
    public void setValue(Object value) {
        setValue(SetListItemPlacement.All, null, null, value);
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, "value", value));
    }

    public void setValues(Object[] values, String delimiter) {
        this.delimiter = StringUtils.defaultString(delimiter, this.delimiter);
        this.values = values == null ?
                new ArrayList<>() :
                Arrays.stream(values).map(TextUtils::toString).map(value -> (Object)value).collect(Collectors.toCollection(ArrayList::new));
        propertyChangedEvent.invoke(new PropertyChangedArgs(this, "value", getValue()));
    }

    public void delete(DeleteListItemPlacement itemPlacement, Integer index) {
        switch (itemPlacement) {
            case First -> CollectionUtils.removeFirst(values);
            case Last ->  CollectionUtils.removeLast(values);
            case Index -> {
                if (index != null) {
                    CollectionUtils.remove(values, index);
                }
            }
            case All ->  values.clear();
        }
    }

    @Override
    public void setValue(SetListItemPlacement itemPlacement, String delimiter, Integer index, Object value) {
        if (itemPlacement != null) {
            if (itemPlacement.isHasIndexSetter() && index != null) {
                CollectionUtils.set(values, index, value);
            } else {
                switch (itemPlacement) {
                    case First -> CollectionUtils.setFirst(values, value);
                    case Last -> CollectionUtils.setLast(values, value);
                    case AddFirst -> values.add(0, value);
                    case AddLast -> values.add(value);
                    case All -> {
                        if (delimiter != null) {
                            this.delimiter = delimiter;
                        }
                        values = value == null ?
                                new ArrayList<>() :
                                Arrays.stream(TextUtils.toString(value).split(this.delimiter)).map(part -> (Object) part).collect(Collectors.toCollection(ArrayList::new));
                    }
                }
            }
            propertyChangedEvent.invoke(new PropertyChangedArgs(this, "value", getValue()));
        }
    }

    @Override
    public String toString() {
        return TextUtils.toString(name);
    }

    @Override
    public boolean hasValue() {
        return values != null && values.size() > 0;
    }

    @Override
    public boolean isList() {
        return true;
    }

    public int size() {
        return values.size();
    }

    public Iterator getIterator() {
        return new Iterator();
    }

    public class Iterator implements java.util.Iterator<Object> {
        private int index;

        private Iterator() {}

        public Object next() {
            return index < values.size() ? values.get(index++) : null;
        }

        public boolean hasNext() {
            return index < values.size();
        }
    }
}
