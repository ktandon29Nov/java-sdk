/**
 * Copyright (c) 2019 Yodlee, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Yodlee, Inc. Use is subject to license terms.
 */
package com.yodlee.api.model.holdings.response;

import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yodlee.api.model.AbstractModelComponent;
import com.yodlee.api.model.Response;
import com.yodlee.api.model.enums.HoldingType;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HoldingTypeListResponse extends AbstractModelComponent implements Response {

	@ApiModelProperty(readOnly = true)
	@JsonProperty("holdingType")
	private List<HoldingType> holdingTypes;

	@JsonProperty("holdingType")
	public List<HoldingType> getHoldingTypes() {
		return holdingTypes == null ? null : Collections.unmodifiableList(holdingTypes);
	}

	@Override
	public String toString() {
		return "HoldingTypeListResponse [holdingTypes=" + holdingTypes + "]";
	}
}