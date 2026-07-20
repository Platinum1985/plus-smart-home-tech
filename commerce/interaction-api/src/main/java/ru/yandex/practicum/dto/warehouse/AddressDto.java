package ru.yandex.practicum.dto.warehouse;

import lombok.*;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;
import org.springframework.stereotype.Service;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {
    private String country;
    private String city;
    private String street;
    private String house;
    private String flat;

    @Override
    public String toString() {
        return country + ", " + city + ", " + street + ", " + house + ", " + flat;
    }

    public AddressDto(String address) {
        String[] parts = address.split(",\\s*");

        this.country = parts.length > 0 ? parts[0].trim() : null;
        this.city = parts.length > 1 ? parts[1].trim() : null;
        this.street = parts.length > 2 ? parts[2].trim() : null;
        this.house = parts.length > 3 ? parts[3].trim() : null;
        this.flat = parts.length > 4 ? parts[4].trim() : null;
    }
}
