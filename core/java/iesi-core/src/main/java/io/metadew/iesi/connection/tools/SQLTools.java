package io.metadew.iesi.connection.tools;

import java.io.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

public final class SQLTools {

    // Insert statement tools
    public static String GetStringForSQL(String input) {
        if (input == null) {
            return "null";
        } else {
            return "'" + GetCleanString(input) + "'";
        }
    }

    public static String GetStringForSQLTable(String input) {
        if (input == null) {
            return "\"null\"";
        } else {
            return "\"" + GetCleanString(input) + "\"";
        }
    }

    public static String GetStringForSQL(boolean input) {
        return "'" + (input ? "Y" : "N") + "'";
    }

    public static String GetStringForSQL(List<String> list) {
        return list == null || list.isEmpty() ? "null" : "'" + String.join(",", list) + "'";
    }

    public static String GetStringForSQL(Map<String, String> map) {
        return map == null || map.isEmpty() ? "null" : "'" + map.entrySet().stream().map(entry -> entry.getKey() + ":" + entry.getValue()).collect(Collectors.joining(",")) + "'";
    }

    public static String GetStringForSQL(Long _long) {
        return _long == null ? "null" : _long.toString();
    }

    public static String GetStringForSQL(Timestamp input) {
        if (input == null) {
            return "null";
        } else {
            return "'" + GetCleanString(input.toString()) + "'";
        }
    }

    public static String GetStringForSQL(LocalDateTime input) {
        if (input == null) {
            return "null";
        } else {
            return "'" + GetCleanString(input.toString()) + "'";
        }
    }

    public static String GetStringForSQL(int input) {
        return Integer.toString(input);
    }

    public static String GetStringForSQL(long input) {
        return Long.toString(input);
    }

    private static String GetCleanString(String input) {
        String cleanString = input;
        return cleanString.replace("'", "''");
    }


    // Identifier tools
    public static String GetNextIdStatement(String tableName, String idFieldName) {
        String result = "";
        result += "select coalesce(max(" + idFieldName + ")+1,1) as \"" + idFieldName + "\" from " + tableName;
        return result;
    }

    public static String GetLookupIdStatement(String tableName, String idFieldName, String lookupFieldName,
                                              String lookupFieldValue) {
        String result = "";
        result += "select " + idFieldName + " from " + tableName + " where " + lookupFieldName + " = '"
                + lookupFieldValue + "'";
        return result;
    }

    public static String GetLookupIdStatement(String tableName, String idFieldName, String lookupWhereClause) {
        String result = "";
        if (lookupWhereClause == null) lookupWhereClause = "";
        if (lookupWhereClause.equalsIgnoreCase("")) {
            result += "select " + idFieldName + " from " + tableName;
        } else {
            result += "select " + idFieldName + " from " + tableName + " " + lookupWhereClause;
        }
        return result;
    }

    // Resultset tools
    public static int getRowCount(ResultSet rs) {
        int rowCount = -1;
        try {
            rs.last();
            rowCount = rs.getRow();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }

        return rowCount;
    }

    // File tools
    public static String getFirstSQLStmt(String filePath, String fileName) {
        return getFirstSQLStmt(filePath + File.separator + fileName);
    }

    public static String getFirstSQLStmt(String absoluteFilePath) {
        String result = "";
        try (BufferedReader br = new BufferedReader(new FileReader(absoluteFilePath))) {

            String line;
            while ((line = br.readLine()) != null) {
                if (line.indexOf(";") < 0) {
                    if (!result.equalsIgnoreCase(""))
                        result = result + " ";
                    result = result + line;
                } else if (line.indexOf(";") == 0) {
                    break;
                } else if (line.indexOf(";") > 0) {
                    if (!result.equalsIgnoreCase(""))
                        result = result + " ";
                    result = result + line.substring(0, line.indexOf(";"));
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    //
    public static String getCreateStmt(ResultSetMetaData rsmd, String target, Boolean ifExists) throws SQLException {
        int cols = rsmd.getColumnCount();
        String CreateIfExists = "";
        if (ifExists) {
            //sqlite
            CreateIfExists = "Create table if not exists ";
        } else {
            //sqlite
            CreateIfExists = "Create table ";
        }
        StringBuilder sb = new StringBuilder(1024);
        if (cols > 0) {
            if (target != null && !target.isEmpty()) {
                sb.append(CreateIfExists).append(target).append(" ( ");
            } else {
                sb.append(CreateIfExists).append(rsmd.getTableName(1)).append(" ( ");
            }
        }
        for (int i = 1; i <= cols; i++) {
            if (i > 1) sb.append(", ");
            String columnName = rsmd.getColumnLabel(i);
            String columnType = rsmd.getColumnTypeName(i);

            sb.append(columnName).append(" ").append(columnType);

            int precision = rsmd.getPrecision(i);
            if (precision != 0) {
                sb.append("( ").append(precision).append(" )");
            }
        } // for columns
        sb.append(" ) ");

        return sb.toString();
    }

    public static String getInsertPstmt(ResultSetMetaData rsmd, String target) throws SQLException {

        int cols = rsmd.getColumnCount();

        String sql = "";
        if (target != null && !target.isEmpty()) {
            sql = "insert into " + target + " (";
        } else {
            sql = "insert into " + rsmd.getTableName(1) + " (";
        }

        // Get the column names; column indices start from 1
        for (int i = 1; i < cols + 1; i++) {
            sql = sql + rsmd.getColumnName(i);
            if (i != cols) {
                sql = sql + ",";
            }
        }
        sql = sql + ") values (";
        for (int i = 1; i < cols + 1; i++) {
            sql = sql + "?";
            if (i != cols) {
                sql = sql + ",";
            }
        }
        sql = sql + ")";

        return sql;
    }

    public static String getDropStmt(String target, boolean ifExists) {
        String sql = "";
        if (ifExists) {
            sql = "drop table if exists " + target;
        } else {
            sql = "drop table " + target;
        }
        return sql;
    }

    public static LocalDateTime getLocalDatetimeFromSql(String localDateTime) {
        return localDateTime == null ? null : LocalDateTime.parse(localDateTime, new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd[ ]['T']HH:mm:ss")
                .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 9, true)
                .toFormatter());
    }

    public static List<String> getListFromSql(String list) {
        return list == null ? new ArrayList<>() : Arrays.stream(list.split(",")).collect(Collectors.toList());
    }

    public static boolean getBooleanFromSql(String bool) {
        return bool.equalsIgnoreCase("y");
    }

    public static Map<String, String> getMapFromSql(String map) {
        return map == null ? new HashMap<>() : Arrays.stream(map.split(",")).collect(Collectors.toMap(s -> s.split(":")[0], s -> {
            if (s.split(":").length == 2) {
                return s.split(":")[1];
            } else {
                return "";
            }
        }));
    }
}
