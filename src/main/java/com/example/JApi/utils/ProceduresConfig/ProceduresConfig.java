package com.example.JApi.utils.ProceduresConfig;

import com.example.JApi.configuration.Connect;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProceduresConfig {
    private int id;
    private String call;
    private List<ProceduresParametersConfig> proceduresParametersConfigList = new ArrayList<>();

    private final Connect connect;
    public ProceduresConfig(Connect connect){
        this.connect = connect;
    }

    private void loaderProcedures(String name, String connect) throws JAXBException, IOException, SQLException {
        Connection connection = this.connect.getConnection().get("main");
        PreparedStatement preparedStatement = connection.prepareStatement("select p.id, p.call  from config.procedures p where name = ? and connect = ? limit 1;");
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, connect);
        preparedStatement.executeQuery();
        ResultSet resultSet = preparedStatement.getResultSet();

        while (resultSet.next()){
            if(resultSet.getString(1) == null ){
                System.out.println("Хранимая процедура не найдена!");
            }
            this.id = resultSet.getInt(1);
            this.call = resultSet.getString(2);
        }
    }

    private void loaderProceduresParameters() throws JAXBException, IOException, SQLException {
        Connection connection = this.connect.getConnection().get("main");
        PreparedStatement preparedStatement =
                connection.prepareStatement("select pp.name, pp.type, pp.type_parameters, pp.index from config.procedures_parameters pp where pp.id_procedures = ?");
        preparedStatement.setInt(1, this.id);
        preparedStatement.executeQuery();
        ResultSet resultSet = preparedStatement.getResultSet();
        ProceduresParametersConfig proceduresParametersConfig = new ProceduresParametersConfig();
        while (resultSet.next()){
            proceduresParametersConfig.setName(resultSet.getString(1));
            proceduresParametersConfig.setType(resultSet.getInt(2));
            proceduresParametersConfig.setTypeParameters(resultSet.getString(3));
            proceduresParametersConfig.setIndex(resultSet.getInt(4));
            this.proceduresParametersConfigList.add(proceduresParametersConfig);
        }
    }

    public void loaderProceduresConfig(String name, String connect) throws JAXBException, IOException, SQLException {
         this.loaderProcedures(name, connect);
         this.loaderProceduresParameters();
    }
}
