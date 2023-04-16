package com.example.JApi.service;

import com.example.JApi.configuration.Connect;
import com.example.JApi.utils.ProceduresConfig.ProceduresConfig;
import org.postgresql.util.PGobject;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;

@Service
public class ProcedureService {
    private final Connect connect;

    public ProcedureService(Connect connect){
        this.connect = connect;
    }

    public List<Map<String, Object>> procedure(String connection, String name, Map<String, Object> body) throws Exception {
        Map<String, Connection> connectionMap = connect.getConnection();
        Connection connectionInstance = connectionMap.get(connection);

        if(connectionInstance == null){
            throw new Exception("Соединение с именем " + connection + " не найдено");
        }

        ProceduresConfig proceduresConfig = new ProceduresConfig(this.connect);
        proceduresConfig.loaderProceduresConfig(name, connection);

        return procedureSql(connectionInstance, name, proceduresConfig, body);
    }

    private List<Map<String, Object>> parsingResultSet(ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();

        while (resultSet.next()){
            ResultSetMetaData metaData = resultSet.getMetaData();
            Map<String, Object> map = new HashMap<>();

            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                map.put(
                        metaData.getColumnName(i),
                        this.saveTypeParsing(resultSet, metaData, i)
                );
            }

            result.add(map);
        }
        return result;
    }


//    private Object ParsingStruct(ResultSet resultSet, ResultSetMetaData metaData, int index) throws SQLException {
//        Map<String, Object> res = new HashMap<>();
//        return null;
//    }

    private Object saveTypeParsing(ResultSet resultSet, ResultSetMetaData metaData, int index) throws SQLException {
        System.out.println(metaData.getColumnType(index));
        return switch (metaData.getColumnType(index)) {
            case Types.VARCHAR, Types.CHAR, Types.LONGVARCHAR -> resultSet.getString(index);
            case Types.INTEGER, Types.BIGINT -> resultSet.getInt(index);
//            case Types.STRUCT -> ParsingStruct(resultSet, metaData, index);
            case Types.DATE -> resultSet.getDate(index);
            default -> null;
        };
    }

    private void saveParameters(PreparedStatement preparedStatement, Map<String, Object> body, ProceduresConfig proceduresConfig){
        proceduresConfig.getProceduresParametersConfigList().forEach(proceduresParametersConfig -> {
             if(proceduresParametersConfig.getTypeParameters().equals("IN")){
                switch (proceduresParametersConfig.getType()){
                    case Types.VARCHAR, Types.CHAR, Types.LONGVARCHAR -> {
                        try {
                            preparedStatement.setString(proceduresParametersConfig.getIndex(), (String) body.get(proceduresParametersConfig.getName()));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    case Types.INTEGER -> {
                        try {
                            preparedStatement.setInt(proceduresParametersConfig.getIndex(), (Integer) body.get(proceduresParametersConfig.getName()));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
             }
        });
    }

    public List<Map<String, Object>> procedureSql(Connection connectionInstance, String name, ProceduresConfig proceduresConfig, Map<String, Object> body) throws SQLException {
        PreparedStatement preparedStatement = connectionInstance.prepareStatement(proceduresConfig.getCall());
        preparedStatement.setEscapeProcessing(true);
        this.saveParameters(preparedStatement, body, proceduresConfig);
        preparedStatement.executeQuery();
        ResultSet resultSet = preparedStatement.getResultSet();
        return parsingResultSet(resultSet);
    }

}
