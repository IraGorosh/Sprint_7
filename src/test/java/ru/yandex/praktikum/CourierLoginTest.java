package ru.yandex.praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.yandex.praktikum.client.CourierClient;
import ru.yandex.praktikum.model.Courier;
import ru.yandex.praktikum.model.CourierCredentials;
import ru.yandex.praktikum.model.CourierGenerator;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CourierLoginTest {
    private CourierClient courierClient;
    private int courierId;

    @BeforeClass
    public static void globalSetUp() {
        RestAssured.filters(
                new RequestLoggingFilter(),
                new ResponseLoggingFilter(),
                new AllureRestAssured()
        );
    }

    @Before
    public void setUp() {
        courierClient = new CourierClient();
    }

    @After
    public void clearData() {
        courierClient.delete(courierId);
    }

    @Test
    @DisplayName("Check success login with valid credentials")
    public void courierCanLogInWithValidCredentials() {
        Courier courier = getCreatedCourier();

        assertLoginReturnsId(courier);
    }

    @Test
    @DisplayName("Check login with invalid login")
    public void courierCanNotLogInWithInvalidLogin() {
        Courier courier = getCreatedCourier();
        CourierCredentials credentials = new CourierCredentials(CourierGenerator.getRandomString(), courier.getPassword());

        assertLoginReturnsError(credentials, SC_NOT_FOUND, "Учетная запись не найдена");

        assertLoginReturnsId(courier);
    }

    @Test
    @DisplayName("Check login with invalid password")
    public void courierCanNotLogInWithInvalidPassword() {
        Courier courier = getCreatedCourier();
        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), CourierGenerator.getRandomString());

        assertLoginReturnsError(credentials, SC_NOT_FOUND, "Учетная запись не найдена");

        assertLoginReturnsId(courier);
    }

    @Test
    @DisplayName("Check login without login")
    public void courierCanNotLogInWithoutLogin() {
        Courier courier = getCreatedCourier();
        CourierCredentials credentials = new CourierCredentials(null, courier.getPassword());

        assertLoginReturnsError(credentials, SC_BAD_REQUEST, "Недостаточно данных для входа");
        assertLoginReturnsId(courier);
    }

    @Test
    @DisplayName("Check login without password")
    public void courierCanNotLogInWithoutPassword() {
        Courier courier = getCreatedCourier();
        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), null);

        assertLoginReturnsError(credentials, SC_BAD_REQUEST, "Недостаточно данных для входа");
        assertLoginReturnsId(courier);
    }

    @Test
    @DisplayName("Check login with nonexistent credentials ")
    public void courierCanNotLogInWithNonexistentCredentials() {
        CourierCredentials credentials = new CourierCredentials(CourierGenerator.getRandomString(), CourierGenerator.getRandomString());

        assertLoginReturnsError(credentials, SC_NOT_FOUND, "Учетная запись не найдена");
    }

    private void assertLoginReturnsError(CourierCredentials credentials, int httpStatus, String message) {
        courierClient
                .login(credentials)
                .assertThat()
                .statusCode(httpStatus)
                .and()
                .assertThat()
                .body("message", is(message));
    }

    private void assertLoginReturnsId(Courier courier) {
        courierId = courierClient
                .login(CourierCredentials.from(courier))
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .assertThat()
                .body("id", notNullValue())
                .extract()
                .path("id");
    }

    private Courier getCreatedCourier() {
        Courier courier = CourierGenerator.getRandom();
        courierClient
                .create(courier)
                .assertThat()
                .body("ok", is(true));
        return courier;
    }
}
