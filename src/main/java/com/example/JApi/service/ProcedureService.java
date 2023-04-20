package com.example.JApi.service;

import com.example.JApi.configuration.Connect;

import com.example.JApi.configuration.ProcedureConfig;
import com.example.JApi.model.ProceduresConfig.ProceduresConfig;

import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;

@Service
public class ProcedureService {
    private final Connect connect;
    private final ProcedureConfig procedureConfig;

    public ProcedureService(Connect connect, ProcedureConfig procedureConfig){
        this.connect = connect;
        this.procedureConfig = procedureConfig;
    }

    public Object procedure(String name, Map<String, Object> body) throws Exception {
        Map<String, Connection> connectionMap = connect.getConnection();
        ProceduresConfig proceduresConfig = procedureConfig.getProcedure().get(name);
        Connection connectionInstance = connectionMap.get(proceduresConfig.getConnectString());

        if(connectionInstance == null){
            throw new Exception("Соединение с именем " + proceduresConfig.getConnectString() + " не найдено");
        }

        List<Map<String, Object>> procedureSqlResult = procedureSql(connectionInstance, procedureConfig.getProcedure().get(name), body);

        if(!proceduresConfig.isArray()){
            return  procedureSqlResult.get(0);
        }

        return procedureSqlResult;
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

    private Object saveTypeParsing(ResultSet resultSet, ResultSetMetaData metaData, int index) throws SQLException {
        return switch (metaData.getColumnType(index)) {
            case Types.VARCHAR, Types.CHAR, Types.LONGVARCHAR -> resultSet.getString(index);
            case Types.INTEGER, Types.BIGINT -> resultSet.getInt(index);
            case Types.STRUCT -> throw new Error("Структура не поддерживается");
            case Types.DATE -> resultSet.getDate(index);
            default -> throw new Error("Указанный тип данных не обрабатывается!");
        };
    }

    private void saveParameters(PreparedStatement preparedStatement, Map<String, Object> body, ProceduresConfig proceduresConfig){
        proceduresConfig.getProceduresParametersConfigList().forEach(proceduresParametersConfig -> {

             if(proceduresParametersConfig.getTypeParameters().equals("IN")){
                switch (proceduresParametersConfig.getType()){

                    case Types.VARCHAR, Types.CHAR, Types.LONGVARCHAR ->
                    {
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

                    default -> throw new Error("у указанного типа входяшего параметра нет обработки");
                }
             }
        });
    }

    public List<Map<String, Object>> procedureSql(
            Connection connectionInstance,
            ProceduresConfig proceduresConfig,
            Map<String, Object> body
    ) throws SQLException {
        PreparedStatement preparedStatement = connectionInstance.prepareStatement(proceduresConfig.getCall());
        preparedStatement.setEscapeProcessing(true);
        this.saveParameters(preparedStatement, body, proceduresConfig);
        preparedStatement.executeQuery();
        ResultSet resultSet = preparedStatement.getResultSet();
        return parsingResultSet(resultSet);
    }

}
