package main.yefancy.lfz.datacube.api.tree;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class SharedLinkMap implements Map<Integer, Boolean> {
    private long[] longArray;

    public SharedLinkMap(){
        longArray = new long[]{0L};
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object o) {
        return false;
    }

    @Override
    public boolean containsValue(Object o) {
        return false;
    }

    @Override
    public Boolean get(Object o) {
        int index = (int) o;
        if (index >= longArray.length * 64) {
            return false;
        }
        return (longArray[index / 64] & (1L << (index % 63))) != 0;
    }

    @Override
    public Boolean put(Integer index, Boolean shared) {
        if (index >= longArray.length * 64) {
           long[] newArray = new long[index / 64 + 1];
           System.arraycopy(longArray, 0, newArray, 0, longArray.length);
           longArray = newArray;
        }
        if (shared) {
            longArray[index / 64] |= (1L << (index % 63));
        } else {
            try {
                longArray[index / 64] &= ~(1L << (index % 63));
            } catch (Exception e2){
                System.out.println();
            }
        }
        return null;
    }

    @Override
    public Boolean remove(Object o) {
        return null;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Boolean> map) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<Integer> keySet() {
        return null;
    }

    @Override
    public Collection<Boolean> values() {
        return null;
    }

    @Override
    public Set<Entry<Integer, Boolean>> entrySet() {
        return null;
    }
}
