package com.example.JApi.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="procedures", schema = "config")
@Data
public class ProceduresConfigEntity implements Serializable {
    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String connect;

    @Column()
    private String id_right;

    @Column(name = "result_array")
    private Boolean resultArray = true;

    @Column(nullable = true)
    private String call;

    protected ProceduresConfigEntity() {

    }

    public ProceduresConfigEntity(
            int id,
            String name,
            String connect,
            String id_right,
            Boolean resultArray,
            String  call) {
               this.id = id;
               this.name = name;
               this.connect = connect;
               this.id_right = id_right;
               this.resultArray = resultArray;
               this.call = call;
    }
}
