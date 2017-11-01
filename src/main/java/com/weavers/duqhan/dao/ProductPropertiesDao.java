/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.weavers.duqhan.dao;

import com.weavers.duqhan.domain.ProductProperties;

/**
 *
 * @author weaversAndroid
 */
public interface ProductPropertiesDao extends BaseDao<ProductProperties> {

    ProductProperties loadByName(String propertyName);
}
