/**
 * Developer: Kadvin Date: 14-7-18 下午3:32
 */
package net.happyonroad.platform.util;

import net.happyonroad.platform.service.Page;
import net.happyonroad.platform.service.Pageable;

import java.util.List;

/**
 * Basic {@code Page} implementation.
 *
 * @param <T> the type of which the page consists.
 * @author Oliver Gierke
 */
public class DefaultPage<T> extends Chunk<T> implements Page<T> {

	private static final long serialVersionUID = 867755909294344406L;

	private final long total;

	/**
	 * Constructor of {@code DefaultPage}.
	 *
	 * @param content the content of this page, must not be {@literal null}.
	 * @param pageable the paging information, can be {@literal null}.
	 * @param total the total amount of items available
	 */
	public DefaultPage(List<T> content, Pageable pageable, long total) {

		super(content, pageable);
		this.total = total;
	}

	/**
	 * Creates a new {@link DefaultPage} with the given content. This will result in the created {@link Page} being identical
	 * to the entire {@link List}.
	 *
	 * @param content must not be {@literal null}.
	 */
	public DefaultPage(List<T> content) {
		this(content, null, null == content ? 0 : content.size());
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Page#getTotalPages()
	 */
	public int getTotalPages() {
		return getSize() == 0 ? 1 : (int) Math.ceil((double) total / (double) getSize());
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Page#getTotalElements()
	 */
	public long getTotalElements() {
		return total;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Page#hasPreviousPage()
	 */
	public boolean hasPreviousPage() {
		return hasPrevious();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Page#isFirstPage()
	 */
	public boolean isFirstPage() {
		return isFirst();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Slice#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return hasNextPage();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Page#hasNextPage()
	 */
	public boolean hasNextPage() {
		return getNumber() + 1 < getTotalPages();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Slice#isLast()
	 */
	@Override
	public boolean isLast() {
		return isLastPage();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Page#isLastPage()
	 */
	public boolean isLastPage() {
		return !hasNextPage();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		String contentType = "UNKNOWN";
		List<T> content = getContent();

		if (content.size() > 0) {
			contentType = content.get(0).getClass().getName();
		}

		return String.format("Page %s of %d containing %s instances", getNumber(), getTotalPages(), contentType);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof DefaultPage<?>)) {
			return false;
		}

		DefaultPage<?> that = (DefaultPage<?>) obj;

		return this.total == that.total && super.equals(obj);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		int result = 17;

		result += 31 * (int) (total ^ total >>> 32);
		result += 31 * super.hashCode();

		return result;
	}
}
