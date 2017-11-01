/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.weavers.duqhan.dao.jpa;

import com.weavers.duqhan.dao.ProductPropertiesDao;
import com.weavers.duqhan.domain.ProductProperties;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

/**
 *
 * @author weaversAndroid
 */
@Repository
public class ProductPropertiesDaoJpa extends BaseDaoJpa<ProductProperties> implements ProductPropertiesDao {

    public ProductPropertiesDaoJpa() {
        super(ProductProperties.class, "ProductProperties");
    }

    @Override
    public ProductProperties loadByName(String propertyName) {
        try {
            Query query = getEntityManager().createQuery("SELECT prop FROM ProductProperties AS prop WHERE prop.propertyName=:propertyName");
            query.setParameter("propertyName", propertyName);
            return (ProductProperties) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

}
