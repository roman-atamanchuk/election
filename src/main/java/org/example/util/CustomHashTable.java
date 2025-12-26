package org.example.util;

import org.example.model.Politician;
import org.example.model.Election;

public class CustomHashTable<T> {
    private CustomLinkedList<T>[] table;

    @SuppressWarnings("unchecked")
    public CustomHashTable(int capacity) {
        table = (CustomLinkedList<T>[]) new CustomLinkedList[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new CustomLinkedList<>();
        }
    }

    private int hash(String key) {
        int hash = 7;
        for (int i = 0; i < key.length(); i++) {
            hash = hash * 31 + key.charAt(i);
        }
        return Math.abs(hash) % table.length;
    }

    public void insert(String key, T value) {
        int index = hash(key);
        table[index].add(value);
    }

    // FIXED: Exact search for Politician or Election
    public T find(String key) {
        if (key == null) return null;

        int index = hash(key);
        Node<T> current = table[index].getHead();

        while (current != null) {
            // Logic for Politicians
            if (current.data instanceof Politician) {
                Politician p = (Politician) current.data;
                // Check if ID matches or Name matches exactly
                if (p.getId().equals(key) || p.getName().equalsIgnoreCase(key)) {
                    return current.data;
                }
            }
            // Logic for Elections
            else if (current.data instanceof Election) {
                Election e = (Election) current.data;
                // Check if ID matches exactly
                if (e.getId().equals(key)) {
                    return current.data;
                }
            }
            current = current.next;
        }
        return null;
    }
}
