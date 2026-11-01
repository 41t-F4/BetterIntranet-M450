package ch.tbz.betterintranet.dashboard.infrastructure;

import ch.tbz.betterintranet.dashboard.api.Link;
import ch.tbz.betterintranet.dashboard.api.NewsItem;
import ch.tbz.betterintranet.dashboard.api.PreventionNotice;
import ch.tbz.betterintranet.dashboard.api.SchoolEvent;
import ch.tbz.betterintranet.dashboard.application.DashboardContentRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

/** Reads the home page content once from {@code dashboard/content.json} on the classpath. */
@Repository
public class ClasspathDashboardContentRepository implements DashboardContentRepository {

    public static final String DEFAULT_LOCATION = "dashboard/content.json";

    /** Shape of {@code content.json}. */
    record Content(List<NewsItem> news,
                   List<SchoolEvent> events,
                   PreventionNotice prevention,
                   List<Link> myLinks,
                   List<Link> quickLinks) {

        Content {
            news = List.copyOf(news);
            events = List.copyOf(events);
            myLinks = List.copyOf(myLinks);
            quickLinks = List.copyOf(quickLinks);
        }
    }

    private final Content content;

    @Autowired
    public ClasspathDashboardContentRepository(ObjectMapper mapper) {
        this(mapper, DEFAULT_LOCATION);
    }

    ClasspathDashboardContentRepository(ObjectMapper mapper, String location) {
        this.content = load(mapper, location);
    }

    @Override
    public List<NewsItem> news() {
        return content.news();
    }

    @Override
    public List<SchoolEvent> events() {
        return content.events();
    }

    @Override
    public PreventionNotice prevention() {
        return content.prevention();
    }

    @Override
    public List<Link> myLinks() {
        return content.myLinks();
    }

    @Override
    public List<Link> quickLinks() {
        return content.quickLinks();
    }

    private static Content load(ObjectMapper mapper, String location) {
        try (InputStream in = new ClassPathResource(location).getInputStream()) {
            return mapper.readValue(in, Content.class);
        } catch (IOException | JacksonException e) {
            throw new IllegalStateException("Cannot load dashboard content from classpath:" + location, e);
        }
    }
}
