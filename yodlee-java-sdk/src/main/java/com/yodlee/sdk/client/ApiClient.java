/**
 * Copyright (c) 2019 Yodlee, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Yodlee, Inc. Use is subject to license terms.
 */
package com.yodlee.sdk.client;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.LocalDate;
import org.threeten.bp.OffsetDateTime;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.internal.http.HttpMethod;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import com.squareup.okhttp.logging.HttpLoggingInterceptor.Level;
import com.yodlee.api.model.AbstractModelComponent;
import com.yodlee.api.model.validator.Problem;
import com.yodlee.sdk.api.exception.ApiException;
import com.yodlee.sdk.api.util.ApiUtils;
import com.yodlee.sdk.builder.JWTUtil;
import com.yodlee.sdk.configuration.AbstractConfiguration;
import com.yodlee.sdk.configuration.cobrand.AbstractJWTConfiguration;
import com.yodlee.sdk.configuration.user.JWTUserConfiguration;
import com.yodlee.sdk.context.AbstractJWTContext;
import com.yodlee.sdk.context.Context;
import com.yodlee.sdk.context.ContextType;

public class ApiClient {

	private static final String APPLICATION_JSON = "application/json";

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiClient.class);

	private String basePath = null;

	private boolean debugging = false;

	private final Map<String, String> defaultHeaderMap = new HashMap<>();

	private OkHttpClient httpClient;

	private HttpLoggingInterceptor loggingInterceptor;

	private final ObjectMapper mapper = new ObjectMapper();

	private Context<?> context;

	public ApiClient() {
		this(null);
	}

	public ApiClient(Context<?> context) {
		super();
		this.context = context;
		httpClient = new OkHttpClient();
		setUserAgent("JavaSDK" + AbstractConfiguration.SDK_VERSION);
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	/**
	 * Set the User-Agent header's value (by adding to the default header map).
	 *
	 * @param userAgent HTTP request's user agent
	 * @return ApiClient
	 */
	public ApiClient setUserAgent(String userAgent) {
		addHeader("User-Agent", userAgent);
		return this;
	}

	/**
	 * Get HTTP client
	 *
	 * @return An instance of OkHttpClient
	 */
	public OkHttpClient getHttpClient() {
		return httpClient;
	}

	/**
	 * Set HTTP client
	 *
	 * @param httpClient An instance of OkHttpClient
	 * @return Api Client
	 */
	public ApiClient setHttpClient(OkHttpClient httpClient) {
		this.httpClient = httpClient;
		return this;
	}

	public void addHeader(String headerKey, String value) {
		defaultHeaderMap.put(headerKey, value);
	}

	/**
	 * Check that whether debugging is enabled for this API client.
	 *
	 * @return True if debugging is enabled, false otherwise.
	 */
	public boolean isDebugging() {
		return debugging;
	}

	/**
	 * Enable/disable debugging for this API client.
	 *
	 * @param debugging To enable (true) or disable (false) debugging
	 * @return ApiClient
	 */
	public ApiClient setDebugging(boolean debugging) {
		if (debugging != this.debugging) {
			if (debugging) {
				loggingInterceptor = new HttpLoggingInterceptor();
				loggingInterceptor.setLevel(Level.BODY);
				httpClient.interceptors().add(loggingInterceptor);
			} else {
				httpClient.interceptors().remove(loggingInterceptor);
				loggingInterceptor = null;
			}
		}
		this.debugging = debugging;
		return this;
	}

	/**
	 * Get connection timeout (in milliseconds).
	 *
	 * @return Timeout in milliseconds
	 */
	public int getConnectTimeout() {
		return httpClient.getConnectTimeout();
	}

	/**
	 * Sets the connect timeout (in milliseconds). A value of 0 means no timeout, otherwise values must be between 1 and
	 * {@link Integer#MAX_VALUE}.
	 *
	 * @param connectionTimeout connection timeout in milliseconds
	 * @return Api client
	 */
	public ApiClient setConnectTimeout(int connectionTimeout) {
		httpClient.setConnectTimeout(connectionTimeout, TimeUnit.MILLISECONDS);
		return this;
	}

	/**
	 * Get read timeout (in milliseconds).
	 *
	 * @return Timeout in milliseconds
	 */
	public int getReadTimeout() {
		return httpClient.getReadTimeout();
	}

	/**
	 * Sets the read timeout (in milliseconds). A value of 0 means no timeout, otherwise values must be between 1 and
	 * {@link Integer#MAX_VALUE}.
	 *
	 * @param readTimeout read timeout in milliseconds
	 * @return Api client
	 */
	public ApiClient setReadTimeout(int readTimeout) {
		httpClient.setReadTimeout(readTimeout, TimeUnit.MILLISECONDS);
		return this;
	}

	/**
	 * Get write timeout (in milliseconds).
	 *
	 * @return Timeout in milliseconds
	 */
	public int getWriteTimeout() {
		return httpClient.getWriteTimeout();
	}

	/**
	 * Sets the write timeout (in milliseconds). A value of 0 means no timeout, otherwise values must be between 1 and
	 * {@link Integer#MAX_VALUE}.
	 *
	 * @param writeTimeout connection timeout in milliseconds
	 * @return Api client
	 */
	public ApiClient setWriteTimeout(int writeTimeout) {
		httpClient.setWriteTimeout(writeTimeout, TimeUnit.MILLISECONDS);
		return this;
	}

	/**
	 * Format the given parameter object into string.
	 *
	 * @param param Parameter
	 * @return String representation of the parameter
	 */
	public String parameterToString(Object param) {
		if (param == null) {
			return "";
		} else if (param instanceof Date || param instanceof OffsetDateTime || param instanceof LocalDate) {
			// Serialize to json string and remove the " enclosing characters
			String jsonStr = "";
			try {
				jsonStr = mapper.writeValueAsString(param);
			} catch (JsonProcessingException e) {
				String msg = String.format("Exception occurred. %s", e.getMessage());
				LOGGER.error(msg, e);
			}
			return jsonStr.substring(1, jsonStr.length() - 1);
		} else if (param instanceof Collection) {
			StringBuilder b = new StringBuilder();
			for (Object o : (Collection<?>) param) {
				if (b.length() > 0) {
					b.append(",");
				}
				b.append(String.valueOf(o));
			}
			return b.toString();
		} else {
			return String.valueOf(param);
		}
	}

	/**
	 * Formats the specified query parameter to a list containing a single {@code Pair} object.
	 *
	 * Note that {@code value} must not be a collection.
	 *
	 * @param name The name of the parameter.
	 * @param value The value of the parameter.
	 * @return A list containing a single {@code Pair} object.
	 */
	public List<Pair> parameterToPair(String name, Object value) {
		List<Pair> params = new ArrayList<>();
		// preconditions
		if (name == null || name.isEmpty() || value == null || value instanceof Collection)
			return params;
		params.add(new Pair(name, parameterToString(value)));
		return params;
	}

	/**
	 * Formats the specified collection query parameters to a list of {@code Pair} objects.
	 *
	 * Note that the values of each of the returned Pair objects are percent-encoded.
	 *
	 * @param collectionFormat The collection format of the parameter.
	 * @param name The name of the parameter.
	 * @param value The value of the parameter.
	 * @return A list of {@code Pair} objects.
	 */
	public List<Pair> parameterToPairs(String collectionFormat, String name, Collection<?> value) {
		List<Pair> params = new ArrayList<>();
		// preconditions
		if (name == null || name.isEmpty() || value == null || value.isEmpty()) {
			return params;
		}
		// create the params based on the collection format
		if ("multi".equals(collectionFormat)) {
			for (Object item : value) {
				params.add(new Pair(name, escapeString(parameterToString(item))));
			}
			return params;
		}
		// collectionFormat is assumed to be "csv" by default
		String delimiter = ",";
		// escape all delimiters except commas, which are URI reserved
		// characters
		if ("ssv".equals(collectionFormat)) {
			delimiter = escapeString(" ");
		} else if ("tsv".equals(collectionFormat)) {
			delimiter = escapeString("\t");
		} else if ("pipes".equals(collectionFormat)) {
			delimiter = escapeString("|");
		}
		StringBuilder sb = new StringBuilder();
		for (Object item : value) {
			sb.append(delimiter);
			sb.append(escapeString(parameterToString(item)));
		}
		params.add(new Pair(name, sb.substring(delimiter.length())));
		return params;
	}

	/**
	 * Escape the given string to be used as URL query value.
	 *
	 * @param str String to be escaped
	 * @return Escaped string
	 */
	public String escapeString(String str) {
		try {
			return URLEncoder.encode(str, "utf8").replaceAll("\\+", "%20");
		} catch (UnsupportedEncodingException e) {
			return str;
		}
	}

	/**
	 * Check if the given MIME is a JSON MIME. JSON MIME examples: application/json application/json; charset=UTF8
	 * APPLICATION/JSON application/vnd.company+json "* / *" is also default to JSON
	 * 
	 * @param mime MIME (Multipurpose Internet Mail Extensions)
	 * @return True if the given MIME is JSON, false otherwise.
	 */
	public boolean isJsonMime(String mime) {
		String jsonMime = "(?i)^(application/json|[^;/ \t]+/[^;/ \t]+[+]json)[ \t]*(;.*)?$";
		return mime != null && (mime.matches(jsonMime) || mime.equals("*/*"));
	}

	/**
	 * Select the Accept header's value from the given accepts array: if JSON exists in the given array, use it;
	 * otherwise use all of them (joining into a string)
	 *
	 * @param accepts The accepts array to select from
	 * @return The Accept header to use. If the given array is empty, null will be returned (not to set the Accept
	 *         header explicitly).
	 */
	public String selectHeaderAccept(String[] accepts) {
		if (accepts.length == 0) {
			return null;
		}
		for (String accept : accepts) {
			if (isJsonMime(accept)) {
				return accept;
			}
		}
		return StringUtil.join(accepts, ",");
	}

	/**
	 * Select the Content-Type header's value from the given array: if JSON exists in the given array, use it; otherwise
	 * use the first one of the array.
	 *
	 * @param contentTypes The Content-Type array to select from
	 * @return The Content-Type header to use. If the given array is empty, or matches "any", JSON will be used.
	 */
	public String selectHeaderContentType(String[] contentTypes) {
		if (contentTypes.length == 0 || contentTypes[0].equals("*/*")) {
			return APPLICATION_JSON;
		}
		for (String contentType : contentTypes) {
			if (isJsonMime(contentType)) {
				return contentType;
			}
		}
		return contentTypes[0];
	}

	/**
	 * Execute HTTP call and deserialize the HTTP response body into the given return type.
	 *
	 * @param returnType The return type used to deserialize HTTP response body
	 * @param <T> The return type corresponding to (same with) returnType
	 * @param call Call
	 * @return ApiResponse object containing response status, headers and data, which is a Java object deserialized from
	 *         response body and would be null when returnType is null.
	 * @throws ApiException If fail to execute the call
	 */
	public <T extends AbstractModelComponent> ApiResponse<T> execute(Call call, Class<T> returnType)
			throws ApiException {
		try {
			renewJwtContextIfRequired();
			Response response = call.execute();
			T data = handleResponse(response, returnType);
			return new ApiResponse<>(response.code(), response.headers().toMultimap(), data);
		} catch (IOException e) {
			logException(e);
			throw new ApiException(e);
		}
	}

	private void renewJwtContextIfRequired() {
		if (context instanceof AbstractJWTContext) {
			AbstractJWTContext<?> jwtContext = (AbstractJWTContext<?>) context;
			if (jwtContext.isAutoRenewToken()) {
				DecodedJWT jwtToken = JWT.decode(jwtContext.getJwtToken());
				if (jwtToken.getExpiresAt().before(new Date())) {
					AbstractJWTConfiguration jwtConfiguration =
							(AbstractJWTConfiguration) jwtContext.getConfiguration();
					String user = null;
					if (context.getContextType().equals(ContextType.USER)) {
						user = ((JWTUserConfiguration) context.getConfiguration()).getUser();
					}
					String renewedJwtToken =
							JWTUtil.getJWTToken(jwtConfiguration.getApiKey(), jwtConfiguration.getPrivateKey(), user,
									jwtConfiguration.getExpiresIn(), jwtConfiguration.getLocale());
					jwtContext.setJwtToken(renewedJwtToken);
				}
			}
		}
	}

	/**
	 * {@link ApiClient#executeAsync(Call, Class, ApiCallback)}
	 *
	 * @param <T> Type
	 * @param call An instance of the Call object
	 * @param callback ApiCallback&lt;T&gt;
	 * @throws ApiException - {@link ApiException}
	 */
	public <T extends AbstractModelComponent> void executeAsync(Call call, ApiCallback<T> callback)
			throws ApiException {
		executeAsync(call, null, callback);
	}

	/**
	 * Execute HTTP call asynchronously.
	 *
	 * @see #execute(Call, Class)
	 * @param <T> Type
	 * @param call The callback to be executed when the API call finishes
	 * @param returnType Return type
	 * @param callback ApiCallback
	 * @throws ApiException - {@link ApiException}
	 */
	public <T extends AbstractModelComponent> void executeAsync(Call call, final Class<T> returnType,
			final ApiCallback<T> callback) throws ApiException {
		if (callback == null) {
			throw new ApiException(ApiUtils.getErrorMessage("callback.required"));
		}
		renewJwtContextIfRequired();
		call.enqueue(new Callback() {

			@Override
			public void onFailure(Request request, IOException e) {
				callback.onFailure(new ApiException(e), 0, null);
			}

			@Override
			public void onResponse(Response response) throws IOException {
				T result;
				try {
					result = handleResponse(response, returnType);
				} catch (ApiException e) {
					callback.onFailure(e, response.code(), response.headers().toMultimap());
					return;
				}
				callback.onSuccess(result, response.code(), response.headers().toMultimap());
			}
		});
	}

	/**
	 * Handle the given response, return the deserialized object when the response is successful.
	 *
	 * @param <T> Type
	 * @param response Response
	 * @param returnType Return type
	 * @throws ApiException If the response has a unsuccessful status code or fail to deserialize the response body
	 * @return Type
	 */
	public <T extends AbstractModelComponent> T handleResponse(Response response, Class<T> returnType)
			throws ApiException {
		if (response.isSuccessful()) {
			return processSuccessResponse(response, returnType);
		} else {
			throw processFailureResponse(response);
		}
	}

	private <T extends AbstractModelComponent> T processSuccessResponse(Response response, Class<T> returnType)
			throws ApiException {
		if (returnType == null || response.code() == 204) {
			// returning null if the returnType is not defined,
			// or the status code is 204 (No Content)
			if (response.body() != null) {
				try {
					response.body().close();
				} catch (IOException e) {
					logException(e);
					throw new ApiException(response.message(), e, response.code(), response.headers().toMultimap());
				}
			}
			return null;
		} else {
			return deserialize(response, returnType);
		}
	}

	private ApiException processFailureResponse(Response response) {
		String respBody = null;
		if (response.body() != null) {
			try {
				respBody = response.body().string();
			} catch (IOException e) {
				logException(e);
				return new ApiException(response.message(), e, response.code(), response.headers().toMultimap());
			}
		}
		return new ApiException(response.message(), response.code(), response.headers().toMultimap(), respBody);
	}

	/**
	 * Deserialize response body to Java object, according to the return type and the Content-Type response header.
	 *
	 * @param <T> Type
	 * @param response HTTP response
	 * @param returnType The type of the Java object
	 * @return The deserialized Java object
	 * @throws ApiException If fail to deserialize response body, i.e. cannot read response body or the Content-Type of
	 *         the response is not supported.
	 */
	public <T extends AbstractModelComponent> T deserialize(Response response, Class<T> returnType)
			throws ApiException {
		if (response == null || returnType == null) {
			return null;
		}
		String respBody;
		try {
			if (response.body() != null)
				respBody = response.body().string();
			else
				respBody = null;
		} catch (IOException e) {
			throw new ApiException(e);
		}
		if (respBody == null || "".equals(respBody)) {
			return null;
		}
		String contentType = response.headers().get("Content-Type");
		if (contentType == null) {
			// ensuring a default content type
			contentType = APPLICATION_JSON;
		}
		if (isJsonMime(contentType)) {
			try {
				return mapper.readValue(respBody, returnType);
			} catch (IOException e) {
				throw new ApiException(e.getMessage());
			}
		} else {
			throw new ApiException("Content type \"" + contentType + "\" is not supported for type: " + returnType,
					response.code(), response.headers().toMultimap(), respBody);
		}
	}

	private void logException(Exception e) {
		String msg = String.format("Exception occurred. %s", e.getMessage());
		LOGGER.error(msg, e);
	}

	/**
	 * Build HTTP call with the given options.
	 *
	 * @param path The sub-path of the HTTP URL
	 * @param method The request method, one of "GET", "HEAD", "OPTIONS", "POST", "PUT", "PATCH" and "DELETE"
	 * @param queryParams The query parameters
	 * @param collectionQueryParams The collection query parameters
	 * @param body The request body object
	 * @param headerParams The header parameters
	 * @param formParams The form parameters
	 * @param authNames The authentications to apply
	 * @param progressRequestListener Progress request listener
	 * @return The HTTP call
	 * @throws ApiException If fail to serialize the request body object
	 */
	public Call buildCall(String path, String method, List<Pair> queryParams, List<Pair> collectionQueryParams,
			Object body, Map<String, String> headerParams, Map<String, Object> formParams, String[] authNames,
			ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
		Request request = buildRequest(path, method, queryParams, collectionQueryParams, body, headerParams, formParams,
				authNames, progressRequestListener);
		return httpClient.newCall(request);
	}

	public Call buildCall(ApiContext apiContext, ProgressRequestBody.ProgressRequestListener progressRequestListener)
			throws ApiException {
		Request request =
				buildRequest(apiContext.getPath(), apiContext.getHttpMethod().name(), apiContext.getQueryParams(),
						apiContext.getCollectionQueryParams(), apiContext.getBody(), apiContext.getHeaderParams(),
						apiContext.getFormParams(), apiContext.getAuthNames(), progressRequestListener);
		return httpClient.newCall(request);
	}

	/**
	 * Build an HTTP request with the given options.
	 *
	 * @param path The sub-path of the HTTP URL
	 * @param method The request method, one of "GET", "HEAD", "OPTIONS", "POST", "PUT", "PATCH" and "DELETE"
	 * @param queryParams The query parameters
	 * @param collectionQueryParams The collection query parameters
	 * @param body The request body object
	 * @param headerParams The header parameters
	 * @param formParams The form parameters
	 * @param authNames The authentications to apply
	 * @param progressRequestListener Progress request listener
	 * @return The HTTP request
	 * @throws ApiException If fail to serialize the request body object
	 */
	public Request buildRequest(String path, String method, List<Pair> queryParams, List<Pair> collectionQueryParams,
			Object body, Map<String, String> headerParams, Map<String, Object> formParams, String[] authNames,
			ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
		final String url = buildUrl(path, queryParams, collectionQueryParams);
		final Request.Builder reqBuilder = new Request.Builder().url(url);
		processHeaderParams(headerParams, reqBuilder);
		String contentType = headerParams.get("Content-Type");
		// ensuring a default content type
		if (contentType == null) {
			contentType = APPLICATION_JSON;
		}
		RequestBody reqBody;
		if (!HttpMethod.permitsRequestBody(method)) {
			reqBody = null;
		} else if ("application/x-www-form-urlencoded".equals(contentType)) {
			reqBody = buildRequestBodyFormEncoding(formParams);
		} else if ("multipart/form-data".equals(contentType)) {
			reqBody = buildRequestBodyMultipart(formParams);
		} else if (body == null) {
			if ("DELETE".equals(method)) {
				// allow calling DELETE without sending a request body
				reqBody = null;
			} else {
				// use an empty request body (for POST, PUT and PATCH)
				reqBody = RequestBody.create(MediaType.parse(contentType), "");
			}
		} else {
			reqBody = serialize(body, contentType);
		}
		Request request = null;
		if (progressRequestListener != null && reqBody != null) {
			ProgressRequestBody progressRequestBody = new ProgressRequestBody(reqBody, progressRequestListener);
			request = reqBuilder.method(method, progressRequestBody).build();
		} else {
			request = reqBuilder.method(method, reqBody).build();
		}
		return request;
	}

	/**
	 * Build full URL by concatenating base path, the given sub path and query parameters.
	 *
	 * @param path The sub path
	 * @param queryParams The query parameters
	 * @param collectionQueryParams The collection query parameters
	 * @return The full URL
	 */
	public String buildUrl(String path, List<Pair> queryParams, List<Pair> collectionQueryParams) {
		final StringBuilder url = new StringBuilder();
		url.append(basePath).append(path);
		if (queryParams != null && !queryParams.isEmpty()) {
			url.append(processQueryParams(path, queryParams));
		}
		if (collectionQueryParams != null && !collectionQueryParams.isEmpty()) {
			processCollectionQueryParams(collectionQueryParams, url);
		}
		return url.toString();
	}

	private void processCollectionQueryParams(List<Pair> collectionQueryParams, final StringBuilder url) {
		String prefix = url.toString().contains("?") ? "&" : "?";
		for (Pair param : collectionQueryParams) {
			if (param.getValue() != null) {
				if (prefix != null) {
					url.append(prefix);
					prefix = null;
				} else {
					url.append("&");
				}
				String value = parameterToString(param.getValue());
				// collection query parameter value already escaped as part of parameterToPairs
				url.append(escapeString(param.getName())).append("=").append(value);
			}
		}
	}

	private StringBuilder processQueryParams(String path, List<Pair> queryParams) {
		// support (constant) query string in `path`, e.g. "/posts?draft=1"
		StringBuilder url = new StringBuilder();
		String prefix = path.contains("?") ? "&" : "?";
		for (Pair param : queryParams) {
			if (param.getValue() != null) {
				if (prefix != null) {
					url.append(prefix);
					prefix = null;
				} else {
					url.append("&");
				}
				String value = parameterToString(param.getValue());
				url.append(escapeString(param.getName())).append("=").append(escapeString(value));
			}
		}
		return url;
	}

	/**
	 * Set header parameters to the request builder, including default headers.
	 *
	 * @param headerParams Header parameters in the ofrm of Map
	 * @param reqBuilder Reqeust.Builder
	 */
	public void processHeaderParams(Map<String, String> headerParams, Request.Builder reqBuilder) {
		for (Entry<String, String> param : headerParams.entrySet()) {
			reqBuilder.header(param.getKey(), parameterToString(param.getValue()));
		}
		for (Entry<String, String> header : defaultHeaderMap.entrySet()) {
			if (!headerParams.containsKey(header.getKey())) {
				reqBuilder.header(header.getKey(), parameterToString(header.getValue()));
			}
		}
	}

	/**
	 * Build a form-encoding request body with the given form parameters.
	 *
	 * @param formParams Form parameters in the form of Map
	 * @return RequestBody
	 */
	public RequestBody buildRequestBodyFormEncoding(Map<String, Object> formParams) {
		FormEncodingBuilder formBuilder = new FormEncodingBuilder();
		for (Entry<String, Object> param : formParams.entrySet()) {
			formBuilder.add(param.getKey(), parameterToString(param.getValue()));
		}
		return formBuilder.build();
	}

	/**
	 * Build a multipart (file uploading) request body with the given form parameters, which could contain text fields
	 * and file fields.
	 *
	 * @param formParams Form parameters in the form of Map
	 * @return RequestBody
	 */
	public RequestBody buildRequestBodyMultipart(Map<String, Object> formParams) {
		MultipartBuilder mpBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);
		for (Entry<String, Object> param : formParams.entrySet()) {
			if (param.getValue() instanceof File) {
				File file = (File) param.getValue();
				Headers partHeaders = Headers.of("Content-Disposition",
						"form-data; name=\"" + param.getKey() + "\"; filename=\"" + file.getName() + "\"");
				MediaType mediaType = MediaType.parse(guessContentTypeFromFile(file));
				mpBuilder.addPart(partHeaders, RequestBody.create(mediaType, file));
			} else {
				Headers partHeaders = Headers.of("Content-Disposition", "form-data; name=\"" + param.getKey() + "\"");
				mpBuilder.addPart(partHeaders, RequestBody.create(null, parameterToString(param.getValue())));
			}
		}
		return mpBuilder.build();
	}

	/**
	 * Guess Content-Type header from the given file (defaults to "application/octet-stream").
	 *
	 * @param file The given file
	 * @return The guessed Content-Type
	 */
	public String guessContentTypeFromFile(File file) {
		String contentType = URLConnection.guessContentTypeFromName(file.getName());
		if (contentType == null) {
			return "application/octet-stream";
		} else {
			return contentType;
		}
	}

	/**
	 * Serialize the given Java object into request body according to the object's class and the request Content-Type.
	 *
	 * @param obj The Java object
	 * @param contentType The request Content-Type
	 * @return The serialized request body
	 * @throws ApiException If fail to serialize the given object
	 */
	public RequestBody serialize(Object obj, String contentType) throws ApiException {
		if (obj instanceof byte[]) {
			// Binary (byte array) body parameter support.
			return RequestBody.create(MediaType.parse(contentType), (byte[]) obj);
		} else if (obj instanceof File) {
			// File body parameter support.
			return RequestBody.create(MediaType.parse(contentType), (File) obj);
		} else if (isJsonMime(contentType)) {
			String content;
			if (obj != null) {
				try {
					content = mapper.writeValueAsString(obj);
				} catch (JsonProcessingException e) {
					content = null;
				}
			} else {
				content = null;
			}
			return RequestBody.create(MediaType.parse(contentType), content);
		} else {
			String msg = String.format("Content type %s is not supported", contentType);
			throw new ApiException(Collections.<Problem>emptyList(), msg, false);
		}
	}
}
