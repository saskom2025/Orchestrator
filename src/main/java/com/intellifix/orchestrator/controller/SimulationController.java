package com.intellifix.orchestrator.controller;

import com.intellifix.orchestrator.mapper.SimulationMapper;
import com.intellifix.orchestrator.model.SimulationDTO;
import com.intellifix.orchestrator.model.SimulationRequestDTO;
import com.intellifix.orchestrator.model.SimulationStatusDTO;
import com.intellifix.orchestrator.service.SimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/rest/simulation")
@Tag(name = "Simulation", description = "Endpoints for managing simulations")
public class SimulationController {

    private final SimulationService service;
    private final SimulationMapper mapper;

    @Autowired
    public SimulationController(SimulationService service, SimulationMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Create a new simulation entry", description = "Saves a new simulation entry to the database.", responses = {
            @ApiResponse(responseCode = "201", description = "Simulation created successfully", content = @Content(schema = @Schema(implementation = SimulationRequestDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SimulationRequestDTO> createSimulation(
            @jakarta.validation.Valid @RequestBody SimulationRequestDTO simulationDTO,
            jakarta.servlet.http.HttpServletRequest request) {

        SimulationRequestDTO data = service.createSimulation(simulationDTO);
        return new ResponseEntity<>(data, HttpStatus.CREATED);
    }

    @PatchMapping("/status")
    @Operation(summary = "Update simulation status", description = "Publishes a status update for a simulation to the status stream.", responses = {
            @ApiResponse(responseCode = "202", description = "Status update accepted"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> updateStatus(@jakarta.validation.Valid @RequestBody SimulationStatusDTO statusUpdate) {
        service.updateStatus(statusUpdate);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<SimulationRequestDTO>> getAllSimulations() {
        List<SimulationRequestDTO> allSimulations = service.getAllSimulations();
        return ResponseEntity.ok(allSimulations);
    }
}
