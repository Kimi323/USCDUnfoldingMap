package module6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMap extends PApplet {
	// You can ignore this.  It's to get rid of eclipse warnings
	private static final long serialVersionUID = 1L;
	
	UnfoldingMap map;
	private List<Marker> airportList;
	List<Marker> routeList;
	
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	
	public void setup() {
		// setting up PApplet
		size(1600, 1200, OPENGL);
		
		// setting up map and default events
		//map = new UnfoldingMap(this, 50, 50, 750, 550);
		map = new UnfoldingMap(this, 150, 50, 1500, 1100, new Google.GoogleMapProvider());
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
		
		// list for markers, hashmap for quicker access when matching with routes
		airportList = new ArrayList<Marker>();
		HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
		
		// create airport markers from features
		for(PointFeature feature : features) {
			AirportMarker m = new AirportMarker(feature);
			m.setRadius(5);
			airportList.add(m);
			
			// put airport in hashmap with OpenFlights unique id for key, location of airport for value
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());		
		}
		
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		
		for(ShapeFeature route : routes) {
			// get source and destination airportIds (these 2 properties are set inside ParseFeed.java)
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));

			// use key(airportId) to get locations from airports(Hashmap<Integer, Location>), add locations to ShapeFeature route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			// Creates a line marker from shape feature with additional properties.
			// each ShapeFeature route has 2 properties: source and dest
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
			// hide all routes when application starts
			sl.setHidden(true);
			//System.out.println(sl.getProperties());
			// add all SimpleLinesMarkers to routeList.
			routeList.add(sl);
		}		
		// add all markers to map
		map.addMarkers(routeList);		
		map.addMarkers(airportList);
		
	}
	
	public void draw() {
		background(0);
		map.draw();		
	}
	
	@Override
	public void mouseMoved() {
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;		
		}
		selectMarkerIfHover(airportList);
	}
	
	// If there is a marker selected 
	private void selectMarkerIfHover(List<Marker> markers) {
		// Abort if there's already a marker selected
		if (lastSelected != null) {
			return;
		}
	
		for (Marker m : markers) {
			CommonMarker marker = (CommonMarker)m;
			if (marker.isInside(map,  mouseX, mouseY)) {
				lastSelected = marker;
				marker.setSelected(true);
				return;
			}
		}
	}
	
	@Override
	public void mouseClicked() {
		if (lastClicked != null) {
			//lastClicked is not null, show all hidden markers, hide routes and set lastClicked to null
			unhideMarkers();
			hideRoutes();
			lastClicked = null;
		}
		else if (lastClicked == null) 
		{
			//if lastClicked is null, set lastClicked to the marker we clicked
			checkAirportsForClick();
		}
	}
	
	// helper methods	
	private void checkAirportsForClick() {
		// will this situation ever occur?
		if (lastClicked != null) return;
		// Loop over the earthquake markers to see if one of them is selected
		for (Marker m : airportList) {
			AirportMarker airportMarker = (AirportMarker)m;
			if (!m.isHidden() && m.isInside(map, mouseX, mouseY)) {
				lastClicked = airportMarker;
				// hide other markers except lastClicked
				hideMarkers();
				// still show lastClick even there are not routes for this airport
				lastClicked.setHidden(false);
				showRoutesForClickedAirport((AirportMarker)lastClicked);
				return;
			}
		}
	}
	
	private void showRoutesForClickedAirport(AirportMarker airportMarker) {		
		int airportId = Integer.parseInt(airportMarker.getId());
		for (Marker m : routeList) {
			SimpleLinesMarker lm = (SimpleLinesMarker)m;
			int sourceId = Integer.parseInt((String) lm.getProperty("source"));
			int destId = Integer.parseInt((String) lm.getProperty("destination"));
			if (airportId == sourceId || airportId == destId) {
				lm.setHidden(false);
				showAirportOnRoute(sourceId);
				showAirportOnRoute(destId);
			}
		}
	}
	
	// show airportMarker if id is the same (inputId comes from route properties: source or destination)
	private void showAirportOnRoute(int inputId) {
		for (Marker m : airportList) {
			AirportMarker am = (AirportMarker)m;
			int airportId = Integer.parseInt(am.getId());
			if (inputId == airportId) {
				am.setHidden(false);
			}
		}
	}
	
	private void unhideMarkers() {
		for (Marker m : airportList) {
			m.setHidden(false);
		}
	}
	
	private void hideRoutes() {
		for (Marker lm : routeList) {
			lm.setHidden(true);
		}
	}
	
	private void hideMarkers() {
		for (Marker m : airportList) {
			m.setHidden(true);
		}
	}
}
