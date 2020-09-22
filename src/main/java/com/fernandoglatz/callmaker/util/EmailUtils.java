/*
 * Copyright 2020 Fernando Glatz. All Rights Reserved.
 */
package com.fernandoglatz.callmaker.util;

import java.io.IOException;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;

import org.jsoup.Jsoup;

/**
 * @author fernando.glatz
 */
public class EmailUtils {

	private static final String TEXT_PLAIN = "text/plain";
	private static final String LINE_FEED = "\r\n";

	public static String getContent(Message message) throws MessagingException, IOException {
		StringBuilder sb = new StringBuilder();
		Object content = message.getContent();

		if (message.isMimeType(TEXT_PLAIN)) {
			sb.append(content);
		} else if (content instanceof Multipart) {
			Multipart multipart = (Multipart) content;
			getContentFromMultipart(sb, multipart);
		}

		return sb.toString();
	}

	private static void getContentFromMultipart(StringBuilder sb, Multipart multipart) throws MessagingException, IOException {
		int count = multipart.getCount();

		for (int i = 0; i < count; i++) {
			BodyPart bodyPart = multipart.getBodyPart(i);
			Object content = bodyPart.getContent();

			if (bodyPart.isMimeType(TEXT_PLAIN)) {
				sb.append(content).append(LINE_FEED);

			} else if (bodyPart.isMimeType(TEXT_PLAIN)) {
				String html = Jsoup.parse(content.toString()).text();
				sb.append(html).append(LINE_FEED);

			} else if (content instanceof Multipart) {
				getContentFromMultipart(sb, (Multipart) content);
			}
		}
	}
}
