/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.weavers.duqhan.dao;

import com.weavers.duqhan.domain.TempProductImg;
import java.util.List;

/**
 *
 * @author weaversAndroid
 */
public interface TempProductImgDao extends BaseDao<TempProductImg> {

    List<TempProductImg> getProductImgsByProductId(Long productId);
}
