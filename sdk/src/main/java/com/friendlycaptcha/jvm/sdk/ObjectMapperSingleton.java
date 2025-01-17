package com.friendlycaptcha.jvm.sdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Singleton class for ObjectMapper. ObjectMapper is thread-safe and can be reused, and is expensive to create.
 */
public final class ObjectMapperSingleton {
	protected static final ObjectMapper objectMapperSingleton = JsonMapper.builder()
		.findAndAddModules().addModule(new JavaTimeModule()).build();

	public static ObjectMapper getInstance() {
		return objectMapperSingleton;
	}
}