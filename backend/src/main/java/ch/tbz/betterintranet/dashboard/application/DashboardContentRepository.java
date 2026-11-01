package ch.tbz.betterintranet.dashboard.application;

import ch.tbz.betterintranet.dashboard.api.Link;
import ch.tbz.betterintranet.dashboard.api.NewsItem;
import ch.tbz.betterintranet.dashboard.api.PreventionNotice;
import ch.tbz.betterintranet.dashboard.api.SchoolEvent;
import java.util.List;

/** Port to the editorial content of the home page (news, own events, prevention notice, links). */
public interface DashboardContentRepository {

    /** All news, in no particular order. */
    List<NewsItem> news();

    /** The school's own events (not holidays), in no particular order. */
    List<SchoolEvent> events();

    PreventionNotice prevention();

    /** "Meine Links": internal routes for the signed-in user. */
    List<Link> myLinks();

    /** "Quick Links": external school services. */
    List<Link> quickLinks();
}
