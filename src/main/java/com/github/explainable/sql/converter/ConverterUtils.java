package com.github.explainable.sql.converter;

import com.github.explainable.sql.SqlException;

import javax.annotation.Nullable;

final class ConverterUtils {
	private ConverterUtils() {
		// Prevent the class from being accidentally instantiated using reflection.
		throw new UnsupportedOperationException("ConvertedUtils cannot be instantiated");
	}

	static void checkUnsupportedFeature(@Nullable Object featureValue, String description) {
		if (featureValue != null) {
			throw new SqlException("Unsupported SQL Feature: " + description);
		}
	}

	static void checkUnsupportedFlag(boolean flag, String description) {
		if (flag) {
			throw new SqlException("Unsupported SQL Feature: " + description);
		}
	}
}
