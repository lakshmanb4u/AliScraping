// -----------------------------------com.weavers.duqhan.dto.CurrencyRates.java-----------------------------------
package com.weavers.duqhan.dto;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "USD",
    "INR",
    "KWD"
})
public class CurrencyRates {

    @JsonProperty("USD")
    private Double uSD;
    @JsonProperty("INR")
    private Double iNR;
    @JsonProperty("KWD")
    private Double kWD;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("USD")
    public Double getUSD() {
        return uSD;
    }

    @JsonProperty("USD")
    public void setUSD(Double uSD) {
        this.uSD = uSD;
    }

    @JsonProperty("INR")
    public Double getINR() {
        return iNR;
    }

    @JsonProperty("INR")
    public void setINR(Double iNR) {
        this.iNR = iNR;
    }
    
    @JsonProperty("KWD")
    public Double getKWD() {
		return kWD;
	}
    @JsonProperty("KWD")
	public void setKWD(Double kWD) {
		this.kWD = kWD;
	}

	@JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
