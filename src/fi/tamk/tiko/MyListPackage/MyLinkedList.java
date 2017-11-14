package fi.tamk.tiko.MyListPackage;

/**
 * MyLinkedList
 *
 * <p>LinkedList that accepts generic type content.</p>
 *
 * @param       <T> generic type parameter
 * @author      Jyri Virtaranta jyri.virtaranta@cs.tamk.fi
 * @version     2017.10.18
 * @since       1.8
 */
public class MyLinkedList<T> implements MyList<T> {
    private Slot<T> first = null;
    private Slot<T> last = null;

    /**
     * @see MyList#add(Object) add
     */
    @Override
    public void add(T e) {
        if (last == null) {
            last = new Slot<>();
            last.setContent(e);
            last.setNext(null);
            first = last;
        } else {
            Slot<T> tmp = new Slot<>();
            tmp.setContent(e);
            last.setNext(tmp);
            last = tmp;
        }
    }

    /**
     * @see MyList#clear() clesr
     */
    @Override
    public void clear() {
        last = null;
        first = null;
    }

    /**
     * @see MyList#get(int) get
     */
    @Override
    public T get(int index) {
        Slot<T> tmp = first;

        for (int i = 0; i < index; i++) {
            tmp = tmp.getNext();
        }

        if (tmp == null || index < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }

        return tmp.getContent();
    }

    /**
     * @see MyList#isEmpty() isEmpty
     */
    @Override
    public boolean isEmpty() {
        Slot<T> tmp = first;

        while (tmp != null) {
            if (tmp.getContent() != null) {
                return false;
            }

            tmp = tmp.getNext();
        }

        return true;
    }

    /**
     * @see MyList#remove(int) remove
     */
    @Override
    public T remove(int index) {
        T result;

        if (index < 0 || index >= size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        if (index == 0) {
            result = first.getContent();
            first = first.getNext();
        } else if (index == size()-1) {
            Slot<T> tmp = first;

            for (int i = 0; i < size()-2; i++) {
                tmp = tmp.getNext();
            }

            result = last.getContent();
            last = tmp;
            last.setNext(null);
        } else {
            Slot<T> previous = null;
            Slot<T> tmp = first;

            for (int i = 0; i < index; i++) {
                previous = tmp;
                tmp = tmp.getNext();
            }

            result = tmp.getContent();
            previous.setNext(tmp.getNext());
        }

        return result;
    }

    /**
     * @see MyList#remove(Object) remove
     */
    @Override
    public boolean remove(T o) {
        if (first.getContent().equals(o)) {
            first = first.getNext();
            return true;
        } else if (last.getContent().equals(o)) {
            Slot<T> tmp = first;

            for (int i = 0; i < size()-2; i++) {
                tmp = tmp.getNext();
            }

            last = tmp;
            last.setNext(null);
            return true;
        } else {
            Slot<T> previous = first;
            Slot<T> tmp = first.getNext();

            while (tmp != null) {
                if (tmp.getContent().equals(o)) {
                    previous.setNext(tmp.getNext());
                    return true;
                }

                previous = tmp;
                tmp = tmp.getNext();
            }
        }

        return false;
    }

    /**
     * @see MyList#size() size
     */
    @Override
    public int size() {
        int result = 0;
        Slot<T> tmp = first;

        while (tmp != null) {
            tmp = tmp.getNext();
            result++;
        }

        return result;
    }

    /**
     * Holds MyLinkedList content
     *
     * @param  <T>  generic type parameter
     */
    private class Slot<T>{
        private T content;
        private Slot<T> next;

        /**
         * Gets content of slot.
         *
         * @return    the content of slot
         */
        public T getContent() {
            return content;
        }

        /**
         * Sets content of slot.
         *
         * @param  content  the content to add into slot
         */
        public void setContent(T content) {
            this.content = content;
        }

        /**
         * Gets next slot.
         *
         * @return  Slot  the next slot as Slot
         */
        public Slot<T> getNext() {
            return next;
        }

        /**
         * Sets next slot.
         *
         * @param  next  the next slot as Slot
         */
        public void setNext(Slot<T> next) {
            this.next = next;
        }
    }
}
