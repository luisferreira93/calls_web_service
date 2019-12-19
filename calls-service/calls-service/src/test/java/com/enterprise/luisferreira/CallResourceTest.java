package com.enterprise.luisferreira;

import com.enterprise.luisferreira.database.Call;
import com.enterprise.luisferreira.dto.CallList;
import com.enterprise.luisferreira.utils.CallType;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class CallResourceTest {

    @Test
    public void createCallTest() {
        given().when()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(prepareCreateCall())
                .post("/create")
                .then()
                .statusCode(200);
    }

    @Test
    public void deleteCallTest() {
        given().when()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(65)
                .post("/delete")
                .then()
                .statusCode(404);
    }

    @Test
    public void retrieveCallsWithInvalidLimitTest() {
        given().when()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .queryParam("limit", -1)
                .get("/retrieve")
                .then()
                .statusCode(400);
    }

    @Test
    public void retrieveCallsTest() {
        given().when()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .queryParam("limit", 1)
                .queryParam("offset", 2)
                .queryParam("callType", CallType.INBOUND)
                .get("/retrieve")
                .then()
                .statusCode(200);
    }

    @Test
    public void retrieveCallsWithInvalidOffsetTest() {
        given().when()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .queryParam("offset", -1)
                .get("/retrieve")
                .then()
                .statusCode(400);
    }


    public CallList prepareCreateCall() {
        CallList callList = new CallList();
        Call call = new Call("922222160", "917441984", new Date(), new Date(), CallType.INBOUND);
        Call call2 = new Call("916821260", "919685412", new Date(), new Date(), CallType.INBOUND);
        List<Call> calls = new ArrayList<>();
        calls.add(call);
        calls.add(call2);
        callList.setCalls(calls);
        return callList;
    }


}
