<style>
.p-menubar-root-list {
  right: 0;
  left: initial !important;
  width: auto;
}
</style>
<script setup lang="ts">
const viewport = useViewport();
const items: MenuItem[] = [
  {label: 'Neuigkeiten', route: '/'},
  {label: 'Museen und Bahnen', route: '/locations'},
  {label: 'Karte', route: '/locationMap', visible: () => viewport.isLessThan('tablet')},
  {label: 'Veranstaltungen', route: '/events'},
  {label: 'Über uns', route: '/about'},
  {label: 'Mitmachen', route: '/participate'},
  {label: 'Impressum', route: '/impressum'},
];

</script>
<template>
  <header class="navbar">
    <div class="flex flex-row align-items-center w-full">
      <div class="flex flex-column">
        <h1 class="m-0">
          museumsbahn-events.at (BETA)
        </h1>
        <span class="text-sm hidden sm:inline">Alle Infos zu Museumsbahnen in Österreich</span>
      </div>
      <div class="flex-grow-1"></div>
      <Menubar
          class="menubar borderless-menubar"
          :model="items"
          :breakpoint="'1190px'"
      >
      <!--          [style.textAlign]="'right'"-->
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
              <span :class="item.icon" class="mr-2" v-if="item.icon != null"/>
              <span>{{ item.label }}</span>
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
      </Menubar>
    </div>
  </header>
</template>