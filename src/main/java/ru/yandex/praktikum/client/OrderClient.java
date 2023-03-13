package ru.yandex.praktikum.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.praktikum.client.base.ScooterRestClient;
import ru.yandex.praktikum.model.Order;
import ru.yandex.praktikum.model.OrderTrack;

import static io.restassured.RestAssured.given;

public class OrderClient extends ScooterRestClient {
    private static final String ORDERS_URI = BASE_URI + "orders/";

    @Step("Create order")
    public ValidatableResponse create(Order order) {
        return given()
                .spec(getBaseSpec())
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post(ORDERS_URI)
                .then();
    }

    @Step("Cancel order with {orderTrack}")
    public ValidatableResponse cancel(OrderTrack orderTrack) {
        return given()
                .spec(getBaseSpec())
                .header("Content-type", "application/json")
                .body(orderTrack)
                .when()
                .put(ORDERS_URI + "cancel/")
                .then();
    }

    @Step("Get list of all orders")
    public ValidatableResponse getList() {
        return given()
                .spec(getBaseSpec())
                .header("Content-type", "application/json")
                .when()
                .get(ORDERS_URI)
                .then();
    }

    @Step("Get order list by track {trackId}")
    public ValidatableResponse getListByTrack(int trackId) {
        return given()
                .spec(getBaseSpec())
                .header("Content-type", "application/json")
                .when()
                .get(ORDERS_URI + "track?t=" + trackId)
                .then();
    }
}
