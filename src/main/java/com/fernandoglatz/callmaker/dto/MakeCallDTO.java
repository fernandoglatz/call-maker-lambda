/*
 * Copyright 2020 Fernando Glatz. All Rights Reserved.
 */
package com.fernandoglatz.callmaker.dto;

import com.google.gson.annotations.SerializedName;

/**
 * @author fernando.glatz
 */
public class MakeCallDTO {

	@SerializedName("numero_destino")
	private String toNumber;

	@SerializedName("mensagem")
	private String message;

	@SerializedName("resposta_usuario")
	private Boolean waitResponse = false;

	@SerializedName("tipo_voz")
	private String voiceType;

	@SerializedName("bina")
	private String fromNumber;

	@SerializedName("gravar_audio")
	private Boolean audioRecord = false;

	@SerializedName("detecta_caixa")
	private Boolean detectPoBox = true;

	@SerializedName("bina_inteligente")
	private Boolean smartId = false;

	public String getToNumber() {
		return toNumber;
	}

	public void setToNumber(String toNumber) {
		this.toNumber = toNumber;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean isWaitResponse() {
		return waitResponse;
	}

	public void setWaitResponse(Boolean waitResponse) {
		this.waitResponse = waitResponse;
	}

	public String getVoiceType() {
		return voiceType;
	}

	public void setVoiceType(String voiceType) {
		this.voiceType = voiceType;
	}

	public String getFromNumber() {
		return fromNumber;
	}

	public void setFromNumber(String fromNumber) {
		this.fromNumber = fromNumber;
	}

	public Boolean isAudioRecord() {
		return audioRecord;
	}

	public void setAudioRecord(Boolean audioRecord) {
		this.audioRecord = audioRecord;
	}

	public Boolean isDetectPoBox() {
		return detectPoBox;
	}

	public void setDetectPoBox(Boolean detectPoBox) {
		this.detectPoBox = detectPoBox;
	}

	public Boolean isSmartId() {
		return smartId;
	}

	public void setSmartId(Boolean smartId) {
		this.smartId = smartId;
	}
}
