package ch.tbz.schoolmock.api;

/**
 * Processing status of an absence entry as the school's absence system would report it.
 */
public enum AbsenceStatus {

	/** Recorded by the teacher, the student has not yet handed in a justification. */
	OPEN,

	/** The justification was accepted. */
	EXCUSED,

	/** No justification was handed in within the deadline or it was rejected. */
	UNEXCUSED
}
