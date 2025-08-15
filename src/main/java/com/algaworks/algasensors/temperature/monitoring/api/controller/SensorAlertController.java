package com.algaworks.algasensors.temperature.monitoring.api.controller;

import com.algaworks.algasensors.temperature.monitoring.api.model.SensorAlertInput;
import com.algaworks.algasensors.temperature.monitoring.api.model.SensorAlertOutput;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorAlert;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorId;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.SensorAlertRepository;
import io.hypersistence.tsid.TSID;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/sensors/{sensorId}/alert")
@RequiredArgsConstructor
public class SensorAlertController {

    private final SensorAlertRepository sensorAlertRepository;

    @GetMapping
    public SensorAlertOutput getSensorAlert(@PathVariable @NotNull TSID sensorId) {
        SensorAlert sensorAlert = findByIdOrFail(sensorId);
        return convertModelToOutput(sensorAlert);
    }

    @PutMapping
    public SensorAlertOutput updateSensorAlert(@PathVariable @NotNull TSID sensorId, @RequestBody SensorAlertInput sensorAlertInput) {
        SensorAlert sensorAlert = findByIdOrDefault(sensorId, sensorAlertInput);
        BeanUtils.copyProperties(sensorAlertInput, sensorAlert);
        sensorAlertRepository.saveAndFlush(sensorAlert);
        return convertModelToOutput(sensorAlert);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSensorAlert(@PathVariable @NotNull TSID sensorId) {
        SensorAlert sensorAlert = findByIdOrFail(sensorId);
        sensorAlertRepository.delete(sensorAlert);
    }

    public SensorAlert findByIdOrFail(TSID sensorId) {
        return sensorAlertRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public SensorAlertOutput convertModelToOutput(SensorAlert sensorAlert) {
        return SensorAlertOutput.builder()
                .id(sensorAlert.getId().getValue())
                .minTemperature(sensorAlert.getMinTemperature())
                .maxTemperature(sensorAlert.getMaxTemperature())
                .build();
    }

    private SensorAlert findByIdOrDefault(TSID sensorId, SensorAlertInput sensorAlertInput) {
        return sensorAlertRepository.findById(new SensorId(sensorId))
                .orElse(SensorAlert.builder()
                        .id(new SensorId(sensorId))
                        .maxTemperature(sensorAlertInput.getMaxTemperature())
                        .minTemperature(sensorAlertInput.getMinTemperature())
                        .build());
    }
}
