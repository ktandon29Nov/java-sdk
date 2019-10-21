/**
 * Copyright (c) 2019 Yodlee, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Yodlee, Inc. Use is subject to license terms.
 */
package com.yodlee.api.model.consent.enums;


public enum ConsentStatus {
	/**
	* Consent generated from input dataset or implicit dataset.<br>
	*/
	CONSENT_GENERATED,//
	/**
	* Consent is accepted by user by selecting from list of consent attributes.<br>
	*/
	CONSENT_ACCEPTED,//
	/**
	* Consent is authorized when user adds/updates an account with generated consent id.<br>
	*/
	CONSENT_AUTHORIZED,//
	/**
	* When user provides not accepted consent id or already authorized consent for add/update account.<br>
	*/
	CONSENT_MISMATCH//
}