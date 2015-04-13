package nl.tudelft.ewi.sorcerers.resources;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nl.tudelft.ewi.sorcerers.model.Warning;
import nl.tudelft.ewi.sorcerers.usecases.GetWarningsForCommit;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@RolesAllowed("user")
@Path("repos/{repo: ([-a-zA-Z_0-9]+\\/[-a-zA-Z_0-9]+)}")
public class RepoResource {
	private String repo;
	private GetWarningsForCommit gwfc;
	
	@Inject
	public RepoResource(@PathParam("repo") String repo, GetWarningsForCommit gwfc) {
		this.repo = repo;
		this.gwfc = gwfc;
	}

	@GET
	@Path("diff/{base: [0-9a-z]+}/{head: [0-9a-z]+}")
	@Produces("application/vnd.octopull.repository+json")
	public RepositoryDTO getDiff(@PathParam("base") String base, @PathParam("head") String head) {
		System.out.println(String.format("%s: %s / %s", repo, base, head));
		List<Warning> baseWarnings = gwfc.execute(this.repo, base);
		List<Warning> headWarnings = gwfc.execute(this.repo, head);
		List<Warning> warnings = new ArrayList<Warning>();
		warnings.addAll(baseWarnings);
		warnings.addAll(headWarnings);
		
		return new RepositoryDTO(new DiffDTO(base, head, warnings));
	}
	
	@JsonSerialize
	private static class RepositoryDTO {
		@JsonProperty
		private DiffDTO diff;
		
		public RepositoryDTO(DiffDTO diff) {
			this.diff = diff;
		}
	}

	private static class DiffDTO {
		@JsonProperty
		private String base;
		@JsonProperty
		private String head;
		@JsonProperty
		private List<Warning> warnings;
		
		public DiffDTO(String base, String head, List<Warning> warnings) {
			this.base = base;
			this.head = head;
			this.warnings = warnings;
		}
	}
}