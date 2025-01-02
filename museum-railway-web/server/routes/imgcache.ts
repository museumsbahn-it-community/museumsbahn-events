import {joinURL} from 'ufo'

export default defineEventHandler(async (event) => {
    const proxyUrl = useRuntimeConfig().backendApiProxyUrl
    const target = joinURL(proxyUrl, event.path)
    return proxyRequest(event, target)
})