package ch.tbz.betterintranet.dashboard.api;

import java.time.LocalDate;

/**
 * One entry of the newsticker.
 *
 * @param id          stable id of the entry
 * @param title       headline
 * @param text        body text
 * @param publishedAt publication date
 */
public record NewsItem(long id, String title, String text, LocalDate publishedAt) {
}
