package com.druvu.lib.jdbc.util;

import com.druvu.lib.jdbc.SqlStatement;
import java.util.Arrays;
import java.util.regex.Matcher;

/**
 * @author Deniss Larka <br>
 *     on 15 Jul 2021
 */
public final class SqlDebug {

    private SqlDebug() {}

    // fill out prepared statement with actual arguments
    public static String debug(String sql, Object[] parameters) {
        return (String) Arrays.stream(parameters).reduce(sql, SqlDebug::fillPlaceholder);
    }

    public static String debug(SqlStatement<?> sqlStatement) {
        return debug(sqlStatement.getQuery(), sqlStatement.getParameters());
    }

    private static Object fillPlaceholder(Object str, Object parameter) {
        // quoteReplacement: '$' and '\' in values are otherwise interpreted as regex group references
        return ((String) str).replaceFirst("\\?", Matcher.quoteReplacement(fillParameter(parameter)));
    }

    private static String fillParameter(Object parameter) {
        if (parameter instanceof Number) {
            return String.valueOf(parameter);
        }
        return "'" + escape(parameter) + "'";
    }

    private static String escape(Object parameter) {
        return String.valueOf(parameter).replaceAll("\\'", "\\'\\'");
    }
}
