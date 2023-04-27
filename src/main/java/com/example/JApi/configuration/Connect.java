package com.example.JApi.configuration;

import com.example.JApi.model.connectionXml.ConnectsXml;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import java.io.FileReader;
import java.io.IOException;

import java.sql.DriverManager;
import java.sql.Connection;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;


@Configuration
public class Connect {
    static final String NAME_FILE_CONNECTION = "connection.xml";
    @Bean
    public Map<String, Connection> getConnection() throws IOException, JAXBException {
        Map<String, Connection> connectionMap = new HashMap<>();
        JAXBContext context = JAXBContext.newInstance(ConnectsXml.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String file = Objects.requireNonNull(getClass().getClassLoader().getResource(NAME_FILE_CONNECTION)).getFile();
        ConnectsXml connectsXml = (ConnectsXml) unmarshaller.unmarshal(new FileReader(file));
        connectsXml.getConnectXml().forEach(connectXml -> {
            String jdbcUrl = switch (connectXml.getType()) {
                case ("postgresql") -> "jdbc:postgresql://";
                case ("mysql") -> "jdbc:mysql://";
                default -> null;
            };
            String url = jdbcUrl + connectXml.getUrl();
            Properties props = new Properties();
            props.setProperty("user", connectXml.getUser());
            props.setProperty("password", connectXml.getPassword());
            try {
                Connection conn = DriverManager.getConnection(url, props);
                connectionMap.put(connectXml.getName(), conn);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return connectionMap;
    }
}
