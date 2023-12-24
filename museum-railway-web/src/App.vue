<script setup lang="ts">
const items: MenuItem[] = [
  {label: "News", route: "/"},
  {label: "Locations", route: "/locations"},
  {label: "About", route: "/about"}
];
</script>

<template>
    <div class="flex flex-row">
        <header class="navbar p-3">
            <div class="flex flex-row">
                <h1 class="m-0">
                    museumsbahnen.at
                </h1>
                <P-Menubar
                    class="menubar"
                    :model="items"
                >
                    <template #item="{ item, props, hasSubmenu }">
                        <router-link
                            v-if="item.route"
                            v-slot="{ href, navigate }"
                            :to="item.route"
                            custom
                        >
                            <a
                                v-ripple
                                :href="href"
                                v-bind="props.action"
                                @click="navigate"
                            >
                                <span :class="item.icon" />
                                <span class="ml-2">{{ item.label }}</span>
                            </a>
                        </router-link>
                        <a
                            v-else
                            v-ripple
                            :href="item.url"
                            :target="item.target"
                            v-bind="props.action"
                        >
                            <span :class="item.icon" />
                            <span class="ml-2">{{ item.label }}</span>
                            <span
                                v-if="hasSubmenu"
                                class="pi pi-fw pi-angle-down ml-2"
                            />
                        </a>
                    </template>
                </P-Menubar>
            </div>
        </header>

        <main class="content w-full flex-grow p-5">
            <!-- route outlet -->
            <!-- component matched by the route will render here -->
            <router-view />
        </main>
    </div>
</template>

<style lang="scss">
@import 'primeflex/primeflex.css';
@import 'primevue/resources/themes/lara-light-blue/theme.css';
@import 'leaflet/dist/leaflet.css';
</style>