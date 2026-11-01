package ch.tbz.betterintranet.dashboard.api;

/**
 * The "Prävention" box.
 *
 * @param title    card title
 * @param text     notice text
 * @param linkText label of the link
 * @param url      target of the link
 */
public record PreventionNotice(String title, String text, String linkText, String url) {
}
