/*
 * Copyright 2020 Fernando Glatz. All Rights Reserved.
 */
package com.fernandoglatz.callmaker.dto;

import com.google.gson.annotations.SerializedName;

/**
 * @author fernando.glatz
 */
public class MakeCallResponseDTO {

	@SerializedName("status")
	private Integer status;

	@SerializedName("sucesso")
	private Boolean completed;

	@SerializedName("motivo")
	private Integer reason;

	@SerializedName("mensagem")
	private String message;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Boolean getCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	public Integer getReason() {
		return reason;
	}

	public void setReason(Integer reason) {
		this.reason = reason;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
