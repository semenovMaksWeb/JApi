package com.example.JApi.model.ProceduresConfig;

import com.example.JApi.configuration.Connect;

import lombok.Data;
import javax.xml.bind.JAXBException;
import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProceduresConfig {
    private int id;
    private String call;
    private String idRight;
    private String connectString;
    private boolean isArray;
    private List<ProceduresParametersConfig> proceduresParametersConfigList = new ArrayList<>();
    private final Connect connect;

    public ProceduresConfig(
            Connect connect,
            int id,
            String connectString,
            String idRight,
            String call,
            boolean isArray
    ) throws SQLException, JAXBException, IOException {
        this.id = id;
        this.call = call;
        this.idRight = idRight;
        this.connect = connect;
        this.connectString = connectString;
        this.isArray = isArray;
        this.loaderProceduresParameters();
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
}
