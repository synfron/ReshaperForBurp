package synfron.reshaper.burp.core.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class ListMap<K, V> {
    private int nodeCount;
    private final HashMap<K, List<OrderedNode>> backingMap = new HashMap<>();

    public void setLast(K key, V value) {
        List<OrderedNode> values = backingMap.computeIfAbsent(key, k -> new ArrayList<>(1));
        if (values.size() == 1) {
            OrderedNode node = values.get(0);
            node.setValue(value);
        } else {
            if (values.isEmpty()) {
                values.add(createNode(key, value));
            } else {
                values.get(values.size() - 1).setValue(value);
            }
        }
    }

    public void add(K key, V value) {
        List<OrderedNode> values = backingMap.computeIfAbsent(key, k -> new ArrayList<>(1));
        values.add(createNode(key, value));
    }

    public V get(K key) {
        List<OrderedNode> nodes = backingMap.get(key);
        return nodes != null ? nodes.get(nodes.size() - 1).getValue() : null;
    }

    public List<Pair<K, V>> entrySet() {
        return backingMap.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .sorted(Comparator.comparingInt(OrderedNode::getOrder))
                .map(node -> Pair.of(node.getKey(), node.getValue()))
                .collect(Collectors.toList());
    }

    private OrderedNode createNode(K key, V value) {
        return new OrderedNode(key, value, ++nodeCount);
    }

    public void remove(K key) {
        List<OrderedNode> nodes = backingMap.get(key);
        if (nodes != null) {
            nodeCount -= nodes.size();
        }
        backingMap.remove(key);
    }

    public boolean containsKey(K key) {
        return backingMap.containsKey(key);
    }

    public int size() {
        return nodeCount;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private class Node {
        private K key;
        private V value;
    }

    @Getter
    @Setter
    private class OrderedNode extends Node {
        private final int order;

        private OrderedNode(K key, V value, int order) {
            super(key, value);
            this.order = order;
        }
    }
}
