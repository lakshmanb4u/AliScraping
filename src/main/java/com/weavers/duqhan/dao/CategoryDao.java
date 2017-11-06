/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.weavers.duqhan.dao;

import com.weavers.duqhan.domain.Category;
import java.util.List;

import org.springframework.stereotype.Repository;

/**
 *
 * @author Android-3
 */
@Repository
public interface CategoryDao extends BaseDao<Category> {

    List<Category> getChildByParentId(Long parentId);
    
    Category getCategoryByName(String name);
}
