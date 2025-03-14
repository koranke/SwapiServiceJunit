package org.example.swapi.people;

import io.restassured.response.Response;
import org.example.api.Swapi;
import org.example.core.Constants;
import org.example.domain.ItemLink;
import org.example.domain.PaginatedResponse;
import org.example.domain.Person;
import org.example.domain.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GetPeopleTests {

	@Test
	public void testBasicGetAll() {
		Response response = Swapi.people().tryGetAll()
				.then().statusCode(200)
				.extract()
				.response();

		PaginatedResponse paginatedResponse = response.as(PaginatedResponse.class);
		Assertions.assertEquals("ok", paginatedResponse.getMessage());
		Assertions.assertNull(paginatedResponse.getPrevious());
		Assertions.assertNotNull(paginatedResponse.getResults());
		Assertions.assertEquals(10, paginatedResponse.getResults().size());
	}

	/*
	This test fails, but without requirements, not sure if this is a valid test or not.
	Appears that while "page" and "limit" use defaults when no query parameters are provided, "limit" is not used
	if "page" is also not provided and vice-versa.
	 */
	@Test
	@Disabled
	public void testGetAllWithLimit() {
		Response response = Swapi.people()
				.withQueryParameter("limit", "20")
				.tryGetAll()
				.then().statusCode(200)
				.extract()
				.response();

		PaginatedResponse paginatedResponse = response.as(PaginatedResponse.class);
		Assertions.assertEquals("ok", paginatedResponse.getMessage());
		Assertions.assertEquals(20, paginatedResponse.getResults().size());
	}

	@Test
	public void testSecondPage() {
		PaginatedResponse paginatedResponse = Swapi.people()
				.tryGetAll()
				.then().extract()
				.response().as(PaginatedResponse.class);

		Set<String> pageOneIds = paginatedResponse.getResults().stream().map(ItemLink::getUid).collect(Collectors.toSet());

		Response response = Swapi.people()
				.withQueryParameter("limit", "10")
				.withQueryParameter("page", "2")
				.tryGetAll()
				.then().statusCode(200)
				.extract()
				.response();

		paginatedResponse = response.as(PaginatedResponse.class);
		Assertions.assertEquals("ok", paginatedResponse.getMessage());
		Assertions.assertEquals("https://www.swapi.tech/api/people?page=1&limit=10", paginatedResponse.getPrevious());
		Assertions.assertEquals("https://www.swapi.tech/api/people?page=3&limit=10", paginatedResponse.getNext());

		Set<String> pageTwoIds = paginatedResponse.getResults().stream().map(ItemLink::getUid).collect(Collectors.toSet());
		pageTwoIds.retainAll(pageOneIds);
		Assertions.assertTrue(pageTwoIds.isEmpty());
	}

	@Test
	public void testGetAllWithCustomPageAndLimit() {
		Response response = Swapi.people()
				.withQueryParameter("limit", "5")
				.withQueryParameter("page", "2")
				.tryGetAll()
				.then().statusCode(200)
				.extract()
				.response();

		PaginatedResponse paginatedResponse = response.as(PaginatedResponse.class);
		Assertions.assertEquals("ok", paginatedResponse.getMessage());
		Assertions.assertEquals(5, paginatedResponse.getResults().size());
		Assertions.assertEquals("https://www.swapi.tech/api/people?page=1&limit=5", paginatedResponse.getPrevious());
		Assertions.assertEquals("https://www.swapi.tech/api/people?page=3&limit=5", paginatedResponse.getNext());
	}

	@Test
	public void testGetLastPage() {
		PaginatedResponse paginatedResponse = Swapi.people()
				.tryGetAll()
				.then().extract()
				.response().as(PaginatedResponse.class);

		String lastPage = String.valueOf(paginatedResponse.getTotalPages());
		String priorPage = String.valueOf(paginatedResponse.getTotalPages() - 1);
		Set<String> pageOneIds = paginatedResponse.getResults().stream().map(ItemLink::getUid).collect(Collectors.toSet());

		Response response = Swapi.people()
				.withQueryParameter("limit", "10")
				.withQueryParameter("page", lastPage)
				.tryGetAll()
				.then().statusCode(200)
				.extract()
				.response();

		paginatedResponse = response.as(PaginatedResponse.class);
		Assertions.assertEquals("ok", paginatedResponse.getMessage());
		Assertions.assertEquals(String.format("https://www.swapi.tech/api/people?page=%s&limit=10", priorPage), paginatedResponse.getPrevious());
		Assertions.assertNull(paginatedResponse.getNext());

		Set<String> lastPageIds = paginatedResponse.getResults().stream().map(ItemLink::getUid).collect(Collectors.toSet());
		lastPageIds.retainAll(pageOneIds);
		Assertions.assertTrue(lastPageIds.isEmpty());
	}


	@Test
	public void testGetByName() {
		String searchName = "Luke Skywalker";
		List<Result<Person>> people = Swapi.people().getFiltered("name", searchName);
		Assertions.assertEquals(1, people.size());
		Assertions.assertEquals(searchName, people.get(0).getProperties().getName());
		Assertions.assertEquals("1", people.get(0).getUid());

		String expectedPersonUrl = Constants.BASE_SWAPI_URL + "people/1";
		Assertions.assertEquals(expectedPersonUrl, people.get(0).getProperties().getUrl());
	}

	@Test
	public void testGetByNameNoMatch() {
		List<Result<Person>> people = Swapi.people().getFiltered("name", "The Hulk");
		Assertions.assertEquals(0, people.size());
	}

	@Test
	public void testGetByNameWithMultiple() {
		String searchName = "Skywalker";
		List<Result<Person>> people = Swapi.people().getFiltered("name", searchName);
		Assertions.assertTrue(people.size() > 1);
		for (Result<Person> person : people) {
			Assertions.assertTrue(person.getProperties().getName().contains(searchName));
		}
	}

}
