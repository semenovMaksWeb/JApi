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

    /**
     * Функция старта вызова хранимой процедуры и вызова API
     * @param name - имя функции
     * @param body - параметры
     * @return Результат функции Map<String, Object> или List<Map<String, Object>>
     */
    public Object procedure(String name, Map<String, Object> body) throws Exception {
        Map<String, Connection> connectionMap = connect.getConnection(); //  список подключении
        ProceduresConfig proceduresConfig = procedureConfig.getProcedure().get(name); // получить функцию конфиг

        if(proceduresConfig == null){ // проверка наличия функции
            throw new Exception("Функция с именем " + name + " не найдена");
        }

        Connection connectionInstance = connectionMap.get(proceduresConfig.getConnectString()); // необходимое соединение

        if(connectionInstance == null){ // если соединения не существует
            throw new Exception("Соединение с именем " + proceduresConfig.getConnectString() + " не найдено");
        }

        // получение результата из функции
        List<Map<String, Object>> procedureSqlResult = procedureSql(connectionInstance, procedureConfig.getProcedure().get(name), body);

        if(!proceduresConfig.isArray()){ // функция возвращает не массив
            return  procedureSqlResult.get(0);
        }

        return procedureSqlResult;
    }

    /**
     * Функция парсит ResultSet в List<Map<String, Object>>
     * @param resultSet - результат функции бд
     * @return List<Map<String, Object>>
     */
    private List<Map<String, Object>> parsingResultSet(ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();

        while (resultSet.next()){ // данные от хранимой процедуры
            ResultSetMetaData metaData = resultSet.getMetaData(); // получить методанные
            Map<String, Object> map = new HashMap<>();

            for (int i = 1; i <= metaData.getColumnCount(); i++) { // прогнать методанные колонки
                map.put(
                        metaData.getColumnName(i),
                        this.saveTypeParsing(resultSet, metaData, i)
                );
            }

            result.add(map);
        }

        return result;
    }

    /**
     * Функция возвращает значение с ResultSet с необходимым типом
     * @param resultSet - ответ хранимой процедуры
     * @param metaData - методанные
     * @param index - index нужен для возвращение значения
     */
    private Object saveTypeParsing(ResultSet resultSet, ResultSetMetaData metaData, int index) throws SQLException {
        return switch (metaData.getColumnType(index)) {
            case Types.VARCHAR, Types.CHAR, Types.LONGVARCHAR -> resultSet.getString(index);
            case Types.INTEGER, Types.BIGINT -> resultSet.getInt(index);
            case Types.DATE -> resultSet.getDate(index);
            case Types.STRUCT -> throw new Error("Структура не поддерживается");
            default -> throw new Error("Указанный тип данных не обрабатывается!");
        };
    }

    /**
     *  Функция меняет ? в хранимой процедуре на соотвествующие значение по конфигу proceduresConfig и параметрам body
     * @param preparedStatement - объект вызова храминой процедуры
     * @param body - параметры хранимой процедуры от API
     * @param proceduresConfig - конфиг хранимой процедуры
     */
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

    /**
     * Вызов хранимой процедуры после валидации
     * @param connectionInstance - соединение к бд
     * @param proceduresConfig - конфиг храминой процедур
     * @param body - параметры храминой процедур от API
     * @return List<Map<String, Object>>
     */
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
