import { useState } from 'react'
import { RouterProvider } from 'react-router-dom'
import { AppDataProvider } from './providers/AppDataProvider'
import { createAppRouter } from './router'

/** Application root: startup data provider plus the router built from the module registry. */
export function App() {
  const [router] = useState(createAppRouter)
  return (
    <AppDataProvider>
      <RouterProvider router={router} />
    </AppDataProvider>
  )
}
