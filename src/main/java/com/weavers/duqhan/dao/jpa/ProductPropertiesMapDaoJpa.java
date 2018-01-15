/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.weavers.duqhan.dao.jpa;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.weavers.duqhan.dao.ProductPropertiesMapDao;
import com.weavers.duqhan.domain.ProductPropertiesMap;
import com.weavers.duqhan.domain.ProductPropertyvalues;

/**
 *
 * @author weaversAndroid
 */
@Repository
public class ProductPropertiesMapDaoJpa extends BaseDaoJpa<ProductPropertiesMap> implements ProductPropertiesMapDao{
    
    public ProductPropertiesMapDaoJpa() {
        super(ProductPropertiesMap.class, "ProductPropertiesMap");
    }

	@Override
	public ProductPropertiesMap loadByProductIdAndPropertyvalueComposition(Long productId, String composition) {
		Query query = getEntityManager().createQuery("SELECT p FROM ProductPropertiesMap AS p WHERE p.productId.id=:productId And p.propertyvalueComposition=:composition ORDER BY RAND()").setMaxResults(1);
		query.setParameter("productId", productId);
		query.setParameter("composition", composition);
    	
    	List<ProductPropertiesMap> list = query.getResultList();
    	if(!list.isEmpty()) {
    		return (ProductPropertiesMap)list.get(0);
    	} else {
    		return null;
    	}
	}
    
}
