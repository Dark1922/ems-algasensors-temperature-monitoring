package com.algaworks.algasensors.temperature.monitoring.api.config.web;

import io.hypersistence.tsid.TSID;
import org.springframework.core.convert.converter.Converter;

public class StringToTSIDWebConverter implements Converter <String, TSID> {

    @Override
    public TSID convert(String source) {
        return TSID.from(source);
    }

    @Override
    public <U> Converter<String, U> andThen(Converter<? super TSID, ? extends U> after) {
        return Converter.super.andThen(after);
    }
}
