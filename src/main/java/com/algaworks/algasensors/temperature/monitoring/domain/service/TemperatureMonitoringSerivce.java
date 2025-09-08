package com.algaworks.algasensors.temperature.monitoring.domain.service;

import com.algaworks.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorId;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorMonitoring;
import com.algaworks.algasensors.temperature.monitoring.domain.model.TemperatureLog;
import com.algaworks.algasensors.temperature.monitoring.domain.model.TemperatureLogId;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.SensorMonitoringRepository;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.TemperatureLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemperatureMonitoringSerivce {

    private final SensorMonitoringRepository sensorMonitoringRepository;
    private  final TemperatureLogRepository temperatureLogRepository;

    @Transactional
    public void ProcessTemperatureReading(TemperatureLogData temperatureLogData) {
        sensorMonitoringRepository.findById(new SensorId(temperatureLogData.getSensorId()))
                .ifPresentOrElse(sensor -> handleSensorMonitoring(temperatureLogData, sensor),
                        () -> logIgnoredTemprature(temperatureLogData));
    }

    private void logIgnoredTemprature(TemperatureLogData temperatureLogData) {
        log.info("Temperature Ignored: SensorId {} Temp {} °C", temperatureLogData.getSensorId(),
                temperatureLogData.getValue());
    }

    private void handleSensorMonitoring(TemperatureLogData temperatureLogData,  SensorMonitoring sensor) {
        if (sensor.isEnable()) {
            sensor.setLastTemperature(temperatureLogData.getValue());
            sensor.setUpdatedAt(OffsetDateTime.now());
            sensorMonitoringRepository.save(sensor);

            TemperatureLog temperatureLog = TemperatureLog.builder()
                    .id(new TemperatureLogId(temperatureLogData.getId()))
                    .registeredAt(temperatureLogData.getRegisteredAt())
                    .value(temperatureLogData.getValue())
                    .sensorID(new SensorId(temperatureLogData.getSensorId()))
                    .build();

            temperatureLogRepository.save(temperatureLog);

            log.info("Temperature updated: SensorId {} Temp {} °C", temperatureLogData.getSensorId(),
                    temperatureLogData.getValue());
        } else {
            logIgnoredTemprature(temperatureLogData);
        }
    }
}
