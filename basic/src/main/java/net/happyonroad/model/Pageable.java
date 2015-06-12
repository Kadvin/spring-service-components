/**
 * Developer: Kadvin Date: 14-7-18 下午3:21
 */
package net.happyonroad.model;

/**
 * Abstract interface for pagination information.
 *
 * @author Oliver Gierke
 * 从JPA中Copy过来的
 */
public interface Pageable {

	/**
	 * Returns the page to be returned.
	 *
	 * @return the page to be returned.
	 */
	int getPageNumber();

	/**
	 * Returns the number of items to be returned.
	 *
	 * @return the number of items of that page
	 */
	int getPageSize();

	/**
	 * Returns the offset to be taken according to the underlying page and page size.
	 *
	 * @return the offset to be taken
	 */
	int getOffset();

	/**
	 * Returns the sorting parameters.
	 *
	 * @return 排序方式
	 */
	Sort getSort();

	/**
	 * Returns the {@link Pageable} requesting the next {@link Page}.
	 *
	 * @return next pageable
	 */
	Pageable next();

	/**
	 * Returns the previous {@link Pageable} or the first {@link Pageable} if the current one already is the first one.
	 *
	 * @return  previous pageable
	 */
	Pageable previousOrFirst();

	/**
	 * Returns the {@link Pageable} requesting the first page.
	 *
	 * @return  first pageable
	 */
	Pageable first();

	/**
	 * Returns whether there's a previous {@link Pageable} we can access from the current one. Will return
	 * {@literal false} in case the current {@link Pageable} already refers to the first page.
	 *
	 * @return  previous pageable
	 */
	boolean hasPrevious();
}
