/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.weavers.duqhan.dao;

import com.weavers.duqhan.domain.ProductPropertiesMap;

/**
 *
 * @author weaversAndroid
 */
public interface ProductPropertiesMapDao extends BaseDao<ProductPropertiesMap>{
    
	ProductPropertiesMap loadByProductIdAndPropertyvalueComposition(Long productId,String composition);
}
