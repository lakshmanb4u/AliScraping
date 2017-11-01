/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.weavers.duqhan.business;

import com.weavers.duqhan.dto.StatusBean;
import java.util.List;

/**
 *
 * @author Android-3
 */
public interface ProductService {

      void loadTempProducts(List<StatusBean> statusBeans);
}
