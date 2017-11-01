/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.weavers.duqhan.dao.jpa;

import org.springframework.stereotype.Repository;

import com.weavers.duqhan.dao.VendorDao;
import com.weavers.duqhan.domain.Vendor;

/**
 *
 * @author Android-3
 */
@Repository
public class VendorDaoJpa extends BaseDaoJpa<Vendor> implements VendorDao {

    public VendorDaoJpa() {
        super(Vendor.class, "Vendor");
    }

}
