import './assets/main.scss';

import { createApp } from 'vue';
import App from './App.vue';
import router from './router/router';
import PrimeVue from 'primevue/config';

import Button from 'primevue/button';
import Menubar from 'primevue/menubar';
import Card from 'primevue/card';
import { createPinia } from 'pinia';
import Ripple from 'primevue/ripple';

import 'primevue/resources/themes/mdc-light-deeppurple/theme.css';
import ScrollPanel from 'primevue/scrollpanel';
import Panel from 'primevue/panel';
import Timeline from 'primevue/timeline';
import Calendar from 'primevue/calendar';
import MultiSelect from 'primevue/multiselect';
import InlineMessage from 'primevue/inlinemessage';

const pinia = createPinia();
const app = createApp(App);
app.directive('ripple', Ripple);
app.use(router);
app.use(pinia);
app.use(PrimeVue, {

});

app.component('PButton', Button);
app.component('PMenubar', Menubar);
app.component('PCard', Card);
app.component('ScrollPanel', ScrollPanel);
app.component('Panel', Panel);
app.component('Timeline', Timeline);
app.component('Calendar', Calendar);
app.component('MultiSelect', MultiSelect);
app.component("InlineMessage", InlineMessage);


app.mount('#app');
