import axios from "axios";


let config = {
    baseURL: '/',
    timeout: 1000
}

const instance = axios.create(config)

export default instance