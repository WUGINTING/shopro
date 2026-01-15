import { createPinia } from 'pinia'
import { useWindowStore } from './modules/window'

const pinia = createPinia()

export { useWindowStore }

export default pinia
