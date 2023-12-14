<script setup lang="ts">
import {MenuItem} from "primevue/menuitem";

const items: MenuItem[] = [
  {label: "News", route: "/"},
  {label: "Locations", route: "/map"},
  {label: "Map", route: "/map"},
  {label: "About", route: "/about"}
]
</script>

<template>
  <div class="flex flex-column">
    <header class="navbar">
      <div class="flex flex-row">
        <h1>museumsbahnen.at</h1>
        <Menubar class="menubar" :model="items">
          <template #item="{ item, props, hasSubmenu }">
            <router-link v-if="item.route" v-slot="{ href, navigate }" :to="item.route" custom>
              <a v-ripple :href="href" v-bind="props.action" @click="navigate">
                <span :class="item.icon" />
                <span class="ml-2">{{ item.label }}</span>
              </a>
            </router-link>
            <a v-else v-ripple :href="item.url" :target="item.target" v-bind="props.action">
              <span :class="item.icon" />
              <span class="ml-2">{{ item.label }}</span>
              <span v-if="hasSubmenu" class="pi pi-fw pi-angle-down ml-2" />
            </a>
          </template>
        </Menubar>
      </div>
    </header>

    <main class="content">
      <!-- route outlet -->
      <!-- component matched by the route will render here -->
      <router-view></router-view>
    </main>
  </div>
</template>

<style>
@import 'primeflex/primeflex.css';
@import 'primevue/resources/themes/lara-light-blue/theme.css';
</style>