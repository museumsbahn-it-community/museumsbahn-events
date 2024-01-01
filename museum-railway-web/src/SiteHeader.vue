<script setup lang="ts">
import { MenuItem } from 'primevue/menuitem';

const items: MenuItem[] = [
  {label: 'News', route: '/'},
  {label: 'Locations', route: '/locations'},
  {label: 'Events', route: '/events'},
  {label: 'About', route: '/about'},
];
</script>
<template>
  <header class="navbar">
    <div class="flex flex-row align-items-center">
      <div class="flex flex-column">
        <h1 class="m-0">
          museumsbahn-events.at
        </h1>
        <span class="text-sm">Alle Infos zu Museumsbahnen in Österreich</span>
      </div>
      <P-Menubar
          class="menubar borderless-menubar"
          :model="items"
      >
        <template #start>
        </template>

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
              <span :class="item.icon"/>
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
            <span :class="item.icon"/>
            <span class="ml-2">{{ item.label }}</span>
            <span
                v-if="hasSubmenu"
                class="pi pi-fw pi-angle-down ml-2"
            />
          </a>
        </template>
      </P-Menubar>
      <div class="flex-grow-1"></div>
    </div>
  </header>
</template>