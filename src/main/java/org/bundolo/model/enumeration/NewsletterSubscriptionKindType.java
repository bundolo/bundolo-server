package org.bundolo.model.enumeration;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public enum NewsletterSubscriptionKindType {
	daily, weekly, monthly, bulletin;

	@Override
	public String toString() {
		return super.toString();
	}

	public static String getStringRepresentation(List<NewsletterSubscriptionKindType> newsletterSubscriptions) {
		StringBuilder result = new StringBuilder();
		result.append("[");
		if (newsletterSubscriptions != null) {
			String prefix = "";
			String quote = "\"";
			for (NewsletterSubscriptionKindType newsletterSubscription : newsletterSubscriptions) {
				result.append(prefix + quote + newsletterSubscription.toString() + quote);
				if (StringUtils.isBlank(prefix)) {
					prefix = ",";
				}
			}
		}
		result.append("]");
		return result.toString();
	}

	public static List<NewsletterSubscriptionKindType> getListRepresentation(String newsletterSubscriptions) {
		List<NewsletterSubscriptionKindType> result = new ArrayList<NewsletterSubscriptionKindType>();
		if (StringUtils.isNotBlank(newsletterSubscriptions)) {
			// trim [ and ]
			newsletterSubscriptions = newsletterSubscriptions.substring(1, newsletterSubscriptions.length() - 1);
			String[] elements = newsletterSubscriptions.split(",");
			for (String element : elements) {
				if (StringUtils.isNotBlank(element)) {
					// trim quotes
					element = element.substring(1, element.length() - 1);
					result.add(NewsletterSubscriptionKindType.valueOf(element));
				}
			}
		}
		return result;
	}
}
