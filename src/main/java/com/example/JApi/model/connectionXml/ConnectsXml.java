package com.example.JApi.model.connectionXml;

import javax.xml.bind.annotation.*;
import java.util.List;
import lombok.Data;

@XmlRootElement(name="connections")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class ConnectsXml {
    @XmlElement(name="connect")
    private List<ConnectXml> connectXml;
}
