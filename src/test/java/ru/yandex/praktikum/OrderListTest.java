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
import ru.yandex.praktikum.client.OrderClient;
import ru.yandex.praktikum.model.Order;
import ru.yandex.praktikum.model.OrderGenerator;
import ru.yandex.praktikum.model.OrderTrack;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrderListTest {
    private OrderClient orderClient;
    private int orderId;

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
    @DisplayName("Check getting of all orders")
    public void getOrdersReturnsAllOrders() {
        Order order = OrderGenerator.getRandomRequiredFields(null);
        orderId = orderClient.create(order)
                .assertThat()
                .extract()
                .path("track");

        orderClient
                .getList()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .assertThat()
                .body("orders", notNullValue());
    }
}
