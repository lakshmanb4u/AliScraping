/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.weavers.duqhan.dao.jpa;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.weavers.duqhan.dao.TemtproductlinklistDao;
import com.weavers.duqhan.domain.Temtproductlinklist;

/**
 *
 * @author weaversAndroid
 */
@Repository
public class TemtproductlinklistDaoJpa extends BaseDaoJpa<Temtproductlinklist> implements TemtproductlinklistDao {

    public TemtproductlinklistDaoJpa() {
        super(Temtproductlinklist.class, "Temtproductlinklist");
    }

    @Override
    public List<Temtproductlinklist> getUnprocessedTempProduct() {
        Query query = getEntityManager().createQuery("SELECT tp FROM Temtproductlinklist AS tp WHERE tp.status =" + 0);
        return query.getResultList();
    }
    
    public Temtproductlinklist getRandomeUnprocessedTempProduct() {
    	//todo remove parent_url= logic 
    	Query query = getEntityManager().createQuery("SELECT tp FROM Temtproductlinklist AS tp WHERE tp.status =" + 0 + " and parent_url is not null  ORDER BY RAND()");
    	query.setMaxResults(1);
    	List<Temtproductlinklist> list = query.getResultList();
    	if(!list.isEmpty()) {
    		return (Temtproductlinklist)list.get(0);
    	} else {
    		return null;
    	}
    }

    @Override
    public List<Temtproductlinklist> getAllTempProduct(int start, int limit) {
        Query query = getEntityManager().createQuery("SELECT tp FROM Temtproductlinklist AS tp WHERE tp.status IN(0, 1, 2)").setFirstResult(start).setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public Temtproductlinklist getTemtproductlinklistByLink(String link) {
        try {
            Query query = getEntityManager().createQuery("SELECT tp FROM Temtproductlinklist AS tp WHERE tp.link =:link");
            query.setParameter("link", link);
            return (Temtproductlinklist) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (NonUniqueResultException nure) {
            return new Temtproductlinklist();
        }
    }
}
