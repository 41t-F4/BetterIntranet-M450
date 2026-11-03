package ch.tbz.betterintranet.testmodule.api;

import ch.tbz.betterintranet.core.gateway.Query;

/** Test-only query used to exercise the gateway end-to-end through a real module package. */
public record PingQuery(String echo) implements Query<String> {
}
