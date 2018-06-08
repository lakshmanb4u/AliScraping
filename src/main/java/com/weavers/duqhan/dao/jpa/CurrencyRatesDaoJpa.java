package com.weavers.duqhan.dao.jpa;

import java.util.List;
import java.util.Objects;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import com.weavers.duqhan.dao.CurrencyRatesDao;
import com.weavers.duqhan.domain.CurrencyRates;

/**
 *
 * @author Android-3
 */

@Repository
public class CurrencyRatesDaoJpa extends BaseDaoJpa<CurrencyRates> implements CurrencyRatesDao {
	public CurrencyRatesDaoJpa() {
        super(CurrencyRates.class, "CurrencyRates");
    }
	
	@Override
	public CurrencyRates getCurrencyRates(String currency) {
		Query query = getEntityManager().createQuery("SELECT p FROM CurrencyRates AS p WHERE p.currency=:currency");
    	query.setParameter("currency", currency);
    	return (CurrencyRates) query.getResultList().get(0);
	}
}