package ch.tbz.betterintranet.dashboard.infrastructure;

import ch.tbz.betterintranet.dashboard.api.Link;
import ch.tbz.betterintranet.dashboard.api.NewsItem;
import ch.tbz.betterintranet.dashboard.api.PreventionNotice;
import ch.tbz.betterintranet.dashboard.api.SchoolEvent;
import ch.tbz.betterintranet.dashboard.application.DashboardContentRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

/** Reads the home page content from {@code dashboard/content.json} on the classpath. */
@Repository
public class ClasspathDashboardContentRepository implements DashboardContentRepository {

    public static final String DEFAULT_LOCATION = "dashboard/content.json";

    @Autowired
    public ClasspathDashboardContentRepository(ObjectMapper mapper) {
        this(mapper, DEFAULT_LOCATION);
    }

    ClasspathDashboardContentRepository(ObjectMapper mapper, String location) {
    }

    @Override
    public List<NewsItem> news() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<SchoolEvent> events() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public PreventionNotice prevention() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<Link> myLinks() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<Link> quickLinks() {
        throw new UnsupportedOperationException("not implemented");
    }
}
