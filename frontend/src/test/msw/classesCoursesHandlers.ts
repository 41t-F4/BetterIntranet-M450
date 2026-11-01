import { HttpResponse, http, type RequestHandler } from 'msw'
import { myClassesFixture } from '../fixtures/classes'
import { myCoursesFixture } from '../fixtures/courses'

/** Default handlers of the «Klassen & Kurse» module: `/api/classes` and `/api/courses`. */
export const classesCoursesHandlers: RequestHandler[] = [
  http.get('/api/classes', () => HttpResponse.json(myClassesFixture)),
  http.get('/api/courses', ({ request }) => {
    const semester = new URL(request.url).searchParams.get('semester')
    const courses = semester ? myCoursesFixture.filter((course) => course.semester === semester) : myCoursesFixture
    return HttpResponse.json(courses)
  }),
]
