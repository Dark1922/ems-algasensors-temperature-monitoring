package com.algaworks.algasensors.temperature.monitoring.domain.service;

import com.algaworks.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorId;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.SensorAlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorAlertService {

    private  final SensorAlertRepository sensorAlertRepository;

    @Transactional //vai trabalhar com a consulta dos dados
    public void handleAlerting(TemperatureLogData temperatureLogData) {
        sensorAlertRepository.findById(new SensorId(temperatureLogData.getSensorId()))
          .ifPresentOrElse(alert -> {
            if (alert.getMaxTemperature() != null && temperatureLogData.getValue().
                    compareTo(alert.getMaxTemperature()) >= 0) {
                log.info("Alert Max Temp: {}", temperatureLogData.getValue());
            } else if (alert.getMinTemperature() != null && temperatureLogData.getValue()
                    .compareTo(alert.getMinTemperature()) <= 0) {
                log.info("Alert Min Temp: {}", temperatureLogData.getValue());
            } else {
                    logIgnoredAlert(temperatureLogData);
            }
        },
        () -> logIgnoredAlert(temperatureLogData));
    }

    public static void logIgnoredAlert(TemperatureLogData temperatureLogData) {
        log.info("Alert ignored SensorId: {} Temp: {}", temperatureLogData.getSensorId(),
                temperatureLogData.getValue());
    }
}
