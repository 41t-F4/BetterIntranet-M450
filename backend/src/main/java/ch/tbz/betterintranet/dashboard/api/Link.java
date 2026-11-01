package ch.tbz.betterintranet.dashboard.api;

/**
 * A link in "Meine Links" or "Quick Links".
 *
 * @param label    visible text
 * @param url      target; a frontend route for internal links, an absolute {@code https://} URL otherwise
 * @param external {@code true} when the link leaves the intranet and should open in a new tab
 */
public record Link(String label, String url, boolean external) {
}
