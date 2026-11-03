import { HttpResponse, http, type RequestHandler } from 'msw'
import { FIXTURE_WEEK_START, holidaysFixture, lessonsFixture, timetableClassesFixture } from '../fixtures/timetable'

/**
 * Default handlers of the timetable module: the fixture lessons for the fixture week of AP 24b,
 * an empty list for every other class or week.
 */
export const timetableHandlers: RequestHandler[] = [
  http.get('/api/timetable/classes', () => HttpResponse.json(timetableClassesFixture)),
  http.get('/api/timetable/holidays', () => HttpResponse.json(holidaysFixture)),
  http.get('/api/timetable', ({ request }) => {
    const params = new URL(request.url).searchParams
    const isFixtureWeek = params.get('classId') === '7471' && params.get('from') === FIXTURE_WEEK_START
    return HttpResponse.json(isFixtureWeek ? lessonsFixture : [])
  }),
]
