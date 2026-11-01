package ch.tbz.schoolmock.api;

/**
 * The part of the school a course or grade belongs to.
 *
 * <p>Swiss vocational education splits a student's week between the vocational school itself
 * ({@link #TBZ}), the optional Berufsmaturitätsschule ({@link #BMS}) and the inter-company courses
 * run by the industry association ({@link #UEK}, "überbetriebliche Kurse").
 */
public enum SchoolPart {

	/** Berufsmaturitätsschule (vocational baccalaureate), general-education subjects. */
	BMS,

	/** Technische Berufsschule Zürich, the modules of the apprenticeship itself. */
	TBZ,

	/** Überbetriebliche Kurse, practical block courses run outside the school. */
	UEK
}
