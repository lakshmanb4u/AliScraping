package com.weavers.duqhan.dao;

import com.weavers.duqhan.domain.CurrencyRates;
import com.weavers.duqhan.domain.Temtproductlinklist;
import java.util.List;

/**
 *
 * @author weaversAndroid
 */
public interface CurrencyRatesDao extends BaseDao<CurrencyRates> {

	public CurrencyRates getCurrencyRates(String currency);
	
}