/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.weavers.duqhan.dao.jpa;

import org.springframework.stereotype.Repository;

import com.weavers.duqhan.dao.ProductPropertiesMapDao;
import com.weavers.duqhan.domain.ProductPropertiesMap;

/**
 *
 * @author weaversAndroid
 */
@Repository
public class ProductPropertiesMapDaoJpa extends BaseDaoJpa<ProductPropertiesMap> implements ProductPropertiesMapDao{
    
    public ProductPropertiesMapDaoJpa() {
        super(ProductPropertiesMap.class, "ProductPropertiesMap");
    }
    
}
