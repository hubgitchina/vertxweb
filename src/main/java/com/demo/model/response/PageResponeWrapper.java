package com.demo.model.response;

/**
 * @ClassName: PageResponeWrapper
 * @Description: 分页数据响应对象
 * @Author wangpeng
 * @Date 2020-08-18 11:20
 * @Version 1.0
 */
public class PageResponeWrapper<T> extends ResponeWrapper<T> {

	private int pageNum;
	private int pageSize;
	private long total;

	public PageResponeWrapper() {

	}

	/**
	 * total的值需要自己给根据实际数据设置
	 *
	 * @param data
	 * @param pageNum
	 * @param pageSize
	 */
	public PageResponeWrapper(T data, int pageNum, int pageSize, long total) {

		super(data);
		this.pageNum = pageNum;
		this.pageSize = pageSize;
		this.total = total;
	}

	public int getPageNum() {

		return pageNum;
	}

	public void setPageNum(int pageNum) {

		this.pageNum = pageNum;
	}

	public int getPageSize() {

		return pageSize;
	}

	public void setPageSize(int pageSize) {

		this.pageSize = pageSize;
	}

	public long getTotal() {

		return total;
	}

	public void setTotal(long total) {

		this.total = total;
	}

}
