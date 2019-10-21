/**
 * Copyright (c) 2019 Yodlee, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Yodlee, Inc. Use is subject to license terms.
 */
package com.yodlee.api.model.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.yodlee.api.model.AbstractAddress;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"address1", "address2", "fullAddress", "state", "city", "zip", "country"})
public class UserAddress extends AbstractAddress {

	@Override
	public String toString() {
		return "UserAddress [address1=" + address1 + ", address2=" + address2 + ", state=" + state + ", city=" + city
				+ ", zip=" + zip + ", country=" + country + "]";
	}
}