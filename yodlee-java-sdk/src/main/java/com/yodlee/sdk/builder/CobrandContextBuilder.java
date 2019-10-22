/**
 * Copyright (c) 2019 Yodlee, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Yodlee, Inc. Use is subject to license terms.
 */
package com.yodlee.sdk.builder;

import com.yodlee.api.model.cobrand.request.CobrandLoginRequest;
import com.yodlee.api.model.cobrand.request.CobrandLoginRequest.Cobrand;
import com.yodlee.sdk.api.ApiConstants;
import com.yodlee.sdk.api.CobrandApi;
import com.yodlee.sdk.api.exception.ApiException;
import com.yodlee.sdk.api.validators.ApiValidator;
import com.yodlee.sdk.client.ApiClient;
import com.yodlee.sdk.configuration.cobrand.CobrandConfiguration;
import com.yodlee.sdk.context.CobrandContext;
import com.yodlee.sdk.context.Context;

public class CobrandContextBuilder implements Builder<CobrandConfiguration, CobrandContext> {

	/**
	 * This method is used to obtain Cobrand context which can be used to invoke any Cobrand bound API. This Cobrand
	 * context holds a cobSession obtained during the invocation of this method.
	 * 
	 * @param cobrandConfiguration {@link CobrandConfiguration}
	 * @return {@link CobrandContext}
	 * @throws ApiException - {@link ApiException}
	 */
	public CobrandContext build(CobrandConfiguration cobrandConfiguration) throws ApiException {
		ConfigValidationUtil.validateConfig(cobrandConfiguration);
		CobrandApi cobrandApi = new CobrandApi(cobrandConfiguration);
		// Prepare login request
		CobrandLoginRequest cobrandLoginRequest = new CobrandLoginRequest();
		Cobrand cobrand = new Cobrand();
		cobrand.setCobrandLogin(cobrandConfiguration.getLoginName());
		cobrand.setCobrandPassword(cobrandConfiguration.getPassword());
		cobrand.setLocale(cobrandConfiguration.getLocale());
		cobrandLoginRequest.setCobrand(cobrand);
		ApiValidator.validate(cobrandLoginRequest);
		// Prepare ApiClient
		ApiClient apiClient = new ApiClient();
		apiClient.setBasePath(cobrandConfiguration.getBasePath());
		apiClient.addHeader(ApiConstants.API_VERSION, cobrandConfiguration.getApiVersion());
		apiClient.addHeader(ApiConstants.COBRAND_NAME, cobrandConfiguration.getName());
		// invoke cobrandLogin API
		cobrandApi.cobrandLogin(cobrandLoginRequest);
		Context<?> context = cobrandApi.getContext();
		return (CobrandContext) context;
	}
}
