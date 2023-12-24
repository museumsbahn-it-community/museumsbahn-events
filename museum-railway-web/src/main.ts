import './assets/main.scss';

import { createApp } from 'vue';
import App from './App.vue';
import router from './router/router';
import PrimeVue from 'primevue/config';

import Button from 'primevue/button';
import Menubar from 'primevue/menubar';
import Card from 'primevue/card';
import { createPinia } from 'pinia';

const pinia = createPinia();
const app = createApp(App);
app.use(router);
app.use(pinia);
app.use(PrimeVue);

app.component('PButton', Button);
app.component('PMenubar', Menubar);
app.component('PCard', Card);

app.mount('#app');
