package nl.tudelft.ewi.sorcerers.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import nl.tudelft.ewi.sorcerers.model.Diff;
import nl.tudelft.ewi.sorcerers.model.Warning;
import nl.tudelft.ewi.sorcerers.usecases.GetWarningsForCommit;
import nl.tudelft.ewi.sorcerers.usecases.GetWarningsForDiff;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@RolesAllowed("user")
@Path("/repos/{repo: ([-a-zA-Z_0-9]+\\/[-a-zA-Z_0-9]+)}")
public class RepoResource {
	@Context UriInfo uriInfo;
	private String repo;
	private GetWarningsForCommit gwfc;
	private GetWarningsForDiff gwfd;
	
	@Inject
	public RepoResource(@PathParam("repo") String repo, GetWarningsForCommit gwfc, GetWarningsForDiff gwfd) {
		this.repo = repo;
		this.gwfc = gwfc;
		this.gwfd = gwfd;
	}

	@GET
	@Path("pulls/{pull: [0-9]+}/diff/{base: [0-9a-z]+}/{head: [0-9a-z]+}")
	@Produces("application/vnd.octopull.repository+json")
	public RepositoryDTO getDiff(@PathParam("pull") Integer pullRequest, @PathParam("base") String base, @PathParam("head") String head) throws IOException {
		Diff diff = gwfd.execute(this.repo, base, head);
		
		List<WarningDTO> transferWarnings = new ArrayList<WarningDTO>();
		for (Warning w : diff.getWarnings()) {
			WarningDTO wdto = new WarningDTO(w.getId(), w.getPath(), w.getLine(), w.getCommit(), w.getTool(), w.getMessage());
			transferWarnings.add(wdto);
		}
		
		String createCommentURL = uriInfo.getBaseUriBuilder().path(CommentResource.class).path(CommentResource.class, "createCommentFromWarning").build().toString();
		return new RepositoryDTO(repo, new DiffDTO(diff.getBase(), diff.getHead(), transferWarnings, createCommentURL), pullRequest);
	}
	
	@JsonSerialize
	private static class RepositoryDTO {
		@JsonProperty
		private String id;
		@JsonProperty
		private DiffDTO diff;
		@JsonProperty
		private int pullRequestNumber;
		
		public RepositoryDTO(String id, DiffDTO diff, int pullRequestNumber) {
			this.id = id;
			this.diff = diff;
			this.pullRequestNumber = pullRequestNumber;
		}
	}

	private static class DiffDTO {
		@JsonProperty
		private String base;
		@JsonProperty
		private String head;
		@JsonProperty
		private List<WarningDTO> warnings;
		@JsonProperty
		private String createCommentURL;
		
		public DiffDTO(String base, String head, List<WarningDTO> warnings, String createCommentURL) {
			this.base = base;
			this.head = head;
			this.warnings = warnings;
			this.createCommentURL = createCommentURL;
		}
	}
	
	private static class WarningDTO {
		@JsonProperty
		private int id;
		@JsonProperty
		private String path;
		@JsonProperty
		private int line;
		@JsonProperty
		private String commit;
		@JsonProperty
		private String tool;
		@JsonProperty
		private String message;
		
		public WarningDTO(int warningId, String path, int line, String commit, String tool, String message) {
			this.id = warningId;
			this.path = path;
			this.line = line;
			this.commit = commit;
			this.tool = tool;
			this.message = message;
		}
	}
}