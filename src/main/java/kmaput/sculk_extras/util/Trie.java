package kmaput.sculk_extras.util;

import javax.annotation.Nullable;
import java.util.HashMap;

public class Trie<K, V> {
    private final Node root = new Node();

    public void put(Iterable<K> path, V value) {
        Node current = root;
        for (K element : path) {
            current = current.children.compute(element, (k, v) -> v == null ? new Node() : v);
        }
        current.value = value;
    }

    @Nullable
    public V getBestMatching(Iterable<K> path) {
        Node current = root;
        V found = null;
        for (K element : path) {
            current = current.children.get(element);
            if (current == null) break;
            if (current.value != null) found = current.value;
        }
        return found;
    }

    private class Node {
        private V value;
        private final HashMap<K, Node> children = new HashMap<>();
    }
}
