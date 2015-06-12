/**
 * Developer: Kadvin Date: 14-7-18 下午3:21
 */
package net.happyonroad.model;

/**
 * A page is a sublist of a list of objects. It allows gain information about the position of it in the containing
 * entire list.
 *
 * @param <T>
 * @author Oliver Gierke
 * 从JPA中copy过来的
 */
public interface Page<T> extends Slice<T> {
    final String TOTAL  = "total";
    final String PAGES  = "pages";
    final String NUMBER = "number";
    final String REAL   = "real";
    final String SORT   = "sort";
    final String COUNT  = "count";

    /**
     * Returns the number of total pages.
     *
     * @return the number of toral pages
     */
    int getTotalPages();

    /**
     * Returns the total amount of elements.
     *
     * @return the total amount of elements
     */
    long getTotalElements();

}
