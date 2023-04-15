package com.example.JApi.service;

import com.example.JApi.configuration.Connect;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;

@Service
public class ProcedureService {
    private final Connect connect;

    public ProcedureService(Connect connect){
        this.connect = connect;
    }

    public List<Map<String, Object>> procedure(String connection, String name) throws Exception {
        Map<String, Connection> connectionMap = connect.getConnection();
        Connection connectionInstance = connectionMap.get(connection);
        if(connectionInstance == null){
            throw new Exception("Соединение с именем " + connection + " не найдено");
        }

        // удалить и новой обработкой
        return procedureMySql(connectionInstance, name);
        // удалить и новой обработкой
    }

    private List<Map<String, Object>> parsingResultSet(ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        while (resultSet.next()){
            ResultSetMetaData metaData = resultSet.getMetaData();
            result.add(new HashMap<>());

            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                result.get(result.size()-1).put(
                        metaData.getColumnName(i),
                        this.saveTypeParsing(resultSet, metaData, i)
                );
            }
        }
        return result;
    }

    private Object saveTypeParsing(ResultSet resultSet, ResultSetMetaData metaData, int index) throws SQLException {
        return switch (metaData.getColumnType(index)) {
            case Types.VARCHAR, Types.CHAR, Types.LONGVARCHAR -> resultSet.getString(index);
            case Types.INTEGER -> resultSet.getInt(index);
            case Types.DATE -> resultSet.getDate(index);
            default -> null;
        };
    }

    public List<Map<String, Object>> procedureMySql(Connection connectionInstance, String name) throws SQLException {
        PreparedStatement preparedStatement = connectionInstance.prepareStatement("{ call " + name + "(?) }");
        preparedStatement.setEscapeProcessing(true);
        // удалить и новой обработкой
        preparedStatement.setString(1, "TEST");
        // удалить и новой обработкой
        preparedStatement.executeQuery();
        ResultSet resultSet = preparedStatement.getResultSet();
        return parsingResultSet(resultSet);
    }

}
