package ru.yandex.practicum.state;

public enum QuantityState {
    ENDED,  // товар закончился
    FEW,    // осталось меньше 10 единиц товара
    ENOUGH, // осталось от 10 до 100 единиц
    MANY;   // осталось больше 100 единиц

    public static QuantityState fromInt(int index) {
        if (index < 0 || index >= values().length) {
            throw new IllegalArgumentException("Не существующий индекс: " + index);
        }
        return values()[index];
    }
}
