import {joinURL} from 'ufo'

export default defineEventHandler(async (event) => {
    const proxyUrl = useRuntimeConfig().backendApiProxyUrl
    const target = joinURL(proxyUrl, event.path)
    console.log("proxying request to: ", target)
    return proxyRequest(event, target)
})