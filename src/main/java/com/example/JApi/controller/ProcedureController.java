package com.example.JApi.controller;

import com.example.JApi.service.ProcedureService;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("procedure")
public class ProcedureController {
    private final ProcedureService procedureService;

    public ProcedureController(ProcedureService procedureService){
        this.procedureService = procedureService;
    }

    @RequestMapping(
            method = RequestMethod.POST
    )
    public Object procedure(
            @ApiParam(required = true, value = "name хранимой процедуры")
            @RequestParam String name,

            @RequestBody Map<String, Object> body
    ) throws Exception {
        return procedureService.procedure(name, body);
    }
}
