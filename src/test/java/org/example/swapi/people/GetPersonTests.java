package org.example.swapi.people;

import io.restassured.response.Response;
import org.example.api.Swapi;
import org.example.domain.Message;
import org.example.domain.Person;
import org.example.domain.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class GetPersonTests {

	@Test
	public void testGetSingleById() {
		Result<Person> result = Swapi.people().getById("1");
		Assertions.assertEquals("1", result.getUid());
		Assertions.assertEquals("A person within the Star Wars universe", result.getDescription());
		Person person = result.getProperties();
		Assertions.assertEquals("Luke Skywalker", person.getName());
		Assertions.assertEquals("172", person.getHeight());
	}

//TODO: convert to junit parameterized tests

//	@DataProvider(name = "InvalidIdScenarios")
//	public Object[][] getInvalidIdScenarios() {
//		List<Object[]> data = new ArrayList<>();
//		String scenario;
//
//		//------------------------------------------------
//		scenario = "99999999999999999999999999999999999999999999999999999";
//		data.add(new Object[]{ scenario });
//		//------------------------------------------------
//		scenario = "X";
//		data.add(new Object[]{ scenario });
//		//------------------------------------------------
//
//		return data.toArray(new Object[][]{});
//	}
//
//	@Test(dataProvider = "InvalidIdScenarios")
//	public void testGetUsingInvalidId(String id) {
//		Response response = Swapi.people().tryGetById(id)
//				.then().statusCode(404)
//				.extract()
//				.response();
//
//		Message message = response.then().extract().as(Message.class);
//		Assertions.assertEquals(message.getMessage(), "not found");
//	}

}
