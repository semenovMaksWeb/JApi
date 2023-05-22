package com.example.JApi.repository;

import com.example.JApi.entity.ProceduresConfigEntity;
import org.springframework.data.repository.*;

import java.util.List;

public interface ProceduresConfigRepository extends Repository<ProceduresConfigEntity, Long> {

    List<ProceduresConfigEntity> findAll();


}