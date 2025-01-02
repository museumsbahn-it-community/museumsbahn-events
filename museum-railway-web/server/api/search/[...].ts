import {joinURL} from 'ufo'

export default defineEventHandler(async (event) => {
    const proxyUrl = useRuntimeConfig().boudiccaSearchApiProxyUrl
    const path = event.path.replace(/^\/api\/search\//, '')
    const target = joinURL(proxyUrl, path)
    console.log("proxy request to ", target)
    return proxyRequest(event, target)
})