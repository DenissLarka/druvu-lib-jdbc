package com.druvu.lib.jdbc.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

import com.druvu.lib.jdbc.statement.SimpleSql;
import com.druvu.lib.jdbc.statement.SimpleSqlStatement;
import com.druvu.lib.jdbc.statement.SqlStatement;

/**
 * @author Deniss Larka
 * on 03 May 2022
 */
public final class SqlLoader {

	public static final String EMPTY = "";

	private SqlLoader() {
	}

	public static String load(String mainPath, String... includePaths) {
		final String sqlContent = SqlLoader.resourceAsString(mainPath);
		return compose(sqlContent, includePaths);
	}

	public static String loadFromFile(String mainPath) {
		final String sqlContent = SqlLoader.fileAsString(mainPath);
		return compose(sqlContent);
	}

	private static String compose(String sqlContent, String... includePaths) {
		final String[] sqlIncludes = resourcesAsStrings(includePaths);
		final int placeholderCount = PlaceholderUtils.countIncludePlaceholders(sqlContent);
		//no placeholders, no filling
		final String content = placeholderCount > 0
				? String.format(sqlContent, PlaceholderUtils.resize(sqlIncludes, placeholderCount))
				: sqlContent;
		//if no includePaths supplied for some placeholders we clean them
		return content.replaceAll(PlaceholderUtils.INCLUDE_PLACEHOLDER, EMPTY).trim();
	}

	public static void loadBulk(String resourcePath, Consumer<SqlStatement<Map<String, Object>>> consumer) {
		if (resourcePath == null || resourcePath.matches("\\s*")) {
			return;
		}
		try (InputStream inputStream = SqlLoader.resourceAsStream(resourcePath)) {
			if (inputStream == null) {
				throw new IllegalArgumentException("Resource not found:" + resourcePath);
			}
			inputStreamToBulkStrings(inputStream, consumer);
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private static String resourceAsString(String resourcePath) {
		if (resourcePath == null || resourcePath.matches("\\s*")) {
			return EMPTY;
		}
		String content;
		try (InputStream inputStream = resourceAsStream(resourcePath)) {
			content = inputStreamToString(inputStream);
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
		if (content != null) {
			return content;
		}
		throw new IllegalStateException("Resource/File not found:" + resourcePath);
	}

	private static String fileAsString(String filePath) {
		if (filePath == null || filePath.matches("\\s*")) {
			return EMPTY;
		}
		try {
			final byte[] bytes = Files.readAllBytes(Paths.get(filePath));
			return new String(bytes, StandardCharsets.UTF_8);
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public static InputStream resourceAsStream(String resourcePath) {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
		if (inputStream == null) {
			return resourceAsStreamStatic(resourcePath);
		}
		return inputStream;
	}

	//All around the loading resources is fragile and change drastically in JPMS.
	//This one is just another try to get the resource
	private static InputStream resourceAsStreamStatic(String resourcePath) {
		return SimpleSql.class.getResourceAsStream(resourcePath.indexOf(0) == '/' ? resourcePath : '/' + resourcePath);
	}

	//load resource as string
	//performance is not so important in the SQL loading
	private static String inputStreamToString(InputStream inputStream) {
		if (inputStream == null) {
			return null;
		}
		try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name()).useDelimiter("\\A")) {
			return scanner.hasNext() ? scanner.next() : EMPTY;
		}
	}

	private static void inputStreamToBulkStrings(InputStream inputStream, Consumer<SqlStatement<Map<String, Object>>> consumer) {
		try (Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter(";")) {
			while (scanner.hasNext()) {
				final String sqlContent = scanner.next().trim();
				if (sqlContent.isEmpty()) {
					continue;
				}
				consumer.accept(new SimpleSqlStatement(sqlContent));
			}
		}
	}

	private static String[] resourcesAsStrings(String[] args) {
		if (args == null) {
			return new String[0];
		}
		return Arrays.stream(args).map(SqlLoader::resourceAsString).toArray(size -> new String[size]);
	}
}
