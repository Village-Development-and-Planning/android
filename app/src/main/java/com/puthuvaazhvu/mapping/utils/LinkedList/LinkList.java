package com.puthuvaazhvu.mapping.utils.LinkedList;

public class LinkList<T> {

    private static class Node<T> {

        private final T data;
        private Node<T> next = null;

        public Node(T data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return data.toString();
        }
    }

    private Node<T> first = null;

    public void addFirst(T data) {
        Node<T> newFirst = new Node<T>(data);
        newFirst.next = first;
        first = newFirst;
    }

    public T removeFirst() {
        Node<T> oldFirst = first;
        first = first.next;
        return oldFirst.data;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Node current = first;
        while (current != null) {
            builder.append(current).append(" ");
            current = current.next;
        }
        return builder.toString();
    }

    public boolean isEmpty() {
        return first == null;
    }

}