package com.example.JApi.configuration;

import com.example.JApi.model.ProceduresConfig.ProceduresConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.bind.JAXBException;
import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ProcedureConfig {
    private final Connect connect;

    public ProcedureConfig(Connect connect) {
        this.connect = connect;
    }

    @Bean
    public Map<String, ProceduresConfig> getProcedure() throws JAXBException, IOException, SQLException {
        Map<String, ProceduresConfig> ProceduresConfigModelMap = new HashMap<>();
        Connection connection = this.connect.getConnection().get("main");
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, name, connect, id_right, call, result_array FROM config.procedures");
        preparedStatement.executeQuery();
        ResultSet resultSet = preparedStatement.getResultSet();

        while (resultSet.next()){
            ProceduresConfig proceduresConfigModel = new ProceduresConfig(
                    connect,
                    resultSet.getInt(1),
                    resultSet.getString(3),
                    resultSet.getInt(4),
                    resultSet.getString(5),
                    resultSet.getBoolean(6)
                    );
            ProceduresConfigModelMap.put(resultSet.getString(2), proceduresConfigModel);
        }

        return ProceduresConfigModelMap;
    }
}
