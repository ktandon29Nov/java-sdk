/**
 * Copyright (c) 2019 Yodlee, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Yodlee, Inc. Use is subject to license terms.
 */
package com.yodlee.api.model.provideraccounts.response;

import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yodlee.api.model.AbstractModelComponent;
import com.yodlee.api.model.Response;
import com.yodlee.api.model.provideraccounts.AddedProviderAccount;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddedProviderAccountResponse extends AbstractModelComponent implements Response {

	@ApiModelProperty(readOnly = true)
	@JsonProperty("providerAccount")
	private List<AddedProviderAccount> providerAccounts;

	public List<AddedProviderAccount> getProviderAccount() {
		return providerAccounts == null ? null : Collections.unmodifiableList(providerAccounts);
	}

	@Override
	public String toString() {
		return "GetProviderAccountRespone [providerAccounts=" + providerAccounts + "]";
	}
}