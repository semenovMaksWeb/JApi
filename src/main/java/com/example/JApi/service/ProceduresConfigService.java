package com.example.JApi.service;

import com.example.JApi.entity.ProceduresConfigEntity;
import com.example.JApi.repository.ProceduresConfigRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProceduresConfigService {

    private final ProceduresConfigRepository proceduresConfigRepository;

    public ProceduresConfigService(ProceduresConfigRepository proceduresConfigRepository) {
        this.proceduresConfigRepository = proceduresConfigRepository;
    }

    public List<ProceduresConfigEntity> getFindAll(){
        return proceduresConfigRepository.findAll();
    }
}
