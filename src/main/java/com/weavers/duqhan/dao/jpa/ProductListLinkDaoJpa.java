/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.weavers.duqhan.dao.jpa;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.weavers.duqhan.dao.TemtproductlinklistDao;
import com.weavers.duqhan.domain.ProductListLink;
import com.weavers.duqhan.domain.Temtproductlinklist;

/**
 *
 * @author weaversAndroid
 */
@Repository
public class ProductListLinkDaoJpa extends BaseDaoJpa<ProductListLink>  {

    public ProductListLinkDaoJpa() {
        super(ProductListLink.class, "ProductListLink");
    }

    
    public List<ProductListLink> getUnprocessedProductListLink() {
        Query query = getEntityManager().createQuery("SELECT tp FROM ProductListLink AS tp WHERE tp.status =" + 0);
        return query.getResultList();
    }

    
}
