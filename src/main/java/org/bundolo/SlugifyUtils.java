package org.bundolo;

import java.text.Normalizer;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class SlugifyUtils {

	private static final Logger logger = Logger.getLogger(SlugifyUtils.class.getName());

	@Autowired
	@Qualifier("slugifyProperties")
	private Properties replacements;

	private Map<String, String> customReplacements;
	private boolean lowerCase = true;

	public String slugify(String input) {
		logger.log(Level.FINE, "slugify input: " + input);
		if (input == null) {
			return "";
		}

		input = input.trim();

		Map<String, String> customReplacements = getCustomReplacements();
		if (customReplacements != null) {
			for (Entry<String, String> entry : customReplacements.entrySet()) {
				input = input.replace(entry.getKey(), entry.getValue());
			}
		}

		for (Entry<Object, Object> e : replacements.entrySet()) {
			input = input.replace((String) e.getKey(), (String) e.getValue());
		}

		input = Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
				.replaceAll("[^\\w+]", "-").replaceAll("\\s+", "-").replaceAll("[-]+", "-").replaceAll("^-", "")
				.replaceAll("-$", "");

		if (getLowerCase()) {
			input = input.toLowerCase();
		}

		logger.log(Level.FINE, "slugify result: ###start###" + input + "###end###");
		return input;
	}

	public Map<String, String> getCustomReplacements() {
		return customReplacements;
	}

	public void setCustomReplacements(Map<String, String> customReplacements) {
		this.customReplacements = customReplacements;
	}

	public boolean getLowerCase() {
		return lowerCase;
	}

	public void setLowerCase(boolean lowerCase) {
		this.lowerCase = lowerCase;
	}
}
