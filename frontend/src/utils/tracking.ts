export interface TrackingPayload {
  [key: string]: unknown
}

interface StoredUtm {
  utm_source?: string
  utm_medium?: string
  utm_campaign?: string
  utm_term?: string
  utm_content?: string
  ts: number
}

const UTM_KEY = 'utm_attribution'
const SESSION_KEY = 'tracking_session_id'
const UTM_EXPIRE_MS = 30 * 24 * 60 * 60 * 1000

const UTM_KEYS = [
  'utm_source',
  'utm_medium',
  'utm_campaign',
  'utm_term',
  'utm_content'
] as const

const generateSessionId = () =>
  `${Date.now().toString(36)}-${Math.random().toString(36).slice(2, 10)}`

export const getSessionId = (): string => {
  const existing = localStorage.getItem(SESSION_KEY)
  if (existing) return existing

  const created = generateSessionId()
  localStorage.setItem(SESSION_KEY, created)
  return created
}

export const initUtmFromUrl = (): void => {
  const params = new URLSearchParams(window.location.search)
  const utm: StoredUtm = { ts: Date.now() }

  UTM_KEYS.forEach((key) => {
    const value = params.get(key)
    if (value) utm[key] = value
  })

  const hasUtm = UTM_KEYS.some((key) => Boolean(utm[key]))
  if (hasUtm) {
    localStorage.setItem(UTM_KEY, JSON.stringify(utm))
    return
  }

  const raw = localStorage.getItem(UTM_KEY)
  if (!raw) return

  try {
    const parsed = JSON.parse(raw) as StoredUtm
    if (Date.now() - parsed.ts > UTM_EXPIRE_MS) {
      localStorage.removeItem(UTM_KEY)
    }
  } catch {
    localStorage.removeItem(UTM_KEY)
  }
}

export const getUtmContext = (): Record<string, string> => {
  const raw = localStorage.getItem(UTM_KEY)
  if (!raw) return {}

  try {
    const parsed = JSON.parse(raw) as StoredUtm
    if (Date.now() - parsed.ts > UTM_EXPIRE_MS) return {}

    const output: Record<string, string> = {}
    UTM_KEYS.forEach((key) => {
      if (parsed[key]) output[key] = parsed[key] as string
    })
    return output
  } catch {
    return {}
  }
}

export const trackEvent = (event: string, payload: TrackingPayload = {}): void => {
  let userId: number | null = null
  const rawUser = localStorage.getItem('user')
  if (rawUser) {
    try {
      userId = JSON.parse(rawUser)?.id ?? null
    } catch {
      userId = null
    }
  }

  const eventPayload = {
    event,
    event_time: new Date().toISOString(),
    session_id: getSessionId(),
    user_id: userId,
    page_path: window.location.pathname,
    referrer: document.referrer || null,
    ...getUtmContext(),
    ...payload
  }

  // Temporary frontend-only tracking. Backend ingestion can be added later.
  // eslint-disable-next-line no-console
  console.log('[track]', eventPayload)
}
