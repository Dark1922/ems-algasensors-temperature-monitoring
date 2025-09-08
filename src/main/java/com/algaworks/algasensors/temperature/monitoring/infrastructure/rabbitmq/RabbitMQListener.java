package com.algaworks.algasensors.temperature.monitoring.infrastructure.rabbitmq;

import com.algaworks.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.algaworks.algasensors.temperature.monitoring.domain.service.TemperatureMonitoringSerivce;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

import static com.algaworks.algasensors.temperature.monitoring.infrastructure.rabbitmq.RabbitMQConfig.QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQListener {

    private final TemperatureMonitoringSerivce temperatureMonitoringSerivce;

    //recomendado usar uma queue para cada listener mais tem como passar mais de um
    //TemperatureLogData pdde ser tanto para entrada quanto para saida de dados
    @RabbitListener(queues = QUEUE)
    @SneakyThrows
    public void handle(@Payload TemperatureLogData temperatureLogData) {

        //@Headers Map<String, Object> headers para poder capturar os headers se precisar

        //LOGS PARA FINS DE TESTES DAS MENSAGENS
//       TSID sensorId = temperatureLogData.getSensorId();
//       Double temperature = temperatureLogData.getValue();
//         log.info("Temperature updated: SensorId {} Temp {} °C", sensorId, temperature);
//         log.info("Headers: {}", headers.toString());

        temperatureMonitoringSerivce.ProcessTemperatureReading(temperatureLogData);

         Thread.sleep(Duration.ofSeconds(5)); // para melhorar visualiazção das mensagem

    }
}
