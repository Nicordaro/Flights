package it.polito.tdp.flight.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.flight.db.FlightDAO;

public class Model {

	FlightDAO fdao = null;
	List<Airport> airports;
	List<Airline> airlines;
	List<Route> routes;

	AirlineIdMap airlineIdMap;
	AirportIdMap airportIdMap;
	RouteIdMap routeIdMap;
	SimpleDirectedWeightedGraph<Airport, DefaultWeightedEdge> grafo;

	public Model() {
		fdao = new FlightDAO();

		airlineIdMap = new AirlineIdMap();
		airportIdMap = new AirportIdMap();
		routeIdMap = new RouteIdMap();

		airlines = fdao.getAllAirlines(airlineIdMap);
		System.out.println(airlines.size());

		airports = fdao.getAllAirports(airportIdMap);
		System.out.println(airports.size());

		routes = fdao.getAllRoutes(airportIdMap, airlineIdMap, routeIdMap);
		System.out.println(routes.size());
	}

	public List<Airport> getAirports() {
		if (this.airports == null) {
			return new ArrayList<Airport>();
		}
		return this.airports;
	}

	public void createGraph() {
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);

		Graphs.addAllVertices(grafo, this.airports);

		for (Route r : routes) {
			Airport source = r.getSourceAirport();
			Airport destination = r.getDestinationAirport();
			if (!source.equals(destination)) {
				double weight = LatLngTool.distance(new LatLng(source.getLatitude(), source.getLongitude()),
						new LatLng(destination.getLatitude(), destination.getLongitude()), LengthUnit.KILOMETER);
				Graphs.addEdge(grafo, source, destination, weight);
			}
		}

		System.out.println(grafo.vertexSet().size());
		System.out.println(grafo.edgeSet().size());
	}

	public void printStats() {
		if (grafo == null) {
			this.createGraph();
		}
		ConnectivityInspector<Airport, DefaultWeightedEdge> ci = new ConnectivityInspector<Airport, DefaultWeightedEdge>(
				grafo);
		System.out.println(ci.connectedSets().size());

	}

	public Set<Airport> getBiggestSCC() {
		ConnectivityInspector<Airport, DefaultWeightedEdge> ci = new ConnectivityInspector<Airport, DefaultWeightedEdge>(
				grafo);

		Set<Airport> bestSet = null;
		int bestSize = 0;

		for (Set<Airport> s : ci.connectedSets()) {
			if (s.size() > bestSize) {
				bestSet = new HashSet<Airport>(s);
				bestSize = s.size();

			}
		}
		return bestSet;
	}

	public List<Airport> getShortestPath(int id1, int id2) {

		Airport airport1 = airportIdMap.get(id1);
		Airport airport2 = airportIdMap.get(id2);

		if (airport1 == null || airport2 == null) {
			throw new RuntimeException("Gli aeroporti selezionati non sono presenti in memoria");
		}

		ShortestPathAlgorithm<Airport, DefaultWeightedEdge> spa = new DijkstraShortestPath<Airport, DefaultWeightedEdge>(
				grafo);
		double weight = spa.getPathWeight(airport1, airport2);
		System.out.println(airport1.getName());
		System.out.println(airport2.getName());
		System.out.println(weight);

		GraphPath<Airport, DefaultWeightedEdge> gp = spa.getPath(airport1, airport2);

		return gp.getVertexList();
	}

}
