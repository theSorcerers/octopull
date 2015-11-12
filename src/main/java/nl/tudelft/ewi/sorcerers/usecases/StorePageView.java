package nl.tudelft.ewi.sorcerers.usecases;

import java.util.Date;
import java.util.Map;

import javax.inject.Inject;

import nl.tudelft.ewi.sorcerers.model.PageView;
import nl.tudelft.ewi.sorcerers.model.PageViewRepository;

public class StorePageView {
	private PageViewRepository pageViewRepository;
	
	@Inject
	public StorePageView(PageViewRepository pageViewRepository) {
		this.pageViewRepository = pageViewRepository;
	}

	public void execute(String user, Map<String, String> screen, String href) {
		this.pageViewRepository.add(new PageView(href, user, screen, new Date()));
	}
}
