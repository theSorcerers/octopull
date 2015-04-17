package nl.tudelft.ewi.sorcerers.resources;

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

import nl.tudelft.ewi.sorcerers.model.CommentService;
import nl.tudelft.ewi.sorcerers.model.Warning;
import nl.tudelft.ewi.sorcerers.usecases.GetWarningsForCommit;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@RolesAllowed("user")
@Path("/repos/{repo: ([-a-zA-Z_0-9]+\\/[-a-zA-Z_0-9]+)}")
public class RepoResource {
	@Context UriInfo uriInfo;
	private String repo;
	private GetWarningsForCommit gwfc;
	
	@Inject
	public RepoResource(@PathParam("repo") String repo, GetWarningsForCommit gwfc) {
		this.repo = repo;
		this.gwfc = gwfc;
	}

	@GET
	@Path("pulls/{pull: [0-9]+}/diff/{base: [0-9a-z]+}/{head: [0-9a-z]+}")
	@Produces("application/vnd.octopull.repository+json")
	public RepositoryDTO getDiff(@PathParam("pull") Integer pullRequest, @PathParam("base") String base, @PathParam("head") String head) {
		System.out.println(String.format("%s: %s / %s", repo, base, head));
		List<Warning> baseWarnings = gwfc.execute(this.repo, base);
		List<Warning> headWarnings = gwfc.execute(this.repo, head);
		List<Warning> warnings = new ArrayList<Warning>();
		warnings.addAll(baseWarnings);
		warnings.addAll(headWarnings);
		
		List<WarningDTO> transferWarnings = new ArrayList<WarningDTO>();
		for (Warning w : warnings) {
			WarningDTO wdto = new WarningDTO(w.getPath(), w.getLine(), w.getCommit(), w.getMessage());
			transferWarnings.add(wdto);
		}
		
		String createCommentURL = uriInfo.getBaseUriBuilder().path(CommentResource.class).path(CommentResource.class, "createCommentFromWarning").build().toString();
		return new RepositoryDTO(new DiffDTO(base, head, transferWarnings, createCommentURL), pullRequest);
	}
	
	@JsonSerialize
	private static class RepositoryDTO {
		@JsonProperty
		private DiffDTO diff;
		@JsonProperty
		private int pullRequestNumber;
		
		public RepositoryDTO(DiffDTO diff, int pullRequestNumber) {
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
		private String path;
		@JsonProperty
		private int line;
		@JsonProperty
		private String commit;
		@JsonProperty
		private String message;
		
		public WarningDTO(String path, int line, String commit, String message) {
			this.path = path;
			this.line = line;
			this.commit = commit;
			this.message = message;
		}
	}
}