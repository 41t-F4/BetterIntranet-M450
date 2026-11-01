package ch.tbz.betterintranet.dashboard.api;

import java.util.List;

/**
 * Everything the home page shows, mirroring the cards of the old intranet.
 *
 * @param news       newsticker, newest first
 * @param events     "Termine der Schule": upcoming holidays and school events, earliest first
 * @param prevention the prevention notice (147.ch)
 * @param myLinks    "Meine Links": internal links personal to the user
 * @param quickLinks "Quick Links": external school services
 */
public record Dashboard(List<NewsItem> news,
                        List<SchoolEvent> events,
                        PreventionNotice prevention,
                        List<Link> myLinks,
                        List<Link> quickLinks) {

    public Dashboard {
        news = List.copyOf(news);
        events = List.copyOf(events);
        myLinks = List.copyOf(myLinks);
        quickLinks = List.copyOf(quickLinks);
    }
}
