package org.example.swapi.planets;

import org.example.api.Swapi;
import org.example.domain.Planet;
import org.example.domain.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GetPlanetTests {

	@Test
	public void testGetSingleById() {
		Result<Planet> result = Swapi.planets().getById("1");
		Assertions.assertEquals("1", result.getUid());
		Assertions.assertEquals("A planet.", result.getDescription());
		Planet planet = result.getProperties();
		Assertions.assertEquals("Tatooine", planet.getName());
		Assertions.assertEquals("10465", planet.getDiameter());
	}

}
