/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.weavers.duqhan.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author weaversAndroid
 */
@Entity
@Table(name = "temtproductlinklist")
public class Temtproductlinklist extends BaseDomain {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "link")
    private String link;

    @Basic(optional = false)
    @NotNull
    @Column(name = "status")
    private int status;
    
    @Column(name = "error")
    @Lob
    private String error;
    @Column(name = "parent_url")
	private Long parentUrl;

    public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    public Long getParentUrl() {
		return this.parentUrl;
	}

	public void setParentUrl(Long id) {
		this.parentUrl = id;
	}

}
