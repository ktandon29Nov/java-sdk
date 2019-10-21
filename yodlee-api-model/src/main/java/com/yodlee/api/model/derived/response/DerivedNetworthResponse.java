/**
 * Copyright (c) 2019 Yodlee, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Yodlee, Inc. Use is subject to license terms.
 */
package com.yodlee.api.model.derived.response;

import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yodlee.api.model.AbstractModelComponent;
import com.yodlee.api.model.Response;
import com.yodlee.api.model.derived.DerivedNetworth;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DerivedNetworthResponse extends AbstractModelComponent implements Response {

	@ApiModelProperty(readOnly = true)
	@JsonProperty("networth")
	private List<DerivedNetworth> networthList;

	@JsonProperty("networth")
	public List<DerivedNetworth> getNetworths() {
		return networthList == null ? null : Collections.unmodifiableList(networthList);
	}

	@Override
	public String toString() {
		return "DerivedNetworthResponse [historicalBalances=" + networthList + "]";
	}
}