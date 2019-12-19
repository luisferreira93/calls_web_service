package com.enterprise.luisferreira;

import static io.restassured.RestAssured.given;

import com.enterprise.luisferreira.database.Call;
import com.enterprise.luisferreira.dto.CallList;
import com.enterprise.luisferreira.utils.CallType;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;

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
  public void retrieveCallsWithInvalidParametersTest() {
    given().when()
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .queryParam("limit", -1)
        .queryParam("offset", -2)
        .queryParam("callType", CallType.INBOUND)
        .get("/retrieve")
        .then()
        .statusCode(400);
  }

  @Test
  public void retrieveCallsTest() {
    given().when()
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .queryParam("limit", 0)
        .queryParam("offset", 0)
        .queryParam("callType", CallType.INBOUND)
        .get("/retrieve")
        .then()
        .statusCode(200);
  }

  private CallList prepareCreateCall() {
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
