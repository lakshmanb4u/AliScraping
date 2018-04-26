/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.weavers.duqhan.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Android-3
 */
@Entity
@Table(name = "category")
public class Category extends BaseDomain {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Column(name = "parent_id")
    private long parentId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "parent_path")
    private String parentPath;
    @Basic(optional = false)
    @NotNull
    @Column(name = "quantity")
    private long quantity;
    @Size(max = 255)
    @Column(name = "img_url")
    private String imgUrl;
    @Size(max = 255)
    @Column(name = "display_text")
    private String displayText;

    @Column(name = "is_display")
    private boolean isDisplay;
    @Size(max = 255)
    @Column(name = "menu_icon")
    private String menuIcon;
    @Column(name = "price_limit")
    private String priceLimit;
    @Column(name = "load_category_id")
    private String loadCategory;
    
    public boolean isDisplay() {
		return isDisplay;
	}

	public void setDisplay(boolean isDisplay) {
		this.isDisplay = isDisplay;
	}

	public String getMenuIcon() {
		return menuIcon;
	}

	public void setMenuIcon(String menuIcon) {
		this.menuIcon = menuIcon;
	}

	public String getPriceLimit() {
		return priceLimit;
	}

	public void setPriceLimit(String priceLimit) {
		this.priceLimit = priceLimit;
	}

	public String getLoadCategory() {
		return loadCategory;
	}

	public void setLoadCategory(String loadCategory) {
		this.loadCategory = loadCategory;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }
}
