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
class CallResourceTest {

  @Test
  private void createCallTest(){
    given().when().contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(prepareCreateCall())
        .post("/create")
        .then()
        .statusCode(200);
  }

  private CallList prepareCreateCall() {
    CallList callList = new CallList();
    Call call = new Call("916821260", "917101984", new Date(), new Date(), CallType.INBOUND);
    Call call2 = new Call("916821260", "919685412", new Date(), new Date(), CallType.INBOUND);
    List<Call> calls = new ArrayList<>();
    calls.add(call);
    calls.add(call2);
    callList.setCalls(calls);
    return callList;
  }

}
