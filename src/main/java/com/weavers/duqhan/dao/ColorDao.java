/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.weavers.duqhan.dao;

import com.weavers.duqhan.domain.Color;
import java.util.List;

/**
 *
 * @author Android-3
 */
public interface ColorDao extends BaseDao<Color> {

    List<Color> loadByIds(List<Long> colorIds);
}
