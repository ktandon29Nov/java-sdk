/**
 * Copyright (c) 2019 Yodlee, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Yodlee, Inc. Use is subject to license terms.
 * 
 */
package com.yodlee.api.model.transaction.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TransactionClauseType {
	/**
	 * The rule created will be based on the amount field
	 */
	@JsonProperty("amount")
	amount,//
	/**
	 * The rule created will be based on the description field
	 */
	@JsonProperty("description")
	description
}