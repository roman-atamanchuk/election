package org.example.util;

import java.io.Serializable;

// Adding Serializable is good practice for data structures
public class SimpleList<T> implements Serializable {
    private T[] data;
    private int size;

    @SuppressWarnings("unchecked")
    public SimpleList() {
        data = (T[]) new Object[50];
        size = 0;
    }

    // --- XML PERSISTENCE METHODS (CRITICAL) ---

    // Encoder uses this to get the array
    public T[] getData() {
        return data;
    }



    // Encoder uses this to know how many valid items are in the array
    public int getSize() {
        return size;
    }
    // Decoder uses this to restore the array
    public void setData(T[] data) {
        if (data != null) {
            // If the array being loaded is smaller than the required size,
            // we should keep our expanded array instead.
            if (this.data != null && data.length < this.size) {
                for (int i = 0; i < data.length; i++) {
                    this.data[i] = data[i];
                }
            } else {
                this.data = data;
            }
        }
    }

    // Decoder uses this to restore the size
    public void setSize(int size) {
        this.size = size;

        // CRITICAL FIX: If the loaded size is bigger than our current array capacity
        if (data == null || size > data.length) {
            // Create a new, larger array to hold the incoming XML data
            T[] newData = (T[]) new Object[size + 5];
            // Copy old data if it exists
            if (data != null) {
                for (int i = 0; i < data.length; i++) newData[i] = data[i];
            }
            this.data = newData;
        }
    }


    // --- EXISTING LOGIC METHODS ---

    public void add(T item) {
        if (size == data.length) resize();
        data[size++] = item;
    }

    public T get(int index) {
        if (index < 0 || index >= size) return null;
        return data[index];
    }

    public void set(int index, T item) {
        if (index >= 0 && index < size) data[index] = item;
    }

    // Note: We keep your size() method for your loops,
    // but the XML tools use getSize() above.
    public int size() { return size; }

    @SuppressWarnings("unchecked")
    private void resize() {
        T[] newData = (T[]) new Object[data.length * 2];
        for (int i = 0; i < size; i++) newData[i] = data[i];
        data = newData;
    }

    public void remove(int index) {
        if (index < 0 || index >= size) return;
        for (int i = index; i < size - 1; i++) data[i] = data[i + 1];
        data[--size] = null;
    }
}
