package ru.job4j.tree;

import java.util.*;

public class SimpleTree<E> implements Tree<E> {
    private final Node<E> root;

    public SimpleTree(final E root) {
        this.root = new Node<>(root);
    }

    @Override
    public boolean add(E parent, E child) {
        boolean rsl = false;
        boolean isChild = false;
        int index = 0;
        List<Node<E>> searchChild = new ArrayList<>();
        searchChild.add(root);
        while (index < searchChild.size()) {
            if (searchChild.get(index).value == child) {
                isChild = true;
                break;
            }
            searchChild.addAll(searchChild.get(index).children);
            index++;
        }
        Optional<Node<E>> node = findBy(parent);
        if (!isChild && node.isPresent() && !node.get().children.contains(child)) {
            rsl = node.get().children.add(new Node<>(child));
        }
        return rsl;
    }

    @Override
    public Optional<Node<E>> findBy(E value) {
        Optional<Node<E>> rsl = Optional.empty();
        Queue<Node<E>> data = new LinkedList<>();
        data.offer(this.root);
        while (!data.isEmpty()) {
            Node<E> el = data.poll();
            if (el.value.equals(value)) {
                rsl = Optional.of(el);
                break;
            }
            data.addAll(el.children);
        }
        return rsl;
    }
}