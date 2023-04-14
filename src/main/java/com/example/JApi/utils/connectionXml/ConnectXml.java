package com.example.JApi.utils.connectionXml;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ConnectXml {
    @XmlElement(name = "type")
    private String type;

    @XmlElement(name = "url")
    private String url;

    @XmlElement(name = "user")
    private String user;

    @XmlElement(name = "password")
    private String password;

    @XmlElement(name = "name")
    private String name;
}
