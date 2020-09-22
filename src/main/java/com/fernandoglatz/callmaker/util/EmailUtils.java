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
	private static final String LINE_FEED_REGEX = "\\R";

	public static String getContent(Message message) throws IOException, MessagingException {
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

	private static void getContentFromPart(Set<String> stringParts, Part part) throws IOException, MessagingException {
		Object content = part.getContent();

		if (part.isMimeType(TEXT_PLAIN)) {
			String contentStr = String.valueOf(content);
			removeLastLineFeeds(contentStr);
			stringParts.add(contentStr);

		} else if (part.isMimeType(TEXT_HTML)) {
			String contentStr = getContentFromHtmlPart(part);
			stringParts.add(contentStr);

		} else if (content instanceof Multipart) {
			getContentFromMultipart(stringParts, (Multipart) content);
		}
	}

	private static String removeLastLineFeeds(String contentStr) {
		while (contentStr.endsWith(LINE_FEED)) {
			contentStr = StringUtils.chomp(contentStr);
		}
		return contentStr;
	}

	private static String getContentFromHtmlPart(Part part) throws IOException, MessagingException {
		Object content = part.getContent();
		String contentStr = String.valueOf(content).replaceAll(LINE_FEED_REGEX, "");
		Document jsoupDoc = Jsoup.parse(contentStr);

		OutputSettings outputSettings = new OutputSettings();
		outputSettings.prettyPrint(false);
		jsoupDoc.outputSettings(outputSettings);
		jsoupDoc.select("br").before("\\r\\n");
		jsoupDoc.select("p").before("\\r\\n");

		String newStr = jsoupDoc.html().replaceAll("\\\\r\\\\n", LINE_FEED);
		return Jsoup.clean(newStr, StringUtils.EMPTY, Whitelist.none(), outputSettings);
	}

	private static void getContentFromMultipart(Set<String> stringParts, Multipart multipart) throws IOException, MessagingException {
		int count = multipart.getCount();

		for (int i = 0; i < count; i++) {
			BodyPart part = multipart.getBodyPart(i);
			getContentFromPart(stringParts, part);
		}
	}
}
