import './assets/main.scss'

import {createApp} from 'vue'
import App from './App.vue'
import router from "./router/router";
import PrimeVue from "primevue/config";

import Button from "primevue/Button"
import Menubar from "primevue/menubar";

const app = createApp(App);
app.use(router);
app.use(PrimeVue);

app.component('Button', Button);
app.component('Menubar', Menubar)

app.mount('#app');
