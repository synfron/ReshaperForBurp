package synfron.reshaper.burp.core.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import synfron.reshaper.burp.core.rules.DeleteItemPlacement;
import synfron.reshaper.burp.core.rules.GetItemPlacement;
import synfron.reshaper.burp.core.rules.SetItemPlacement;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ListMap<K, V> {
    private int nodeCount;
    private final HashMap<K, List<OrderedNode>> backingMap = new HashMap<>();

    public void setLastOrAdd(K key, V value) {
        List<OrderedNode> values = backingMap.computeIfAbsent(key, k -> new ArrayList<>(1));
        if (values.isEmpty()) {
            values.add(createNode(key, value));
        } else {
            values.get(values.size() - 1).set(key, value);
        }
    }

    public V getWhere(K key, Predicate<V> predicate, GetItemPlacement itemPlacement) {
        return switch (itemPlacement) {
            case First -> getFirstWhere(key, predicate);
            case Last -> getLastWhere(key, predicate);
        };
    }

    public void computeWhereOrAdd(K key, Predicate<V> predicate, Function<V, V> compute, SetItemPlacement itemPlacement) {
        switch (itemPlacement) {
            case First -> computeFirstWhereOrAdd(key, predicate, compute);
            case Last -> computeLastWhereOrAdd(key, predicate, compute);
            case Only -> computeOnlyWhereOrAdd(key, predicate, compute);
            case New -> add(key, compute.apply(null));
            case All -> computeAllWhereOrAdd(key, predicate, compute);
        }
    }

    public void computeLastWhereOrAdd(K key, Predicate<V> predicate, Function<V, V> compute) {
        List<OrderedNode> values = backingMap.computeIfAbsent(key, k -> new ArrayList<>(1)).stream()
                .filter(node -> predicate.test(node.getValue()))
                .toList();
        if (values.isEmpty()) {
            values.add(createNode(key, compute.apply(null)));
        } else {
            OrderedNode node = values.get(values.size() - 1);
            V value = node.getValue();
            V newValue = compute.apply(value);
            if (value != newValue) {
                node.setValue(newValue);
            }
        }
    }

    public V getLastWhere(K key, Predicate<V> predicate) {
        List<OrderedNode> values = backingMap.computeIfAbsent(key, k -> new ArrayList<>(1)).stream()
                .filter(node -> predicate.test(node.getValue()))
                .toList();
        if (!values.isEmpty()) {
            OrderedNode node = values.get(values.size() - 1);
            return node.getValue();
        }
        return null;
    }

    public void computeFirstWhereOrAdd(K key, Predicate<V> predicate, Function<V, V> compute) {
        List<OrderedNode> values = backingMap.computeIfAbsent(key, k -> new ArrayList<>(1)).stream()
                .filter(node -> predicate.test(node.getValue()))
                .toList();
        if (values.isEmpty()) {
            values.add(createNode(key, compute.apply(null)));
        } else {
            OrderedNode node = values.getFirst();
            V value = node.getValue();
            V newValue = compute.apply(value);
            if (value != newValue) {
                node.setValue(newValue);
            }
        }
    }

    public V getFirstWhere(K key, Predicate<V> predicate) {
        List<OrderedNode> values = backingMap.computeIfAbsent(key, k -> new ArrayList<>(1)).stream()
                .filter(node -> predicate.test(node.getValue()))
                .toList();
        if (!values.isEmpty()) {
            OrderedNode node = values.getFirst();
            return node.getValue();
        }
        return null;
    }

    public void computeAllWhereOrAdd(K key, Predicate<V> predicate, Function<V, V> compute) {
        List<OrderedNode> values = backingMap.computeIfAbsent(key, k -> new ArrayList<>(1)).stream()
                .filter(node -> predicate.test(node.getValue()))
                .toList();
        if (values.isEmpty()) {
            values.add(createNode(key, compute.apply(null)));
        } else {
            values.forEach(node -> {
                V value = node.getValue();
                V newValue = compute.apply(value);
                if (value != newValue) {
                    node.setValue(newValue);
                }
            });
        }
    }

    public void computeOnlyWhereOrAdd(K key, Predicate<V> predicate, Function<V, V> compute) {
        List<OrderedNode> values = backingMap.computeIfAbsent(key, k -> new ArrayList<>(1)).stream()
                .filter(node -> predicate.test(node.getValue()))
                .toList();
        if (values.isEmpty()) {
            values.add(createNode(key, compute.apply(null)));
        } else {
            List<OrderedNode> nodesToRemove = CollectionUtils.subList(values, 1, values.size() - 1);
            backingMap.get(key).removeAll(nodesToRemove);
            OrderedNode node = values.getFirst();
            V value = node.getValue();
            V newValue = compute.apply(value);
            if (value != newValue) {
                node.setValue(newValue);
            }
            nodeCount -= nodesToRemove.size();
        }
    }

    public void setAllOrAdd(K key, V value) {
        List<OrderedNode> values = backingMap.computeIfAbsent(key, k -> new ArrayList<>(1));
        if (values.isEmpty()) {
            values.add(createNode(key, value));
        } else {
            for (OrderedNode node : values) {
                node.set(key, value);
            }
        }
    }

    public void setFirstOrAdd(K key, V value) {
        List<OrderedNode> values = backingMap.computeIfAbsent(key, k -> new ArrayList<>(1));
        if (values.isEmpty()) {
            values.add(createNode(key, value));
        } else {
            values.getFirst().set(key, value);
        }
    }

    public void setOnly(K key, V value) {
        List<OrderedNode> values = backingMap.computeIfAbsent(key, k -> new ArrayList<>(1));
        OrderedNode node;
        if (values.isEmpty()) {
            node = createNode(key, value);
        } else {
            node = values.getFirst();
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

    public void setOrAdd(K key, V value, SetItemPlacement itemPlacement) {
        switch (itemPlacement) {
            case First -> setFirstOrAdd(key, value);
            case Last -> setLastOrAdd(key, value);
            case New -> add(key, value);
            case All -> setAllOrAdd(key, value);
            case Only -> setOnly(key, value);
        }
    }

    public V getFirst(K key) {
        List<OrderedNode> nodes = backingMap.get(key);
        return CollectionUtils.hasAny(nodes) ? nodes.getFirst().getValue() : null;
    }

    public V getLast(K key) {
        List<OrderedNode> nodes = backingMap.get(key);
        return CollectionUtils.hasAny(nodes) ? nodes.get(nodes.size() - 1).getValue() : null;
    }

    public List<V> getAll(K key) {
        return CollectionUtils.defaultIfNull(backingMap.get(key)).stream()
                .map(OrderedNode::getValue)
                .toList();
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

    public void removeAllWhere(K key, Predicate<V> predicate) {
        List<OrderedNode> nodes = backingMap.get(key);
        if (nodes != null) {
            List<OrderedNode> removeNodes = nodes.stream().filter(node -> predicate.test(node.getValue())).toList();
            nodeCount -= removeNodes.size();
            if (nodes.size() == removeNodes.size()) {
                backingMap.remove(key);
            } else {
                nodes.removeAll(removeNodes);
            }
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

    public void removeFirstWhere(K key, Predicate<V> predicate) {
        List<OrderedNode> nodes = backingMap.get(key);
        if (nodes != null) {
            List<OrderedNode> removeNodes = nodes.stream().filter(node -> predicate.test(node.getValue())).toList();
            if (!removeNodes.isEmpty()) {
                nodeCount -= 1;
                nodes.remove(removeNodes.getFirst());
                if (nodes.isEmpty()) {
                    backingMap.remove(key);
                }
            }
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

    public void removeLastWhere(K key, Predicate<V> predicate) {
        List<OrderedNode> nodes = backingMap.get(key);
        if (nodes != null) {
            List<OrderedNode> removeNodes = nodes.stream().filter(node -> predicate.test(node.getValue())).toList();
            if (!removeNodes.isEmpty()) {
                nodeCount -= 1;
                nodes.remove(removeNodes.get(removeNodes.size() - 1));
                if (nodes.isEmpty()) {
                    backingMap.remove(key);
                }
            }
        }
    }

    public void remove(K key, DeleteItemPlacement itemPlacement) {
        switch (itemPlacement) {
            case First -> removeFirst(key);
            case Last -> removeLast(key);
            case All -> removeAll(key);
        }
    }

    public void removeWhere(K key, Predicate<V> predicate, DeleteItemPlacement itemPlacement) {
        switch (itemPlacement) {
            case First -> removeFirstWhere(key, predicate);
            case Last -> removeLastWhere(key, predicate);
            case All -> removeAllWhere(key, predicate);
        }
    }

    public boolean containsKey(K key) {
        return backingMap.containsKey(key);
    }

    public Set<K> keys() {
        return backingMap.keySet();
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
