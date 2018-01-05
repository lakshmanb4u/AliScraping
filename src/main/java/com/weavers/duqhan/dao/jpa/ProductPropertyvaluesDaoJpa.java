/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.weavers.duqhan.dao.jpa;

import org.springframework.stereotype.Repository;

import com.weavers.duqhan.dao.ProductPropertyvaluesDao;
import com.weavers.duqhan.domain.ProductPropertyvalues;

/**
 *
 * @author weaversAndroid
 */
@Repository
public class ProductPropertyvaluesDaoJpa extends BaseDaoJpa<ProductPropertyvalues> implements ProductPropertyvaluesDao {

    public ProductPropertyvaluesDaoJpa() {
        super(ProductPropertyvalues.class, "ProductPropertyvalues");
    }

}
