package module6;

import java.util.List;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import processing.core.PConstants;
import processing.core.PGraphics;

/** 
 * A class to represent AirportMarkers on a world map.
 *   
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMarker extends CommonMarker {
	public static List<SimpleLinesMarker> routes;
	
	// constructor
	public AirportMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
		//System.out.println("Enter constructor");
		//java.util.HashMap<String, Object> properties = city.getProperties();
		//System.out.println("properties: " + properties);
	    // properties: {country="United States", altitude=0, code="DHB", city="Deer Harbor", name="Deer Harbor Seaplane"}
	}
	
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		pg.fill(113, 182, 247);
		pg.ellipse(x, y, 7, 7);		
	}

	@Override
	public void showTitle(PGraphics pg, float x, float y) {
		//System.out.println(">> enter showTitle");
		// show rectangle with title
		String title = getTitle();
		pg.pushStyle();
		
		pg.rectMode(PConstants.CORNER);
		
		pg.stroke(110);
		pg.fill(182,232,136);
		pg.rect(x, y + 15, pg.textWidth(title) +6, 18, 5);
		
		pg.textAlign(PConstants.LEFT, PConstants.TOP);
		pg.fill(0);
		pg.text(title, x + 3 , y +18);
				
		pg.popStyle();		
	}
	
	public String getTitle() {
		//System.out.println(">> enter getTitle()");
		//System.out.println((String) getProperty("code"));
		String code = ((String) getProperty("code")).replace("\"", "");
		String name = ((String) getProperty("name")).replace("\"", "");
		String city = ((String) getProperty("city")).replace("\"", "");
		String country = ((String) getProperty("country")).replace("\"", "");
		String title = name + "/" + code + "  " + city + " - " + country;
		return title;			
	}
	
	public String getId() {
		return (String) getProperty("id");
	}	
}
