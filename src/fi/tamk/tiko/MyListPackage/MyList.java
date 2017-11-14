package fi.tamk.tiko.MyListPackage;

/**
 * MyList
 *
 * <p>Interface for generic type Lists.</p>
 *
 * @param       <T> generic type parameter
 * @author      Jyri Virtaranta jyri.virtaranta@cs.tamk.fi
 * @version     2017.10.18
 * @since       1.8
 */
public interface MyList<T> {
    /**
     * Appends the specified element to the end of this list
     *
     * @param  e  generic type parameter
     */
    void add(T e);

    /**
     * Removes all of the elements from this list
     */
    void clear();

    /**
     * Returns the element at the specified position in this list.
     *
     * @param   index  the index of the array as int
     * @return         the object in the array index
     */
    T get(int index);

    /**
     * Returns true if this list contains no elements.
     *
     * @return  boolean  true if empty
     */
    boolean isEmpty();

    /**
     * Removes the element at the specified position in this list.
     * Returns the removed element.
     *
     * @param   index  the index of the array as int
     * @return         the object in the array index
     */
    T remove(int index);

    /**
     * Removes the first occurrence of the specified element from this list,
     * if it is present (return true)
     *
     * @param   e        type generic to be removed
     * @return  boolean  true if e present
     */
    boolean remove(T e);

    /**
     * Returns the number of elements in this list.
     *
     * @return  int  number of elements
     */
    int size();
}
