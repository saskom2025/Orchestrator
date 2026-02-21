package com.intellifix.orchestrator.controller;

import com.intellifix.orchestrator.model.SimulatorConfigDTO;
import com.intellifix.orchestrator.model.UploadLogDTO;
import com.intellifix.orchestrator.service.UploadLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rest/upload/log")
@Tag(name = "Upload log summary", description = "Endpoints for managing Upload log files")
public class UploadLogController {

    @Autowired
    private UploadLogService uploadLogService;

    @GetMapping("/all")
    @Operation(summary = "Get all uploaded log files", description = "Retrieves all upload log file entries, ordered by creation date.", responses = {
            @ApiResponse(responseCode = "200", description = "List of uploaded log files retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UploadLogDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<List<UploadLogDTO>> getAllUploadLogs(){
        List<UploadLogDTO> allUploadLogs = uploadLogService.getAllUploadSummary();

        return ResponseEntity.ok(allUploadLogs);
    }
}
