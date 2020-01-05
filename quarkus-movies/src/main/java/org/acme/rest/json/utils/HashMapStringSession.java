package org.acme.rest.json.utils;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;

import org.acme.rest.json.Session;

//import org.graalvm.nativeimage.Inline;

public class HashMapStringSession implements Map<String, Session> {

	static class Node implements Map.Entry<String,Session> {
        final int hash;
        final String key;
        /*@Inline*/ Session value;
        Node next;

        Node(int hash, String key, Session value, Node next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final String getKey()        { return key; }
        public final Session getValue()      { return value; }
        public final String toString() { return key + "=" + value; }

        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        public final Session setValue(Session newValue) {
            Session oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final boolean equals(Object o) {
            if (o == this)
                return true;
            if (o instanceof Map.Entry) {
                Map.Entry<?,?> e = (Map.Entry<?,?>)o;
                if (Objects.equals(key, e.getKey()) &&
                    Objects.equals(value, e.getValue()))
                    return true;
            }
            return false;
        }
    }
	
    abstract class HashIterator {
        Node next;        // next entry to return
        Node current;     // current entry
        int expectedModCount;  // for fast-fail
        int index;             // current slot

        HashIterator() {
            expectedModCount = modCount;
            Node[] t = table;
            current = next = null;
            index = 0;
            if (t != null && size > 0) { // advance to first entry
                do {} while (index < t.length && (next = t[index++]) == null);
            }
        }

        public final boolean hasNext() {
            return next != null;
        }

        final Node nextNode() {
            Node[] t;
            Node e = next;
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            if (e == null)
                throw new NoSuchElementException();
            if ((next = (current = e).next) == null && (t = table) != null) {
                do {} while (index < t.length && (next = t[index++]) == null);
            }
            return e;
        }

        public final void remove() {
            Node p = current;
            if (p == null)
                throw new IllegalStateException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            current = null;
            String key = p.key;
            removeNode(hash(key), key, null, false, false);
            expectedModCount = modCount;
        }
    }
	
    final class KeyIterator extends HashIterator implements Iterator<String> {
    	public final String next() { return nextNode().key; }
    }
    
    final class ValueIterator extends HashIterator implements Iterator<Session> {
    	public final Session next() { return nextNode().value; }
    }
    
    final class EntryIterator extends HashIterator implements Iterator<Map.Entry<String,Session>> {
    	public final Map.Entry<String,Session> next() { return nextNode(); }
    }
    
    static class HashMapSpliterator {
        final HashMapStringSession map;
        Node current;          // current node
        int index;                  // current index, modified on advance/split
        int fence;                  // one past last index
        int est;                    // size estimate
        int expectedModCount;       // for comodification checks

        HashMapSpliterator(HashMapStringSession m, int origin,
                           int fence, int est,
                           int expectedModCount) {
            this.map = m;
            this.index = origin;
            this.fence = fence;
            this.est = est;
            this.expectedModCount = expectedModCount;
        }

        final int getFence() { // initialize fence and size on first use
            int hi;
            if ((hi = fence) < 0) {
            	HashMapStringSession m = map;
                est = m.size;
                expectedModCount = m.modCount;
                Node[] tab = m.table;
                hi = fence = (tab == null) ? 0 : tab.length;
            }
            return hi;
        }

        public final long estimateSize() {
            getFence(); // force init
            return (long) est;
        }
    }

    static final class KeySpliterator extends HashMapSpliterator implements Spliterator<String> {
        KeySpliterator(HashMapStringSession m, int origin, int fence, int est,
                       int expectedModCount) {
            super(m, origin, fence, est, expectedModCount);
        }

        public KeySpliterator trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid || current != null) ? null :
                new KeySpliterator(map, lo, index = mid, est >>>= 1,
                                        expectedModCount);
        }

        public void forEachRemaining(Consumer<? super String> action) {
            int i, hi, mc;
            if (action == null)
                throw new NullPointerException();
            HashMapStringSession m = map;
            Node[] tab = m.table;
            if ((hi = fence) < 0) {
                mc = expectedModCount = m.modCount;
                hi = fence = (tab == null) ? 0 : tab.length;
            }
            else
                mc = expectedModCount;
            if (tab != null && tab.length >= hi &&
                (i = index) >= 0 && (i < (index = hi) || current != null)) {
                Node p = current;
                current = null;
                do {
                    if (p == null)
                        p = tab[i++];
                    else {
                        action.accept(p.key);
                        p = p.next;
                    }
                } while (p != null || i < hi);
                if (m.modCount != mc)
                    throw new ConcurrentModificationException();
            }
        }

        public boolean tryAdvance(Consumer<? super String> action) {
            int hi;
            if (action == null)
                throw new NullPointerException();
            Node[] tab = map.table;
            if (tab != null && tab.length >= (hi = getFence()) && index >= 0) {
                while (current != null || index < hi) {
                    if (current == null)
                        current = tab[index++];
                    else {
                        String k = current.key;
                        current = current.next;
                        action.accept(k);
                        if (map.modCount != expectedModCount)
                            throw new ConcurrentModificationException();
                        return true;
                    }
                }
            }
            return false;
        }

        public int characteristics() {
            return (fence < 0 || est == map.size ? Spliterator.SIZED : 0) |
                Spliterator.DISTINCT;
        }
    }
	
    final class KeySet extends AbstractSet<String> {
        public final int size()                 	{ return size; }
        public final void clear()               	{ HashMapStringSession.this.clear(); }
        public final Iterator<String> iterator()     { return new KeyIterator(); }
        public final boolean contains(Object o) { return containsKey(o); }
        public final boolean remove(Object key) {
            return removeNode(hash(key), key, null, false, true) != null;
        }
        public final Spliterator<String> spliterator() {
            return new KeySpliterator(HashMapStringSession.this, 0, -1, 0, 0);
        }
        public final void forEach(Consumer<? super String> action) {
            Node[] tab;
            if (action == null)
                throw new NullPointerException();
            if (size > 0 && (tab = table) != null) {
                int mc = modCount;
                for (int i = 0; i < tab.length; ++i) {
                    for (Node e = tab[i]; e != null; e = e.next)
                        action.accept(e.key);
                }
                if (modCount != mc)
                    throw new ConcurrentModificationException();
            }
        }
    }
    
    static final class ValueSpliterator extends HashMapSpliterator implements Spliterator<Session> {
	    ValueSpliterator(HashMapStringSession m, int origin, int fence, int est,
	                     int expectedModCount) {
	        super(m, origin, fence, est, expectedModCount);
	    }
	
	    public ValueSpliterator trySplit() {
	        int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
	        return (lo >= mid || current != null) ? null :
	            new ValueSpliterator(map, lo, index = mid, est >>>= 1,
	                                      expectedModCount);
	    }
	
	    public void forEachRemaining(Consumer<? super Session> action) {
	        int i, hi, mc;
	        if (action == null)
	            throw new NullPointerException();
	        HashMapStringSession m = map;
	        Node[] tab = m.table;
	        if ((hi = fence) < 0) {
	            mc = expectedModCount = m.modCount;
	            hi = fence = (tab == null) ? 0 : tab.length;
	        }
	        else
	            mc = expectedModCount;
	        if (tab != null && tab.length >= hi &&
	            (i = index) >= 0 && (i < (index = hi) || current != null)) {
	            Node p = current;
	            current = null;
	            do {
	                if (p == null)
	                    p = tab[i++];
	                else {
	                    action.accept(p.value);
	                    p = p.next;
	                }
	            } while (p != null || i < hi);
	            if (m.modCount != mc)
	                throw new ConcurrentModificationException();
	        }
	    }
	
	    public boolean tryAdvance(Consumer<? super Session> action) {
	        int hi;
	        if (action == null)
	            throw new NullPointerException();
	        Node[] tab = map.table;
	        if (tab != null && tab.length >= (hi = getFence()) && index >= 0) {
	            while (current != null || index < hi) {
	                if (current == null)
	                    current = tab[index++];
	                else {
	                    Session v = current.value;
	                    current = current.next;
	                    action.accept(v);
	                    if (map.modCount != expectedModCount)
	                        throw new ConcurrentModificationException();
	                    return true;
	                }
	            }
	        }
	        return false;
	    }
	
	    public int characteristics() {
	        return (fence < 0 || est == map.size ? Spliterator.SIZED : 0);
	    }
	}
    
    final class Values extends AbstractCollection<Session> {
        public final int size()						{ return size; }
        public final void clear()               	{ HashMapStringSession.this.clear(); }
        public final Iterator<Session> iterator()  	{ return new ValueIterator(); }
        public final boolean contains(Object o) { return containsValue(o); }
        public final Spliterator<Session> spliterator() {
            return new ValueSpliterator(HashMapStringSession.this, 0, -1, 0, 0);
        }
        public final void forEach(Consumer<? super Session> action) {
            Node[] tab;
            if (action == null)
                throw new NullPointerException();
            if (size > 0 && (tab = table) != null) {
                int mc = modCount;
                for (int i = 0; i < tab.length; ++i) {
                    for (Node e = tab[i]; e != null; e = e.next)
                        action.accept(e.value);
                }
                if (modCount != mc)
                    throw new ConcurrentModificationException();
            }
        }
    }
    
    static final class EntrySpliterator extends HashMapSpliterator implements Spliterator<Map.Entry<String,Session>> {
    EntrySpliterator(HashMapStringSession m, int origin, int fence, int est,
                     int expectedModCount) {
        super(m, origin, fence, est, expectedModCount);
    }

    public EntrySpliterator trySplit() {
        int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
        return (lo >= mid || current != null) ? null :
            new EntrySpliterator(map, lo, index = mid, est >>>= 1,
                                      expectedModCount);
    }

    public void forEachRemaining(Consumer<? super Map.Entry<String,Session>> action) {
        int i, hi, mc;
        if (action == null)
            throw new NullPointerException();
        HashMapStringSession m = map;
        Node[] tab = m.table;
        if ((hi = fence) < 0) {
            mc = expectedModCount = m.modCount;
            hi = fence = (tab == null) ? 0 : tab.length;
        }
        else
            mc = expectedModCount;
        if (tab != null && tab.length >= hi &&
            (i = index) >= 0 && (i < (index = hi) || current != null)) {
            Node p = current;
            current = null;
            do {
                if (p == null)
                    p = tab[i++];
                else {
                    action.accept(p);
                    p = p.next;
                }
            } while (p != null || i < hi);
            if (m.modCount != mc)
                throw new ConcurrentModificationException();
        }
    }

    public boolean tryAdvance(Consumer<? super Map.Entry<String,Session>> action) {
        int hi;
        if (action == null)
            throw new NullPointerException();
        Node[] tab = map.table;
        if (tab != null && tab.length >= (hi = getFence()) && index >= 0) {
            while (current != null || index < hi) {
                if (current == null)
                    current = tab[index++];
                else {
                    Node e = current;
                    current = current.next;
                    action.accept(e);
                    if (map.modCount != expectedModCount)
                        throw new ConcurrentModificationException();
                    return true;
                }
            }
        }
        return false;
    }

    public int characteristics() {
        return (fence < 0 || est == map.size ? Spliterator.SIZED : 0) |
            Spliterator.DISTINCT;
    }
}
    
    final class EntrySet extends AbstractSet<Map.Entry<String,Session>> {
        public final int size()                 { return size; }
        public final void clear()               { HashMapStringSession.this.clear(); }
        public final Iterator<Map.Entry<String,Session>> iterator() {
            return new EntryIterator();
        }
        public final boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> e = (Map.Entry<?,?>) o;
            Object key = e.getKey();
            Node candidate = getNode(hash(key), key);
            return candidate != null && candidate.equals(e);
        }
        public final boolean remove(Object o) {
            if (o instanceof Map.Entry) {
                Map.Entry<?,?> e = (Map.Entry<?,?>) o;
                Object key = e.getKey();
                Object value = e.getValue();
                return removeNode(hash(key), key, value, true, true) != null;
            }
            return false;
        }
        public final Spliterator<Map.Entry<String,Session>> spliterator() {
            return new EntrySpliterator(HashMapStringSession.this, 0, -1, 0, 0);
        }
        public final void forEach(Consumer<? super Map.Entry<String,Session>> action) {
            Node[] tab;
            if (action == null)
                throw new NullPointerException();
            if (size > 0 && (tab = table) != null) {
                int mc = modCount;
                for (int i = 0; i < tab.length; ++i) {
                    for (Node e = tab[i]; e != null; e = e.next)
                        action.accept(e);
                }
                if (modCount != mc)
                    throw new ConcurrentModificationException();
            }
        }
    }
	
    int size;
    
    Node[] table;
    
    int modCount;
    
    int threshold;
    
    float loadFactor;
    
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16

    static final int MAXIMUM_CAPACITY = 1 << 30;

    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    static final int TREEIFY_THRESHOLD = 8;

    static final int UNTREEIFY_THRESHOLD = 6;

    static final int MIN_TREEIFY_CAPACITY = 64;
    
    public HashMapStringSession(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);
        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);
    }

    public HashMapStringSession(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public HashMapStringSession() {
        this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted
    }
    
	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
    
    final Node getNode(int hash, Object key) {
        Node[] tab; Node first, e; int n; String k;
        if ((tab = table) != null && (n = tab.length) > 0 && (first = tab[(n - 1) & hash]) != null) {
            if (first.hash == hash &&  ((k = first.key) == key || (key != null && key.equals(k))))
                return first;
            if ((e = first.next) != null) {
                do {
                    if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k))))
                        return e;
                } while ((e = e.next) != null);
            }
        }
        return null;
    }
	
	@Override
	public boolean containsKey(Object key) {
		return getNode(hash(key), key) != null;
	}

	@Override
	public boolean containsValue(Object value) {
        Node[] tab; Session v;
        if ((tab = table) != null && size > 0) {
            for (int i = 0; i < tab.length; ++i) {
                for (Node e = tab[i]; e != null; e = e.next) {
                    if ((v = e.value) == value ||
                        (value != null && value.equals(v)))
                        return true;
                }
            }
        }
        return false;
	}

	@Override
	public Session get(Object key) {
        Node e;
        return (e = getNode(hash(key), key)) == null ? null : e.value;
	}

	final Node[] resize() {
        Node[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;
        if (oldCap > 0) {
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                     oldCap >= DEFAULT_INITIAL_CAPACITY)
                newThr = oldThr << 1; // double threshold
        }
        else if (oldThr > 0) // initial capacity was placed in threshold
            newCap = oldThr;
        else {               // zero initial threshold signifies using defaults
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        if (newThr == 0) {
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                      (int)ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
        @SuppressWarnings({"rawtypes","unchecked"})
        Node[] newTab = (Node[])new Node[newCap];
        table = newTab;
        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                Node e;
                if ((e = oldTab[j]) != null) {
                    oldTab[j] = null;
                    if (e.next == null)
                        newTab[e.hash & (newCap - 1)] = e;
                    else { // preserve order
                        Node loHead = null, loTail = null;
                        Node hiHead = null, hiTail = null;
                        Node next;
                        do {
                            next = e.next;
                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }
	
	Node newNode(int hash, String key, Session value, Node next) {
        return new Node(hash, key, value, next);
    }
	
    final Session putVal(int hash, String key, Session value, boolean onlyIfAbsent, boolean evict) {
		 Node[] tab; Node p; int n, i;
		 if ((tab = table) == null || (n = tab.length) == 0)
		     n = (tab = resize()).length;
		 if ((p = tab[i = (n - 1) & hash]) == null)
		     tab[i] = newNode(hash, key, value, null);
		 else {
		     Node e; String k;
		     if (p.hash == hash && ((k = p.key) == key || (key != null && key.equals(k))))
		         e = p;
		     else {
		         for (int binCount = 0; ; ++binCount) {
		             if ((e = p.next) == null) {
		                 p.next = newNode(hash, key, value, null);
		                 break;
		             }
		             if (e.hash == hash &&
		                 ((k = e.key) == key || (key != null && key.equals(k))))
		                 break;
		             p = e;
		         }
		     }
		     if (e != null) { // existing mapping for key
		         Session oldValue = e.value;
		         if (!onlyIfAbsent || oldValue == null)
		             e.value = value;
		         return oldValue;
		     }
		 }
		 ++modCount;
		 if (++size > threshold)
		     resize();
		 return null;
    }
	
	@Override
	public Session put(String key, Session value) {
		return putVal(hash(key), key, value, false, true);
	}

    final Node removeNode(int hash, Object key, Object value, boolean matchValue, boolean movable) {
    	Node[] tab; Node p; int n, index;
    	if ((tab = table) != null && (n = tab.length) > 0 && (p = tab[index = (n - 1) & hash]) != null) {
    		Node node = null, e; String k; Session v;
    		if (p.hash == hash && ((k = p.key) == key || (key != null && key.equals(k)))) {
    			node = p;
    		}
    		else if ((e = p.next) != null) {
				do {
					if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k)))) {
						node = e;
						break;
					}
					p = e;
				} while ((e = e.next) != null);
    			
    		}
    		if (node != null && (!matchValue || (v = node.value) == value || (value != null && value.equals(v)))) {
    			if (node == p)
    				tab[index] = node.next;
    			else
    				p.next = node.next;
    			++modCount;
    			--size;
    			return node;
    		}
    	}
    	return null;
    }
	
	@Override
	public Session remove(Object key) {
        Node e;
        return (e = removeNode(hash(key), key, null, false, true)) == null ?
            null : e.value;
	}

    static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }
	
    final void putMapEntries(Map<? extends String, ? extends Session> m, boolean evict) {
        int s = m.size();
        if (s > 0) {
            if (table == null) { // pre-size
                float ft = ((float)s / loadFactor) + 1.0F;
                int t = ((ft < (float)MAXIMUM_CAPACITY) ?
                         (int)ft : MAXIMUM_CAPACITY);
                if (t > threshold)
                    threshold = tableSizeFor(t);
            }
            else if (s > threshold)
                resize();
            for (Map.Entry<? extends String, ? extends Session> e : m.entrySet()) {
                String key = e.getKey();
                Session value = e.getValue();
                putVal(hash(key), key, value, false, evict);
            }
        }
    }
	
	@Override
	public void putAll(Map<? extends String, ? extends Session> m) {
		putMapEntries(m, true);		
	}

	@Override
	public void clear() {
        Node[] tab;
        modCount++;
        if ((tab = table) != null && size > 0) {
            size = 0;
            for (int i = 0; i < tab.length; ++i)
                tab[i] = null;
        }
	}

	@Override
	public Set<String> keySet() {
        return new KeySet();
	}

	@Override
	public Collection<Session> values() {
        return new Values();
	}

	@Override
	public Set<Entry<String, Session>> entrySet() {
        return new EntrySet();
	}
	
}