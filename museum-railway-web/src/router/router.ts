import Home from "../views/HomePage.vue";
import About from "../views/AboutPage.vue";
import {createRouter, createWebHashHistory} from 'vue-router';
import Locations from "../views/LocationsPage.vue";
import EventListPage from '../views/EventListPage.vue';

const routes = [
    { path: '/', component: Home },
    { path: '/about', component: About },
    { path: '/locations', component: Locations },
    { path: '/events', component: EventListPage },
];

const router = createRouter({
    history: createWebHashHistory(),
    routes,
});

export default router;