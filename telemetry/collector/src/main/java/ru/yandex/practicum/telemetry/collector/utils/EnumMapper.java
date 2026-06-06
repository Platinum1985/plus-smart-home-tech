package ru.yandex.practicum.telemetry.collector.utils;

public class EnumMapper {

    public static <T extends Enum<T>> T map(Enum<?> source, Class<T> targetType) {
        if (source == null) {
            return null;
        }

        try {
            // Ищем константу в целевом enum с тем же именем
            return Enum.valueOf(targetType, source.name());
        } catch (IllegalArgumentException e) {
            System.err.println("Enum mapping failed: " + source.name() + " -> " + targetType.getSimpleName());
            return null; // Или можно вернуть значение по умолчанию
        }
    }
}
