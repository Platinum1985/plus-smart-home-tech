package ru.yandex.practicum.kafka.serializer;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public class GeneralAvroSerializer implements Serializer<GenericRecord> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // Здесь можно настроить параметры сериализации, если нужно
    }

    @Override
    public byte[] serialize(String topic, GenericRecord data) {
        if (data == null) {
            return null;
        }

        try {
            // 1. Получаем схему из GenericRecord
            Schema schema = data.getSchema();

            // 2. Создаём кодировщик для GenericRecord с учётом схемы
            DatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);

            // 3. Буфер для хранения результата сериализации
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // 4. Создаём бинарный кодировщик Avro
            Encoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);

            // 5. Сериализуем данные
            writer.write(data, encoder);
            encoder.flush(); // Гарантируем, что все данные записаны

            // 6. Возвращаем байтовый массив
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сериализации Avro-сообщения для топика " + topic, e);
        }
    }
}
