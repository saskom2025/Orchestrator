package com.intellifix.orchestrator.controller;

import com.intellifix.orchestrator.model.FixVersionDTO;
import com.intellifix.orchestrator.service.FixVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/rest/fix/version")
@Tag(name = "Fix Versions", description = "Endpoints for managing FIX protocol versions")
public class FixVersionController {

    private final FixVersionService fixVersionService;

    public FixVersionController(FixVersionService fixVersionService) {
        this.fixVersionService = fixVersionService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get fix version by ID", description = "Retrieves a FIX protocol version entry by its unique identifier.", responses = {
            @ApiResponse(responseCode = "200", description = "Fix version found", content = @Content(schema = @Schema(implementation = FixVersionDTO.class))),
            @ApiResponse(responseCode = "404", description = "Fix version not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<FixVersionDTO> getFixVersionById(
            @Parameter(description = "ID of the fix version to retrieve", required = true) @PathVariable Integer id) {
        FixVersionDTO fixVersion = fixVersionService.getFixVersionById(id);
        return ResponseEntity.ok(fixVersion);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all fix versions", description = "Retrieves all FIX protocol version entries, ordered by date added (newest first).", responses = {
            @ApiResponse(responseCode = "200", description = "List of fix versions retrieved successfully", content = @Content(array = @ArraySchema(schema = @Schema(implementation = FixVersionDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<List<FixVersionDTO>> getAllFixVersions() {
        List<FixVersionDTO> fixVersions = fixVersionService.getAllFixVersions();
        return ResponseEntity.ok(fixVersions);
    }
}
