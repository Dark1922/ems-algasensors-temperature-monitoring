package com.algaworks.algasensors.temperature.monitoring.infrastructure.rabbitmq;

import com.algaworks.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.algaworks.algasensors.temperature.monitoring.domain.service.SensorAlertService;
import com.algaworks.algasensors.temperature.monitoring.domain.service.TemperatureMonitoringSerivce;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static com.algaworks.algasensors.temperature.monitoring.infrastructure.rabbitmq.RabbitMQConfig.QUEUE_PROCESS_TEMPERATURE;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQListener {

    //aplicação pode publicar em quantas filas quiser
    //pode ter mais de um listener para a mesma fila
    //cada listener pode ter sua propria regra de negocio
    //cada listener pode ter sua propria concorrencia
    //cada listener pode estar em uma aplicação diferente
    //cada listener pode estar em uma aplicação com tecnologia diferente
    //cada listener pode estar em uma aplicação com linguagem diferente
    //cada listener pode estar em uma aplicação com banco de dados diferente

    private final TemperatureMonitoringSerivce temperatureMonitoringSerivce;
    private final SensorAlertService sensorAlertService;

    //recomendado usar uma queue para cada listener mais tem como passar mais de um
    //TemperatureLogData pdde ser tanto para entrada quanto para saida de dados
    @RabbitListener(queues = QUEUE_PROCESS_TEMPERATURE, concurrency = "2-3") //pode configurar a concorrencia para ter mais de uma thread processando as mensagens
    @SneakyThrows
    public void handleProcessTemperature(@Payload TemperatureLogData temperatureLogData) {
        //@Headers Map<String, Object> headers para poder capturar os headers se precisar

        //LOGS PARA FINS DE TESTES DAS MENSAGENS
//       TSID sensorId = temperatureLogData.getSensorId();
//       Double temperature = temperatureLogData.getValue();
//         log.info("Temperature updated: SensorId {} Temp {} °C", sensorId, temperature);
//         log.info("Headers: {}", headers.toString());
        temperatureMonitoringSerivce.ProcessTemperatureReading(temperatureLogData);
//         Thread.sleep(Duration.ofSeconds(5)); // para melhorar visualiazção das mensagem

    }

    @RabbitListener(queues = QUEUE_PROCESS_TEMPERATURE, concurrency = "2-3")
    @SneakyThrows
    public void handleAlerting(@Payload TemperatureLogData temperatureLogData) {

        sensorAlertService.handleAlerting(temperatureLogData);
       // log.info("ALERTING - SensorId: {} Temp: {}", temperatureLogData.getSensorId(), temperatureLogData.getValue());
        Thread.sleep(Duration.ofSeconds(5));

    }
}
