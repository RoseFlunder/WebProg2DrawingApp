package de.hsb.webprog2.drawing.rest;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.spi.resource.Singleton;

@Singleton
@Path("drawing")
public class DrawingResource {
	
	@Context
	private ServletContext context;
	
	@PostConstruct
	private void init() {
		System.out.println(context);
	}
		

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getValue(){
		return "test";
	}
}
