package com.demo.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName: BaseEntity
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-11-24 10:13
 * @Version 1.0
 */
public class BaseEntity implements Serializable {

	private String id;

	private String createBy;

	// @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	// @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createDate;

	// @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	// @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateDate;

	private String updateBy;

	private Integer isDel;

	public String getId() {

		return id;
	}

	public void setId(String id) {

		this.id = id;
	}

	public String getCreateBy() {

		return createBy;
	}

	public void setCreateBy(String createBy) {

		this.createBy = createBy;
	}

	public LocalDateTime getCreateDate() {

		return createDate;
	}

	public void setCreateDate(LocalDateTime createDate) {

		this.createDate = createDate;
	}

	public LocalDateTime getUpdateDate() {

		return updateDate;
	}

	public void setUpdateDate(LocalDateTime updateDate) {

		this.updateDate = updateDate;
	}

	public String getUpdateBy() {

		return updateBy;
	}

	public void setUpdateBy(String updateBy) {

		this.updateBy = updateBy;
	}

	public Integer getIsDel() {

		return isDel;
	}

	public void setIsDel(Integer isDel) {

		this.isDel = isDel;
	}
}
