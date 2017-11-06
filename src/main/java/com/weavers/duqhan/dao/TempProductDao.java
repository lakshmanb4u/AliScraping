/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.weavers.duqhan.dao;

import com.weavers.duqhan.domain.TempProduct;
import java.util.List;

/**
 *
 * @author weaversAndroid
 */
public interface TempProductDao extends BaseDao<TempProduct> {

    List<TempProduct> getAllAvailableProduct(int start, int limit);

    TempProduct getProductByExternelLink(String link);
}
