/*
 * Copyright 2020 Fernando Glatz. All Rights Reserved.
 */
package com.fernandoglatz.callmaker.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNS;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNSRecord;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.event.S3EventNotification.S3BucketEntity;
import com.amazonaws.services.s3.event.S3EventNotification.S3Entity;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.event.S3EventNotification.S3ObjectEntity;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.fernandoglatz.callmaker.dto.MakeCallDTO;
import com.fernandoglatz.callmaker.dto.MakeCallResponseDTO;
import com.fernandoglatz.callmaker.util.EmailUtils;
import com.fernandoglatz.callmaker.util.TotalVoiceApi;

/**
 * @author fernando.glatz
 */
public class SNSHandler extends AbstractRequestHandler<SNSEvent, String> {

	private static final String API_KEY = "api_key";
	private static final String VOICE_TYPE = "voice_type";
	private static final String FROM_NUMBER = "from_number";
	private static final String TO_NUMBER = "to_number";
	private static final String SUBJECT = "subject";
	private static final String CONTENT = "content";
	private static final String MESSAGE = "message";
	private static final String SEPARATOR = ";";

	@Override
	public String handleRequest(SNSEvent event, Context context) {
		try {
			List<SNSRecord> records = event.getRecords();
			for (SNSRecord record : records) {
				SNS sns = record.getSNS();
				String message = sns.getMessage();

				S3EventNotification s3Event = S3EventNotification.parseJson(message);
				List<S3EventNotificationRecord> s3Records = s3Event.getRecords();

				for (S3EventNotificationRecord s3record : s3Records) {
					S3Entity s3 = s3record.getS3();
					S3BucketEntity bucket = s3.getBucket();
					S3ObjectEntity object = s3.getObject();

					String bucketName = bucket.getName();
					String objectKey = object.getKey();

					AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
					S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, objectKey));

					try (InputStream inputStream = s3Object.getObjectContent()) {
						processEmail(inputStream);
					}
				}
			}
		} catch (Exception e) {
			logError(e);
			return "Error on execution";
		}

		return "Executed";
	}

	private void processEmail(InputStream inputStream) throws MessagingException, IOException {
		Map<String, String> env = System.getenv();
		String apiKey = env.get(API_KEY);
		String voiceType = env.get(VOICE_TYPE);
		String fromNumber = env.get(FROM_NUMBER);
		String toNumber = env.get(TO_NUMBER);
		String subject = env.get(SUBJECT);
		String content = env.get(CONTENT);
		String message = env.get(MESSAGE);
		String[] toNumbers = toNumber.split(SEPARATOR);

		MimeMessage mimeMessage = new MimeMessage(null, inputStream);
		String emailSubject = mimeMessage.getSubject();
		String emailContent = EmailUtils.getContent(mimeMessage);

		logInfo("Subject: " + subject);
		//logInfo("Content: " + emailContent);

		if (subject.equalsIgnoreCase(emailSubject) && emailContent != null && content != null //
				&& emailContent.toLowerCase().contains(content.toLowerCase())) {

			TotalVoiceApi totalVoiceApi = new TotalVoiceApi(apiKey);

			for (String number : toNumbers) {
				makeCall(totalVoiceApi, fromNumber, number.trim(), message, voiceType);
			}
		} else {
			logInfo("Ignoring email...");
		}
	}

	private void makeCall(TotalVoiceApi totalVoiceApi, String fromNumber, String toNumber, String message, String voiceType) throws IOException {
		MakeCallDTO dto = new MakeCallDTO();
		dto.setFromNumber(fromNumber);
		dto.setToNumber(toNumber);
		dto.setMessage(message);
		dto.setVoiceType(voiceType);

		logInfo("Calling " + toNumber);

		MakeCallResponseDTO responseDTO = totalVoiceApi.makeCall(dto);
		String responseMessage = responseDTO.getMessage();

		logInfo("TotalVoice response: " + responseMessage);
	}
}
