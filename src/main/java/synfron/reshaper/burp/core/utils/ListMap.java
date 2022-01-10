package synfron.reshaper.burp.core.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ListMap<K, V> {
    private int nodeCount;
    private final HashMap<K, List<OrderedNode>> backingMap = new HashMap<>();

    public void setLast(K key, V value) {
        List<OrderedNode> values = backingMap.computeIfAbsent(key, k -> new ArrayList<>(1));
        if (values.isEmpty()) {
            values.add(createNode(key, value));
        } else {
            values.get(values.size() - 1).set(key, value);
        }
    }

    public void setAll(K key, V value) {
        List<OrderedNode> values = backingMap.computeIfAbsent(key, k -> new ArrayList<>(1));
        if (values.isEmpty()) {
            values.add(createNode(key, value));
        } else {
            for (OrderedNode node : values) {
                node.set(key, value);
            }
        }
    }

    public void setFirst(K key, V value) {
        List<OrderedNode> values = backingMap.computeIfAbsent(key, k -> new ArrayList<>(1));
        if (values.isEmpty()) {
            values.add(createNode(key, value));
        } else {
            values.get(0).set(key, value);
        }
    }

    public void setOnly(K key, V value) {
        List<OrderedNode> values = backingMap.computeIfAbsent(key, k -> new ArrayList<>(1));
        OrderedNode node;
        if (values.isEmpty()) {
            node = createNode(key, value);
        } else {
            node = values.get(0);
            node.set(key, value);
            nodeCount -= values.size();
            values.clear();
        }
        values.add(node);
        nodeCount++;
    }

    public void add(K key, V value) {
        List<OrderedNode> values = backingMap.computeIfAbsent(key, k -> new ArrayList<>(1));
        values.add(createNode(key, value));
    }

    public void set(K key, V value, SetItemPlacement itemPlacement) {
        switch (itemPlacement) {
            case First -> setFirst(key, value);
            case Last -> setLast(key, value);
            case New -> add(key, value);
            case All -> setAll(key, value);
            case Only -> setOnly(key, value);
        }
    }

    public V getFirst(K key) {
        List<OrderedNode> nodes = backingMap.get(key);
        return CollectionUtils.hasAny(nodes) ? nodes.get(0).getValue() : null;
    }

    public V getLast(K key) {
        List<OrderedNode> nodes = backingMap.get(key);
        return CollectionUtils.hasAny(nodes) ? nodes.get(nodes.size() - 1).getValue() : null;
    }

    public V get(K key, GetItemPlacement itemPlacement) {
        return switch (itemPlacement) {
            case First -> getFirst(key);
            case Last -> getLast(key);
        };
    }

    public List<Pair<K, V>> entries() {
        return backingMap.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .sorted(Comparator.comparingInt(OrderedNode::getOrder))
                .map(node -> Pair.of(node.getKey(), node.getValue()))
                .collect(Collectors.toList());
    }

    public <T> List<T> entries(BiFunction<K, V, T> entryGetter) {
        return backingMap.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .sorted(Comparator.comparingInt(OrderedNode::getOrder))
                .map(node -> entryGetter.apply(node.getKey(), node.getValue()))
                .collect(Collectors.toList());
    }

    private OrderedNode createNode(K key, V value) {
        return new OrderedNode(key, value, ++nodeCount);
    }

    public void removeAll(K key) {
        List<OrderedNode> nodes = backingMap.get(key);
        if (nodes != null) {
            nodeCount -= nodes.size();
            backingMap.remove(key);
        }
    }

    public void removeFirst(K key) {
        List<OrderedNode> nodes = backingMap.get(key);
        if (CollectionUtils.hasAny(nodes)) {
            if (nodes.size() == 1) {
                backingMap.remove(key);
            } else {
                nodes.remove(0);
            }
            nodeCount--;
        }
    }

    public void removeLast(K key) {
        List<OrderedNode> nodes = backingMap.get(key);
        if (CollectionUtils.hasAny(nodes)) {
            if (nodes.size() == 1) {
                backingMap.remove(key);
            } else {
                nodes.remove(nodes.size() - 1);
            }
            nodeCount--;
        }
    }

    public void remove(K key, DeleteItemPlacement itemPlacement) {
        switch (itemPlacement) {
            case First -> removeFirst(key);
            case Last -> removeLast(key);
            case All -> removeAll(key);
        }
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

        public void set(K key, V value) {
            this.key = key;
            this.value = value;
        }
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
