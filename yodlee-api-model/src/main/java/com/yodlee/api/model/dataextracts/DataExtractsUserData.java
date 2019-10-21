/**
 * Copyright (c) 2019 Yodlee, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Yodlee, Inc. Use is subject to license terms.
 */
package com.yodlee.api.model.dataextracts;

import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.yodlee.api.model.AbstractModelComponent;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"user", "holding", "transaction", "account", "providerAccount"})
public class DataExtractsUserData extends AbstractModelComponent {

	@ApiModelProperty(readOnly = true)
	@JsonProperty("user")
	private DataExtractsUser user;

	@ApiModelProperty(readOnly = true)
	@JsonProperty("holding")
	private List<DataExtractsHolding> holdings;

	@ApiModelProperty(readOnly = true)
	@JsonProperty("transaction")
	private List<DataExtractsTransaction> transactions;

	@ApiModelProperty(readOnly = true)
	@JsonProperty("account")
	private List<DataExtractsAccount> accounts;

	@ApiModelProperty(readOnly = true)
	@JsonProperty("providerAccount")
	private List<DataExtractsProviderAccount> providerAccounts;

	@JsonProperty("holding")
	public List<DataExtractsHolding> getHoldings() {
		return holdings == null ? null : Collections.unmodifiableList(holdings);
	}

	@JsonProperty("transaction")
	public List<DataExtractsTransaction> getTransactions() {
		return transactions == null ? null : Collections.unmodifiableList(transactions);
	}

	@JsonProperty("account")
	public List<DataExtractsAccount> getAccounts() {
		return accounts == null ? null : Collections.unmodifiableList(accounts);
	}

	@JsonProperty("providerAccount")
	public List<DataExtractsProviderAccount> getProviderAccounts() {
		return providerAccounts == null ? null : Collections.unmodifiableList(providerAccounts);
	}

	public DataExtractsUser getUser() {
		return user;
	}

	@Override
	public String toString() {
		return "DataExtractsUserData [holdings=" + holdings + ", transactions=" + transactions + ", accounts="
				+ accounts + ", providerAccounts=" + providerAccounts + ", user=" + user + "]";
	}
}