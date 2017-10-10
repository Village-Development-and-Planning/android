package com.puthuvaazhvu.mapping.views.managers.operation;

import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.NoSuchElementException;

/*
Extension of Double-linked-list to store the fragment stack flow.
 */
public class Operation {
    private static final String TAG = Operation.class.getSimpleName();

    private Node head;
    private Node tail;
    private int size;

    public Operation() {
        size = 0;
    }

    /**
     * this class keeps track of each element information
     */
    private class Node {
        Fragment element;
        Node next;
        Node prev;

        public Node(Fragment element, Node next, Node prev) {
            this.element = element;
            this.next = next;
            this.prev = prev;
        }

        public boolean isHead() {
            return prev == null;
        }

        public boolean isTail() {
            return next == null;
        }

        @Override
        public String toString() {
            return element.getTag();
        }
    }

    public Fragment getHead() {
        return head.element;
    }

    public Fragment getTail() {
        return tail.element;
    }

    /**
     * returns the size of the linked list
     *
     * @return
     */
    public int size() {
        return size;
    }

    /**
     * return whether the list is empty or not
     *
     * @return
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * adds element at the starting of the linked list
     *
     * @param element
     */
    public void addFirst(Fragment element) {
        Node tmp = new Node(element, head, null);
        if (head != null) {
            head.prev = tmp;
        }
        head = tmp;
        if (tail == null) {
            tail = tmp;
        }
        size++;
        Log.i(TAG, "adding: " + element);
    }

    /**
     * adds element at the end of the linked list
     *
     * @param element
     */
    public void addLast(Fragment element) {

        Node tmp = new Node(element, null, tail);
        if (tail != null) {
            tail.next = tmp;
        }
        tail = tmp;
        if (head == null) {
            head = tmp;
        }
        size++;
        Log.i(TAG, "adding: " + element);
    }

    public Node findNode(Fragment element) {
        Node tmp = head;
        while (tmp != null) {
            if (tmp.element.getTag().equals(element.getTag()))
                return tmp;
            tmp = tmp.next;
        }
        throw new IllegalArgumentException("The node " + element.getTag() + " is not found");
    }

    /**
     * Removes a particular node from the double-linked list.
     * <p>
     * Algorithm
     * Find the node to remove.
     * node.previous.next = node.next
     * node.next.previous = node.previous
     * node.previous = null
     * node.next = null
     *
     * @param node The node to be removed
     */
    public void removeNode(Fragment node) {
        Node n = findNode(node);

        if (n.isHead() && n.isTail()) {
            head = null;
        } else if (n.isHead()) {
            head = n.next;
            head.prev = null;
        } else if (n.isTail()) {
            tail = n.prev;
            tail.next = null;
        } else {
            n.prev.next = n.next;
            n.next.prev = n.prev;
        }
        n.prev = null;
        n.next = null;
        size--;
    }

    /**
     * this method removes element from the start of the linked list
     *
     * @return
     */
    public Fragment removeFirst() {
        if (size == 0) throw new NoSuchElementException();
        Node tmp = head;
        head = head.next;
        head.prev = null;
        size--;
        Log.i(TAG, "deleted: " + tmp.element);
        return tmp.element;
    }

    /**
     * this method removes element from the end of the linked list
     *
     * @return
     */
    public Fragment removeLast() {
        if (size == 0) throw new NoSuchElementException();
        Node tmp = tail;
        tail = tail.prev;
        tail.next = null;
        size--;
        Log.i(TAG, "deleted: " + tmp.element);
        return tmp.element;
    }
}