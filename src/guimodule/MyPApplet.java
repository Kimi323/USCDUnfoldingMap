package guimodule;

import processing.core.*;

public class MyPApplet extends PApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fileName = "/Users/kimi/eclipse-workspace/UCSDUnfoldingMaps/data/palmTrees.jpg";
	private PImage backgroundImage;
	
	// this method only run once
	public void setup() {		
		size(200, 200);
		backgroundImage = loadImage(fileName, "jpg");
	}
	
	//draw is a loop and will be run again and again
	public void draw() {
		//height and width are member variables which we can access
		backgroundImage.resize(0, height);  //height changes dynamically (here is 200)		
		image(backgroundImage, 0, 0);
		ellipse(width/4, height/5, width/5, height/5);		
		int h = hour(); //h is an int from 0 to 23
		double blue = (h + 1.0)/ 24.0 * 255;
		fill(255, 209, (float) blue);
	}
	
	
}
