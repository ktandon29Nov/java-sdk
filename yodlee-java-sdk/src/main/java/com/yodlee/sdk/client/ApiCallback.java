/**
 * Copyright (c) 2019 Yodlee, Inc. All Rights Reserved.
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */
/*
 * Yodlee Core APIs This file describes the Yodlee Platform APIs, using the swagger notation. You can use this swagger
 * file to generate client side SDKs to the Yodlee Platform APIs for many different programming languages. You can
 * generate a client SDK for Python, Java, javascript, PHP or other languages according to your development needs. For
 * more details about our APIs themselves, please refer to https://developer.yodlee.com/Yodlee_API/.
 *
 * OpenAPI spec version: 1.1.0 Contact: developer@yodlee.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git Do not edit the class manually.
 */
package com.yodlee.sdk.client;

import java.util.List;
import java.util.Map;
import com.yodlee.sdk.api.exception.ApiException;

/**
 * Callback for asynchronous API call.
 *
 * @param <T> The return type
 */
public interface ApiCallback<T> {

	/**
	 * This is called when the API call fails.
	 *
	 * @param e The exception causing the failure
	 * @param statusCode Status code of the response if available, otherwise it would be 0
	 * @param responseHeaders Headers of the response if available, otherwise it would be null
	 */
	void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders);

	/**
	 * This is called when the API call succeeded.
	 *
	 * @param result The result deserialized from response
	 * @param statusCode Status code of the response
	 * @param responseHeaders Headers of the response
	 */
	void onSuccess(T result, int statusCode, Map<String, List<String>> responseHeaders);

	/**
	 * This is called when the API upload processing.
	 *
	 * @param bytesWritten bytes Written
	 * @param contentLength content length of request body
	 * @param done write end
	 */
	void onUploadProgress(long bytesWritten, long contentLength, boolean done);

	/**
	 * This is called when the API downlond processing.
	 *
	 * @param bytesRead bytes Read
	 * @param contentLength content lenngth of the response
	 * @param done Read end
	 */
	void onDownloadProgress(long bytesRead, long contentLength, boolean done);
}
