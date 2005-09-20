/**
 * @(#)DelegateCollection.java 0.9 1999/03/29 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import java.util.Collection;
import java.util.Iterator;
import java.io.Serializable;

/**
 * A DelegateCollection that works as a delegator to collections.
 * It implements the <code>java.util.Collection</code> interface itself, and so
 * a DelegateCollection is a Collection delegating to a specified implementation
 * Collection.
 * <p>
 * As such, this behaviour is not very useful, but derived subclasses that
 * operate over a collection and want to provide Collection-like behaviour
 * can easily extend this class.
 * All operations of the Collection interface will be delegated.
 * <p>
 * If collection-like classes that depend upon a collection-implementation
 * are derived from the distinct implementation Collection, this inheritance relation
 * is static and can neither be changed on runtime nor without structural change on compile-time.
 * If such classes derive from DelegateCollection instead,
 * the aggregate (more specific: delegate) relation is more dynamic and
 * the implementation can change transparently on runtime.
 * 
 * @structure delegate delegatee:java.util.Collection
 * @structure implements java.util.Collection
 * @structure implements java.io.Serializable
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @author  Josh Bloch
 * @see java.util.Collection
 * @see java.util.AbstractCollection
 */
public class DelegateCollection/*<A>*/ implements Collection/*<A>*/, Serializable {
    private static final long serialVersionUID = 5707149303394458449L;

    /**
     * Extend to create a Collection delegating nowhere.
     * For delegatee will be set to <code>null</code>, this object will throw
     * NullPointerExceptions in almost every method.
     */
    private DelegateCollection() {
        delegatee = null;
    }

    /**
     * Extend to create a Collection delegating to an implementation Collection.
     * @param delegatee the implementation-Collection to that Collection operations are delegated.
     */
    protected DelegateCollection(Collection/*<A>*/ delegatee) {
        this.delegatee = delegatee;
    }

    // delegation operations

    /**
     * Contains the delegatee Collection to which operations are be delegated.
     * @serial serialization of the collection delegated to.
     */
    private Collection/*<A>*/ delegatee = null;

    /**
     * Get the delegatee Collection to which operations are delegated.
     * @return the implementation-Collection that Collection operations are delegated to.
     */
    protected Collection/*<A>*/ getDelegatee() {
        return this.delegatee;
    } 

    /**
     * Set the delegatee Collection to which operations are delegated.
     * @param delegatee the implementation-Collection that Collection operations are delegated to.
     */
    protected void setDelegatee(Collection/*<A>*/ delegatee) {
        this.delegatee = delegatee;
    } 

    // Delegated operations.

    // Query Operations

    /**
     * Returns the number of elements in this collection.  If this collection
     * contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     * 
     * @return the number of elements in this collection
     */
    public int size() {
        return getDelegatee().size();
    } 

    /**
     * Returns <tt>true</tt> if this collection contains no elements.
     * 
     * @return <tt>true</tt> if this collection contains no elements
     */
    public boolean isEmpty() {
        return getDelegatee().isEmpty();
    } 

    /**
     * Returns <tt>true</tt> if this collection contains the specified
     * element.  More formally, returns <tt>true</tt> if and only if this
     * collection contains at least one element <tt>e</tt> such that
     * <tt>(o==null ? e==null : o.equals(e))</tt>.
     * 
     * @param o element whose presence in this collection is to be tested.
     * @return <tt>true</tt> if this collection contains the specified
     * element
     */
    public boolean contains(Object o) {
        return getDelegatee().contains(o);
    } 

    /**
     * Returns an iterator over the elements in this collection.  There are no
     * guarantees concerning the order in which the elements are returned
     * (unless this collection is an instance of some class that provides a
     * guarantee).
     * 
     * @return an <tt>Iterator</tt> over the elements in this collection
     */
    public Iterator/*<A>*/ iterator() {
        return getDelegatee().iterator();
    } 

    /**
     * Returns an array containing all of the elements in this collection.  If
     * the collection makes any guarantees as to what order its elements are
     * returned by its iterator, this method must return the elements in the
     * same order.<p>
     * 
     * The returned array will be "safe" in that no references to it are
     * maintained by this collection.  (In other words, this method must
     * allocate a new array even if this collection is backed by an array).
     * The caller is thus free to modify the returned array.<p>
     * 
     * This method acts as bridge between array-based and collection-based
     * APIs.
     * 
     * @return an array containing all of the elements in this collection
     */
    public Object[] toArray() {
        return getDelegatee().toArray();
    } 

    /**
     * Returns an array containing all of the elements in this collection
     * whose runtime type is that of the specified array.  If the collection
     * fits in the specified array, it is returned therein.  Otherwise, a new
     * array is allocated with the runtime type of the specified array and the
     * size of this collection.<p>
     * 
     * If this collection fits in the specified array with room to spare
     * (i.e., the array has more elements than this collection), the element
     * in the array immediately following the end of the collection is set to
     * <tt>null</tt>.  This is useful in determining the length of this
     * collection <em>only</em> if the caller knows that this collection does
     * not contain any <tt>null</tt> elements.)<p>
     * 
     * If this collection makes any guarantees as to what order its elements
     * are returned by its iterator, this method must return the elements in
     * the same order.<p>
     * 
     * Like the <tt>toArray</tt> method, this method acts as bridge between
     * array-based and collection-based APIs.  Further, this method allows
     * precise control over the runtime type of the output array, and may,
     * under certain circumstances, be used to save allocation costs<p>
     * 
     * Suppose <tt>l</tt> is a <tt>List</tt> known to contain only strings.
     * The following code can be used to dump the list into a newly allocated
     * array of <tt>String</tt>:
     * 
     * <pre>
     * String[] x = (String[]) v.toArray(new String[<span class="Number">0</span>]);
     * </pre><p>
     * 
     * Note that <tt>toArray(new Object[0])</tt> is identical in function to
     * <tt>toArray()</tt>.
     * 
     * @param a the array into which the elements of this collection are to be
     * stored, if it is big enough; otherwise, a new array of the same
     * runtime type is allocated for this purpose.
     * @return an array containing the elements of this collection
     * 
     * @throws ArrayStoreException the runtime type of the specified array is
     * not a supertype of the runtime type of every element in this
     * collection.
     */

    public /*<T>*/ Object/*>T<*/[] toArray(Object/*>T<*/ a[]) {
        return getDelegatee().toArray(a);
    } 

    // Modification Operations

    /**
     * Ensures that this collection contains the specified element (optional
     * operation).  Returns <tt>true</tt> if this collection changed as a
     * result of the call.  (Returns <tt>false</tt> if this collection does
     * not permit duplicates and already contains the specified element.)<p>
     * 
     * Collections that support this operation may place limitations on what
     * elements may be added to this collection.  In particular, some
     * collections will refuse to add <tt>null</tt> elements, and others will
     * impose restrictions on the type of elements that may be added.
     * Collection classes should clearly specify in their documentation any
     * restrictions on what elements may be added.<p>
     * 
     * If a collection refuses to add a particular element for any reason
     * other than that it already contains the element, it <em>must</em> throw
     * an exception (rather than returning <tt>false</tt>).  This preserves
     * the invariant that a collection always contains the specified element
     * after this call returns.
     * 
     * @param o element whose presence in this collection is to be ensured.
     * @return <tt>true</tt> if this collection changed as a result of the
     * call
     * 
     * @throws UnsupportedOperationException add is not supported by this
     * collection.
     * @throws ClassCastException class of the specified element prevents it
     * from being added to this collection.
     * @throws IllegalArgumentException some aspect of this element prevents
     * it from being added to this collection.
     */
    public boolean add(Object/*>A<*/ o) {
        return getDelegatee().add(o);
    } 

    /**
     * Removes a single instance of the specified element from this
     * collection, if it is present (optional operation).  More formally,
     * removes an element <tt>e</tt> such that <tt>(o==null ?  e==null :
     * o.equals(e))</tt>, if this collection contains one or more such
     * elements.  Returns true if this collection contained the specified
     * element (or equivalently, if this collection changed as a result of the
     * call).
     * 
     * @param o element to be removed from this collection, if present.
     * @return <tt>true</tt> if this collection changed as a result of the
     * call
     * 
     * @throws UnsupportedOperationException remove is not supported by this
     * collection.
     */
    public boolean remove(Object o) {
        return getDelegatee().remove(o);
    } 


    // Bulk Operations

    /**
     * Returns <tt>true</tt> if this collection contains all of the elements
     * in the specified collection.
     * 
     * @param c collection to be checked for containment in this collection.
     * @return <tt>true</tt> if this collection contains all of the elements
     * in the specified collection
     * @see #contains(Object)
     */
    public boolean containsAll(Collection/*<?>*/ c) {
        return getDelegatee().containsAll(c);
    } 

    /**
     * Adds all of the elements in the specified collection to this collection
     * (optional operation).  The behavior of this operation is undefined if
     * the specified collection is modified while the operation is in progress.
     * (This implies that the behavior of this call is undefined if the
     * specified collection is this collection, and this collection is
     * nonempty.)
     * 
     * @param c elements to be inserted into this collection.
     * @return <tt>true</tt> if this collection changed as a result of the
     * call
     * 
     * @throws UnsupportedOperationException if this collection does not
     * support the <tt>addAll</tt> method.
     * @throws ClassCastException if the class of an element of the specified
     * collection prevents it from being added to this collection.
     * @throws IllegalArgumentException some aspect of an element of the
     * specified collection prevents it from being added to this
     * collection.
     * 
     * @see #add(Object)
     */
    public boolean addAll(Collection/*<? extends A>*/ c) {
        return getDelegatee().addAll(c);
    } 

    /**
     * 
     * Removes all this collection's elements that are also contained in the
     * specified collection (optional operation).  After this call returns,
     * this collection will contain no elements in common with the specified
     * collection.
     * 
     * @param c elements to be removed from this collection.
     * @return <tt>true</tt> if this collection changed as a result of the
     * call
     * 
     * @throws UnsupportedOperationException if the <tt>removeAll</tt> method
     * is not supported by this collection.
     * 
     * @see #remove(Object)
     * @see #contains(Object)
     */
    public boolean removeAll(Collection/*<?>*/ c) {
        return getDelegatee().removeAll(c);
    } 

    /**
     * Retains only the elements in this collection that are contained in the
     * specified collection (optional operation).  In other words, removes from
     * this collection all of its elements that are not contained in the
     * specified collection.
     * 
     * @param c elements to be retained in this collection.
     * @return <tt>true</tt> if this collection changed as a result of the
     * call
     * 
     * @throws UnsupportedOperationException if the <tt>retainAll</tt> method
     * is not supported by this Collection.
     * 
     * @see #remove(Object)
     * @see #contains(Object)
     */
    public boolean retainAll(Collection/*<?>*/ c) {
        return getDelegatee().retainAll(c);
    } 

    /**
     * Removes all of the elements from this collection (optional operation).
     * This collection will be empty after this method returns unless it
     * throws an exception.
     * 
     * @throws UnsupportedOperationException if the <tt>clear</tt> method is
     * not supported by this collection.
     */
    public void clear() {
        getDelegatee().clear();
    } 


    // Comparison and hashing

    /**
     * Compares the specified object with this collection for equality. <p>
     * 
     * While the <tt>Collection</tt> interface adds no stipulations to the
     * general contract for the <tt>Object.equals</tt>, programmers who
     * implement the <tt>Collection</tt> interface "directly" (in other words,
     * create a class that is a <tt>Collection</tt> but is not a <tt>Set</tt>
     * or a <tt>List</tt>) must exercise care if they choose to override the
     * <tt>Object.equals</tt>.  It is not necessary to do so, and the simplest
     * course of action is to rely on <tt>Object</tt>'s implementation, but
     * the implementer may wish to implement a "value comparison" in place of
     * the default "reference comparison."  (The <tt>List</tt> and
     * <tt>Set</tt> interfaces mandate such value comparisons.)<p>
     * 
     * The general contract for the <tt>Object.equals</tt> method states that
     * equals must be symmetric (in other words, <tt>a.equals(b)</tt> if and
     * only if <tt>b.equals(a)</tt>).  The contracts for <tt>List.equals</tt>
     * and <tt>Set.equals</tt> state that lists are only equal to other lists,
     * and sets to other sets.  Thus, a custom <tt>equals</tt> method for a
     * collection class that implements neither the <tt>List</tt> nor
     * <tt>Set</tt> interface must return <tt>false</tt> when this collection
     * is compared to any list or set.  (By the same logic, it is not possible
     * to write a class that correctly implements both the <tt>Set</tt> and
     * <tt>List</tt> interfaces.)
     * 
     * @param o Object to be compared for equality with this collection.
     * @return <tt>true</tt> if the specified object is equal to this
     * collection
     * 
     * @see java.lang.Object#equals(Object)
     * @see java.util.Set#equals(Object)
     * @see java.util.List#equals(Object)
     */
    public boolean equals(Object o) {
        return getDelegatee().equals(o);
    } 

    /**
     * 
     * Returns the hash code value for this collection.  While the
     * <tt>Collection</tt> interface adds no stipulations to the general
     * contract for the <tt>Object.hashCode</tt> method, programmers should
     * take note that any class that overrides the <tt>Object.equals</tt>
     * method must also override the <tt>Object.hashCode</tt> method in order
     * to satisfy the general contract for the <tt>Object.hashCode</tt>method.
     * In particular, <tt>c1.equals(c2)</tt> implies that
     * <tt>c1.hashCode()==c2.hashCode()</tt>.
     * 
     * @return the hash code value for this collection
     * 
     * @see Object#hashCode()
     * @see Object#equals(Object)
     */
    public int hashCode() {
        return getDelegatee().hashCode();
    } 
}
