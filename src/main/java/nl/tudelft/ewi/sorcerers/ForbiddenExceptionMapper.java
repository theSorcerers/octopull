package nl.tudelft.ewi.sorcerers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {
	@Context UriInfo uriInfo;
	
	@Override
	public Response toResponse(ForbiddenException exception) {
		// TODO add proper entity with login link
		URI loginLink = uriInfo.getBaseUriBuilder().path("/oauth/login/").build();
		Message message = new Message("warning", "Authentication error", "You are not logged in.");
		message.addAction(loginLink.toString(), "Log in");
		return Response.status(Status.FORBIDDEN)
				.entity(message)
				.type("application/vnd.octopull.message+json").build();
	}
	
	@JsonSerialize
	public static class Message {
		@JsonProperty
		private String level;
		@JsonProperty
		private String title;
		@JsonProperty
		private String message;
		@JsonProperty
		private List<Action> actions;
		
		public Message(String level, String title, String message) {
			 this.level = level;
			 this.title = title;
			 this.message = message;
			 this.actions = new ArrayList<Action>();
		}

		public void addAction(String href, String title) {
			this.actions.add(new Action(href, title));
		}

		@JsonSerialize
		private static class Action {
			@JsonProperty
			private String href;
			@JsonProperty
			private String title;

			public Action(String href, String title) {
				this.href = href;
				this.title = title;
			}
		}
	}
}
