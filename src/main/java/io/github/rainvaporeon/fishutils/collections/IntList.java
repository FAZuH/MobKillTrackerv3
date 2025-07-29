package io.github.rainvaporeon.fishutils.collections;

import io.github.rainvaporeon.fishutils.misc.annotations.DelegatesToShadow;

import java.util.*;

@DelegatesToShadow
public class IntList implements List<Integer> {
    @DelegatesToShadow.Target
    private final List<Integer> back;

    public IntList() { this.back = new ArrayList<>(); }
    public IntList(int capacity) { this.back = new ArrayList<>(capacity); }

    @Override
    public int size() {
        return back.size();
    }

    @Override
    public boolean isEmpty() {
        return back.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return back.contains(o);
    }

    @Override
    public Iterator<Integer> iterator() {
        return back.iterator();
    }

    @Override
    public Object[] toArray() {
        return back.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return back.toArray(a);
    }

    public int[] toIntArray() {
        int[] arr = new int[size()];
        for(int i = 0; i < arr.length; i++) {
            arr[i] = getAsInt(i);
        }
        return arr;
    }

    @Override
    public boolean add(Integer integer) {
        return add(integer.intValue());
    }

    public boolean add(int integer) {
        return back.add(integer);
    }

    @Override
    public boolean remove(Object o) {
        return back.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return new HashSet<>(back).containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Integer> c) {
        return back.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Integer> c) {
        return back.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return back.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return back.retainAll(c);
    }

    @Override
    public void clear() {
        back.clear();
    }

    @Override
    public Integer get(int index) {
        return back.get(index);
    }

    public int getAsInt(int index) {
        return back.get(index);
    }

    @Override
    public Integer set(int index, Integer element) {
        return set(index, element.intValue());
    }

    public int set(int index, int element) {
        return back.set(index, element);
    }

    @Override
    public void add(int index, Integer element) {
        add(index, element.intValue());
    }

    public void add(int index, int element) {
        back.add(index, element);
    }

    @Override
    public Integer remove(int index) {
        return back.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return back.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return back.lastIndexOf(o);
    }

    @Override
    public ListIterator<Integer> listIterator() {
        return back.listIterator();
    }

    @Override
    public ListIterator<Integer> listIterator(int index) {
        return back.listIterator(index);
    }

    @Override
    public List<Integer> subList(int fromIndex, int toIndex) {
        return back.subList(fromIndex, toIndex);
    }
}
