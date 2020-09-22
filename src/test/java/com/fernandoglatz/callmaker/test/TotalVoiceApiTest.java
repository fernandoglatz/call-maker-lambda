/*
 * Copyright 2020 Fernando Glatz. All Rights Reserved.
 */
package com.fernandoglatz.callmaker.test;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.fernandoglatz.callmaker.dto.MakeCallDTO;

/**
 * @author fernandoglatz
 */
public class TotalVoiceApiTest {

	@Test
	public void makeCallTest() throws IOException {

		MakeCallDTO tts = new MakeCallDTO();
		tts.setFromNumber("****");
		tts.setToNumber("****");
		tts.setMessage("Test message");

		//		TotalVoiceApi totalVoiceApi = new TotalVoiceApi("****");
		//		MakeCallResponseDTO ttsResponse = totalVoiceApi.makeCall(tts);
		//		String responseMessage = ttsResponse.getMessage();
		//		System.out.println(responseMessage);
	}
}
