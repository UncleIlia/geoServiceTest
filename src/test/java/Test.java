import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.Mockito;
import ru.netology.entity.Country;
import ru.netology.entity.Location;
import ru.netology.geo.GeoService;
import ru.netology.geo.GeoServiceImpl;
import ru.netology.i18n.LocalizationService;
import ru.netology.i18n.LocalizationServiceImpl;
import ru.netology.sender.MessageSender;
import ru.netology.sender.MessageSenderImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

class geoServiceTest {

    GeoService geoService = new GeoServiceImpl();

    private static Stream<Arguments> argumentsForByIp() {
        return Stream.of(
                Arguments.of(null, null, null, 0, "127.0.0.1"),
                Arguments.of("Moscow", "RUSSIA", "Lenina", 15, "172.0.32.11"),
                Arguments.of("New York", "USA", " 10th Avenue", 32, "96.44.183.149"),
                Arguments.of("Moscow", "RUSSIA", null, 0, "172.0.00.00"),
                Arguments.of("New York", "USA", null, 0, "96.00.000.000")
        );
    }

    @ParameterizedTest
    @MethodSource("argumentsForByIp")
    public void byIp(String city, Country country, String street, int building, String ip) {

        Assertions.assertEquals(new Location(city, country, street, building), geoService.byIp(ip));
    }

    @Test
    public void byCoordinates() {

        final double latitude = 222.0;
        final double longitud = 333.0;

        Assertions.assertThrows(RuntimeException.class, () -> {
            geoService.byCoordinates(latitude, longitud);
        });
    }

    @ParameterizedTest
    @CsvSource({"'Welcome', 'USA'", "'Добро пожаловать', 'RUSSIA'"})
    public void LocalizationService(String expected, Country country) {

        LocalizationService localizationService = new LocalizationServiceImpl();

        Assertions.assertEquals(expected, localizationService.locale(country));
    }

    private static Stream<Arguments> argumentsForSend() {
        return Stream.of(
                Arguments.of("x-real-ip", "96.44.183.149", "New York", "USA", " 10th Avenue", 32, "Welcome"),
                Arguments.of("x-real-ip", "172.0.32.11", "Moscow", "RUSSIA", "Lenina", 15, "Добро пожаловать")
        );
    }

    @ParameterizedTest
    @MethodSource("argumentsForSend")
    public void send(String key, String ip, String city, Country country, String street, int building, String expected) {

        Map<String, String> headers = new HashMap<>();
        headers.put(key, ip);

        GeoService geoService = Mockito.mock(GeoService.class);
        Mockito.when(geoService.byIp(ip)).thenReturn(new Location("New York", Country.USA, " 10th Avenue", 32));

        LocalizationService localizationService = Mockito.mock(LocalizationService.class);
        Mockito.when(localizationService.locale(Country.USA)).thenReturn(expected);

        MessageSender messageSender = new MessageSenderImpl(geoService, localizationService);
        Assertions.assertEquals(expected, messageSender.send(headers));
    }


}

