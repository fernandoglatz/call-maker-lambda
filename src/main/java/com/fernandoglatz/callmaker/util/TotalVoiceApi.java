/*
 * Copyright 2020 Fernando Glatz. All Rights Reserved.
 */
package com.fernandoglatz.callmaker.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fernandoglatz.callmaker.dto.MakeCallDTO;
import com.fernandoglatz.callmaker.dto.MakeCallResponseDTO;

/**
 * @author fernandoglatz
 */
public class TotalVoiceApi {

	private static final String POST = "POST";
	private static final String ACCESS_TOKEN = "Access-Token";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String APPLICATION_JSON = "application/json";
	private static final Integer CONNECTION_TIMEOUT = 600000; //10 minutes

	private static final Integer START_HTTP_2XX = 200;
	private static final Integer START_HTTP_3XX = 300;
	private static final Integer END_HTTP_3XX = 399;

	private static final String VOICE_TYPE_DEFAULT = "br-Camila";
	private static final String TOTAL_VOICE_TTS_URL = "https://api.totalvoice.com.br/tts";

	private final String apiKey;

	public TotalVoiceApi(String apiKey) {
		this.apiKey = apiKey;
	}

	public MakeCallResponseDTO makeCall(MakeCallDTO dto) throws IOException {
		if (StringUtils.isEmpty(dto.getVoiceType())) {
			dto.setVoiceType(VOICE_TYPE_DEFAULT);
		}

		ObjectMapper objectMapper = JsonUtils.getMapper();
		String json = objectMapper.writeValueAsString(dto);
		String jsonResponse = sendRequest(TOTAL_VOICE_TTS_URL, json);

		return objectMapper.readValue(jsonResponse, MakeCallResponseDTO.class);
	}

	private String sendRequest(String urlStr, String json) throws IOException {
		String response;
		URL url = new URL(urlStr);
		HttpURLConnection connection = null;

		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(POST);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestProperty(ACCESS_TOKEN, getApiKey());
			connection.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);

			connection.setConnectTimeout(CONNECTION_TIMEOUT);
			connection.setReadTimeout(CONNECTION_TIMEOUT);

			try (OutputStream outputStream = connection.getOutputStream()) {
				IOUtils.write(json, outputStream, StandardCharsets.UTF_8);
				outputStream.flush();
			}

			Integer responseCode = connection.getResponseCode();

			if (responseCode >= START_HTTP_2XX && responseCode <= END_HTTP_3XX) {
				try (InputStream inputStream = connection.getInputStream()) {
					response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
				}
			} else {
				try (InputStream inputStream = connection.getErrorStream()) {
					response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
				}
			}

			if (responseCode >= START_HTTP_3XX && responseCode <= END_HTTP_3XX) {
				response = sendRequest(response, json); //redirect
			}
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		return response;
	}

	public String getApiKey() {
		return apiKey;
	}
}
