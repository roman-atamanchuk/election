package org.example.util;



// The list for Hash Table buckets
public class CustomLinkedList<T> {
    private Node<T> head;

    public void add(T item) {
        Node<T> newNode = new Node<>(item);
        if (head == null) {
            head = newNode;
        } else {
            Node<T> temp = head;
            while (temp.next != null) temp = temp.next;
            temp.next = newNode;
        }
    }

    public Node<T> getHead() { return head; }
}