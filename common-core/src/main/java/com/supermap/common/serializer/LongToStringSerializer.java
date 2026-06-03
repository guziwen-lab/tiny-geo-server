package com.supermap.common.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * 解决js的long类型失精问题
 *
 * @author gzw
 */
@JsonComponent
public class LongToStringSerializer extends StdSerializer<Long> {

    public static final int MIN_CONVERTER_LENGTH = 14;

    public LongToStringSerializer() {
        super(Long.class);
    }

    @Override
    public void serialize(Long longValue, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        int length = longValue.toString().length();
        if (length > MIN_CONVERTER_LENGTH) {
            jsonGenerator.writeString(longValue.toString());
        } else {
            jsonGenerator.writeNumber(longValue);
        }
    }

}