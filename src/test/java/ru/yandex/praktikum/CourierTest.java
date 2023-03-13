package ru.yandex.praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
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

public class CourierTest {

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
    @DisplayName("Check the creation of a courier with valid data")
    public void courierCanBeCreatedWithValidData() {
        Courier courier = CourierGenerator.getRandom();

        assertCreateReturnsResponse(courier, SC_CREATED).body("ok", is(true));

        assertLoginReturnsId(courier);
    }

    @Test
    @DisplayName("Check the creation of a courier without first name")
    public void courierCanBeCreatedWithoutFirstName() {
        Courier courier = new Courier(CourierGenerator.getRandomString(), CourierGenerator.getRandomString(), null);

        assertCreateReturnsResponse(courier, SC_CREATED).body("ok", is(true));

        assertLoginReturnsId(courier);
    }

    @Test
    @DisplayName("Check that it is impossible to create two identical couriers")
    public void twoIdenticalCouriersCanNotBeCreated() {
        Courier courier = CourierGenerator.getRandom();

        assertCreateReturnsResponse(courier, SC_CREATED)
                .body("ok", is(true));

        assertCreateReturnsResponse(courier, SC_CONFLICT)
                .body("message", is("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Check that it is impossible to create courier with existing login")
    public void courierWithExistingLoginCanNotBeCreated() {
        Courier courier = CourierGenerator.getRandom();

        assertCreateReturnsResponse(courier, SC_CREATED)
                .body("ok", is(true));

        Courier courierWithExistingLogin = new Courier(
                courier.getLogin(),
                CourierGenerator.getRandomString(),
                CourierGenerator.getRandomString()
        );

        assertCreateReturnsResponse(courierWithExistingLogin, SC_CONFLICT)
                .body("message", is("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Check that it is impossible to create without login")
    public void courierCanNotBeCreatedWithoutLogin() {
        Courier courier = new Courier(null, CourierGenerator.getRandomString(), CourierGenerator.getRandomString());
        assertCreateReturnsResponse(courier, SC_BAD_REQUEST)
                .body("message", is("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Check that it is impossible to create without password")
    public void courierCanNotBeCreatedWithoutPassword() {
        Courier courier = new Courier(CourierGenerator.getRandomString(), null, CourierGenerator.getRandomString());
        assertCreateReturnsResponse(courier, SC_BAD_REQUEST)
                .body("message", is("Недостаточно данных для создания учетной записи"));
    }

    private ValidatableResponse assertCreateReturnsResponse(Courier courier, int httpStatus) {
        return courierClient
                .create(courier)
                .assertThat()
                .statusCode(httpStatus)
                .and()
                .assertThat();
    }

    private void assertLoginReturnsId(Courier courier) {
        courierId = courierClient
                .login(CourierCredentials.from(courier))
                .assertThat()
                .body("id", notNullValue())
                .extract().path("id");
    }
}

