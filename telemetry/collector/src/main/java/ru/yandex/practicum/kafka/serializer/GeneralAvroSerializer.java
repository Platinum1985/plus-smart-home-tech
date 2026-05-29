package ru.yandex.practicum.kafka.serializer;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import java.io.ByteArrayOutputStream;
import java.util.Map;

public class GeneralAvroSerializer implements Serializer<GenericRecord> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // Настройки не требуются
    }

    @Override
    public byte[] serialize(String topic, GenericRecord data) {
        if (data == null) {
            return null;
        }

        try {
            // Шаг 1. Получаем схему из записи
            Schema schema = data.getSchema();

            // Шаг 2. Сериализуем схему в байты
            ByteArrayOutputStream schemaStream = new ByteArrayOutputStream();
            Encoder schemaEncoder = EncoderFactory.get().binaryEncoder(schemaStream, null);

            // Исправленная строка: создаём простую схему для сериализации Schema объекта
            Schema stringSchema = Schema.create(Schema.Type.STRING);
            DatumWriter<Schema> schemaWriter = new GenericDatumWriter<>(stringSchema);
            schemaWriter.write(schema, schemaEncoder);  // Сериализуем схему
            schemaEncoder.flush();  // Гарантируем запись всех данных
            byte[] schemaBytes = schemaStream.toByteArray();

            // Шаг 3. Сериализуем данные (без схемы)
            ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            Encoder dataEncoder = EncoderFactory.get().binaryEncoder(dataStream, null);

            org.apache.avro.specific.SpecificDatumWriter<GenericRecord> writer =
                    new org.apache.avro.specific.SpecificDatumWriter<>(schema);
            writer.write(data, dataEncoder);
            dataEncoder.flush();
            byte[] dataBytes = dataStream.toByteArray();

            // Шаг 4. Объединяем: схема + данные
            int totalLength = schemaBytes.length + dataBytes.length;
            byte[] result = new byte[totalLength];

            System.arraycopy(schemaBytes, 0, result, 0, schemaBytes.length);
            System.arraycopy(dataBytes, 0, result, schemaBytes.length, dataBytes.length);

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сериализации Avro-сообщения для топика " + topic, e);
        }
    }
}