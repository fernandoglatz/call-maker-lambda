/*
 * Copyright 2020 Fernando Glatz. All Rights Reserved.
 */
package com.fernandoglatz.callmaker.util;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Whitelist;

/**
 * @author fernandoglatz
 */
public class EmailUtils {

	private static final String TEXT_PLAIN = "text/plain";
	private static final String TEXT_HTML = "text/html";
	private static final String LINE_FEED = "\r\n";

	public static String getContent(Message message) throws MessagingException, IOException {
		Set<String> stringParts = new LinkedHashSet<>();

		getContentFromPart(stringParts, message);
		StringBuilder sb = new StringBuilder();

		for (String stringPart : stringParts) {
			if (sb.length() == 0) {
				sb.append(LINE_FEED);
			}

			sb.append(stringPart);
		}

		return sb.toString();
	}

	private static void getContentFromPart(Set<String> stringParts, Part part) throws MessagingException, IOException {
		Object content = part.getContent();

		if (part.isMimeType(TEXT_PLAIN)) {
			stringParts.add(String.valueOf(content).concat(LINE_FEED));

		} else if (part.isMimeType(TEXT_HTML)) {
			getContentFromHtmlPart(stringParts, content);

		} else if (content instanceof Multipart) {
			getContentFromMultipart(stringParts, (Multipart) content);
		}
	}

	private static void getContentFromHtmlPart(Set<String> stringParts, Object content) {
		Document jsoupDoc = Jsoup.parse(String.valueOf(content));

		OutputSettings outputSettings = new OutputSettings();
		outputSettings.prettyPrint(false);
		jsoupDoc.outputSettings(outputSettings);
		jsoupDoc.select("br").before("\\r\\n");
		jsoupDoc.select("p").before("\\r\\n");

		String newStr = jsoupDoc.html().replaceAll("\\\\r\\\\n", LINE_FEED);
		String prettyContent = Jsoup.clean(newStr, StringUtils.EMPTY, Whitelist.none(), outputSettings);
		stringParts.add(String.valueOf(prettyContent));
	}

	private static void getContentFromMultipart(Set<String> stringParts, Multipart multipart) throws MessagingException, IOException {
		int count = multipart.getCount();

		for (int i = 0; i < count; i++) {
			BodyPart part = multipart.getBodyPart(i);
			getContentFromPart(stringParts, part);
		}
	}
}
