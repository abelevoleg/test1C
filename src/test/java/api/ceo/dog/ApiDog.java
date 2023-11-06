package api.ceo.dog;

import com.google.gson.JsonParser;
import cucumber.api.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class ApiDog {
    private final String directory = "src/test/resourses/files/dog/images/";
    private final String fileName = "dog.jpg";

    @Then("отправляем get запрос")
    public void getApiDog() throws IOException {
        RestAssured.requestSpecification = getRequestSpec();

        String response = given().log().all()
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().body().asString();

        checkResponse(response);
    }

    @Then("отправляем post запрос")
    public void postApiDog() {
        given().log().all()
                .when()
                .post()
                .then()
                .statusCode(405)
                .assertThat()
                .body("status", equalTo("error"))
                .body("code", equalTo(405))
                .body("message", notNullValue());
    }


    private RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri("https://dog.ceo/api/breeds/image/random")
                .setAccept("application/json")
                .build();
    }


    private void checkResponse(String responseString) throws IOException {
        String link = new JsonParser().parse(responseString).getAsJsonObject().get("message").getAsString();

        FileUtils.cleanDirectory(new File(directory));
        File dogImage = getFile(link, directory + fileName);

        Assert.assertTrue("Скачанный файл пустой!", FileUtils.sizeOf(dogImage) != 0);
    }

    private File getFile(String url, String fileName) throws IOException {
        File file = new File(fileName);
        FileUtils.copyURLToFile(new URL(url), file, 60, 60);
        return file;
    }
}
