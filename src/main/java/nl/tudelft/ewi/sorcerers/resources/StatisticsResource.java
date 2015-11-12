package nl.tudelft.ewi.sorcerers.resources;

import java.security.Principal;
import java.util.Map;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import nl.tudelft.ewi.sorcerers.usecases.StorePageView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Path("statistics")
public class StatisticsResource {
	private StorePageView storePageView;

	@Inject
	public StatisticsResource(StorePageView storePageView) {
		this.storePageView = storePageView;
	}
	
	@POST
	@Transactional
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addPageView(PageViewDTO pvd, @Context SecurityContext securityContext) {
		String user = null;
		Principal userPrincipal = securityContext.getUserPrincipal();
		if (userPrincipal != null) {
			user = userPrincipal.getName();
		}
		storePageView.execute(user, pvd.screen, pvd.getHref());
		return Response.ok().build();
	}

	@JsonSerialize
	private static class PageViewDTO {
		@JsonProperty
		private String href;
		@JsonProperty
		private Map<String, String>	screen;
		
		@SuppressWarnings("unused")
		private PageViewDTO() {}
		
		public String getHref() {
			return href;
		}
		
		public Map<String, String> getScreen() {
			return screen;
		}
	}
}
