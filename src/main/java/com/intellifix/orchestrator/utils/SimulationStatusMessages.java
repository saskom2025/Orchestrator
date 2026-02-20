package com.intellifix.orchestrator.utils;

public class SimulationStatusMessages {

    public static String getMessage(String status) {
        return switch (status) {
            case String s when s.equals(SimulationStatus.IN_PROGRESS.name()) ->
                "Simulation has been initiated and execution is in progress";
            case String s when s.equals(SimulationStatus.COMPLETED.name()) ->
                "Simulation has been completed successfully";
            case String s when s.equals(SimulationStatus.FAILED.name()) ->
                "Simulation failed due to some system errors";
            case String s when s.equals(SimulationStatus.CANCELLED.name()) ->
                "Simulation has been cancelled through external request";
            default -> "Unknown status";
        };
    }
}
