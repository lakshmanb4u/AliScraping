package com.weavers.duqhan.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author weaversAndroid
 */
@Entity
@Table(name = "conversion_rate")
public class CurrencyRates extends BaseDomain {

    private static final long serialVersionUID = 1L;
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "currency")
    private String currency;
    
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "rates")
    private Double rates;

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Double getRates() {
		return rates;
	}

	public void setRates(Double rates) {
		this.rates = rates;
	}
    
    
    
}