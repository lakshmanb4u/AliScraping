/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.weavers.duqhan.dao;

import com.weavers.duqhan.domain.Category;
import java.util.List;

/**
 *
 * @author Android-3
 */
public interface CategoryDao extends BaseDao<Category> {

    List<Category> getChildByParentId(Long parentId);
    
    Category getCategoryByName(String name);
}
