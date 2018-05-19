package it.polito.tdp.flight.model;

import java.util.List;

import it.polito.tdp.flight.db.FlightDAO;

public class Model {

	FlightDAO fdao = null;
	List<Airport> airports;
	List<Airline> airlines;
	List<Route> routes;

	AirlineIdMap airlineIdMap;
	AirportIdMap airportIdMap;
	RouteIdMap routeIdMap;

	public Model() {
		fdao = new FlightDAO();

		airlineIdMap = new AirlineIdMap();
		airportIdMap = new AirportIdMap();
		routeIdMap = new RouteIdMap();

		airlines = fdao.getAllAirlines(airlineIdMap);
		System.out.println(airlines);

		airports = fdao.getAllAirports(airportIdMap);
		System.out.println(airports);

		routes = fdao.getAllRoutes(airportIdMap, airlineIdMap, routeIdMap);
		System.out.println(routes);
	}

}
