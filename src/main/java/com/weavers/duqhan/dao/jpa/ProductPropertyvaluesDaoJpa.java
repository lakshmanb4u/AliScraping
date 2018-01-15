/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.weavers.duqhan.dao.jpa;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.weavers.duqhan.dao.ProductPropertyvaluesDao;
import com.weavers.duqhan.domain.ProductPropertyvalues;
import com.weavers.duqhan.domain.Temtproductlinklist;

/**
 *
 * @author weaversAndroid
 */
@Repository
public class ProductPropertyvaluesDaoJpa extends BaseDaoJpa<ProductPropertyvalues> implements ProductPropertyvaluesDao {

    public ProductPropertyvaluesDaoJpa() {
        super(ProductPropertyvalues.class, "ProductPropertyvalues");
    }

	@Override
	public ProductPropertyvalues loadByProductIdAndPropertyIdAndRefId(Long productId, Long proprtyId, String refId) {
		Query query = getEntityManager().createQuery("SELECT p FROM ProductPropertyvalues AS p WHERE p.productId=:productId AND p.propertyId=:proprtyId And p.refId=:refId ORDER BY RAND()").setMaxResults(1);
		query.setParameter("productId", productId);
		query.setParameter("proprtyId", proprtyId);
		query.setParameter("refId", refId);
    	
    	List<ProductPropertyvalues> list = query.getResultList();
    	if(!list.isEmpty()) {
    		return (ProductPropertyvalues)list.get(0);
    	} else {
    		return null;
    	}
	}

}
