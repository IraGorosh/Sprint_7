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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.praktikum.client.OrderClient;
import ru.yandex.praktikum.model.Order;
import ru.yandex.praktikum.model.OrderGenerator;
import ru.yandex.praktikum.model.OrderTrack;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreatingTest {

    private final String[] color;
    private OrderClient orderClient;
    private int orderId;

    public OrderCreatingTest(String[] color) {
        this.color = color;
    }

    @Parameterized.Parameters
    public static Object[][] getColor() {
        return new Object[][]{
                {new String[]{"BLACK"}},
                {new String[]{"GREY"}},
                {new String[]{"BLACK", "GREY"}},
                {null}
        };
    }

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
        orderClient = new OrderClient();
    }

    @After
    public void clearData() {
        OrderTrack orderTrack = new OrderTrack(orderId);
        orderClient.cancel(orderTrack);
    }

    @Test
    @DisplayName("Check success creation of an order with any scooter color")
    public void orderCanBeCreatedWithAnyColor() {
        Order order = OrderGenerator.getRandomRequiredFields(color);
        orderId = orderClient.create(order)
                .assertThat()
                .statusCode(SC_CREATED)
                .and()
                .assertThat()
                .body("track", notNullValue())
                .extract()
                .path("track");

        orderClient.getListByTrack(orderId)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .assertThat()
                .body("order", notNullValue());
    }
}
