package com.intellifix.orchestrator.controller;

import com.intellifix.orchestrator.model.SimulatorConfigDTO;
import com.intellifix.orchestrator.service.SimulatorConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/rest/simulator/config")
@Tag(name = "Simulator Configs", description = "Endpoints for managing Simulator configs")
public class SimulatorConfigController {

    private final SimulatorConfigService simulatorConfigService;

    SimulatorConfigController(SimulatorConfigService simulatorConfigService) {
        this.simulatorConfigService = simulatorConfigService;
    }

    @PostMapping
    @Operation(summary = "Create a new simulator config", description = "Saves a new simulator configuration entry to the database.", responses = {
            @ApiResponse(responseCode = "201", description = "Simulator config created successfully", content = @Content(schema = @Schema(implementation = SimulatorConfigDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<SimulatorConfigDTO> createSimulatorConfig(
            @Valid @RequestBody SimulatorConfigDTO simulatorConfigDTO) {
        SimulatorConfigDTO created = simulatorConfigService.createSimulatorConfig(simulatorConfigDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get simulator config by ID", description = "Retrieves a simulator configuration entry by its unique identifier.", responses = {
            @ApiResponse(responseCode = "200", description = "Simulator config found", content = @Content(schema = @Schema(implementation = SimulatorConfigDTO.class))),
            @ApiResponse(responseCode = "404", description = "Simulator config not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<SimulatorConfigDTO> getSimulatorConfigById(
            @Parameter(description = "ID of the simulator config to retrieve", required = true) @PathVariable Integer id) {
        SimulatorConfigDTO config = simulatorConfigService.getSimulatorConfigById(id);
        return ResponseEntity.ok(config);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get simulator configs by type", description = "Retrieves simulator configuration entries filtered by simulator config type, ordered by creation date (newest first).", responses = {
            @ApiResponse(responseCode = "200", description = "List of simulator configs retrieved successfully", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SimulatorConfigDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<List<SimulatorConfigDTO>> getSimulatorConfigsByType(
            @Parameter(description = "Type of the simulator config to filter by", required = true) @PathVariable String type) {
        List<SimulatorConfigDTO> configs = simulatorConfigService.getSimulatorConfigsByType(type);
        return ResponseEntity.ok(configs);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all simulator configs", description = "Retrieves all simulator configuration entries, ordered by creation date (newest first).", responses = {
            @ApiResponse(responseCode = "200", description = "List of simulator configs retrieved successfully", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SimulatorConfigDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<List<SimulatorConfigDTO>> getAllSimulatorConfigs() {
        List<SimulatorConfigDTO> configs = simulatorConfigService.getAllSimulatorConfigs();
        return ResponseEntity.ok(configs);
    }
}
