.<style lang="scss">
@use "../assets/colors.scss" as colors;
@use "../assets/variables_impl.scss" as variables;

.logo {
  height: calc(variables.$navbar-height - 24px);
}

.p-menubar-root-list {
  right: 0;
  left: initial !important;
  width: auto;
}

.sticky-header {
  top: 0;
  position: sticky;
  z-index: 1000;
}

.navbar {
  z-index: 1000;
  position: sticky;
  top: 0;
  left: 0;
  height: variables.$navbar-height;
  width: 100%;
  color: colors.$text-color-light;
  background: colors.$color-umbragrau;
}

.navbar-content {
  height: calc(variables.$navbar-height - 8px);
}

.navbar-line {
  z-index: 1001;
  display: flex;
  position: sticky;
  top: calc(variables.$navbar-height - 8px);
  left: 0;
  height: 8px;
  width: 100%;
  background: colors.$color-verkehrsrot;
}

.menubar {
  height: variables.$navbar-height;
}
</style>
<script setup lang="ts">
const viewport = useViewport();
const items: MenuItem[] = [
  { label: 'Hauptseite', route: '/' },
  { label: 'Museen und Bahnen', route: '/locations' },
  { label: 'Karte', route: '/locationMap', visible: () => viewport.isLessThan('tablet') },
  { label: 'Veranstaltungen', route: '/events' },
  { label: 'Projekttagebuch', route: '/news' },
  { label: 'Über uns', route: '/about' },
  { label: 'Mitmachen', route: '/participate' },
  { label: 'Impressum', route: '/impressum' },
];

</script>
<template>
  <header class="sticky-header">
    <div class="navbar flex flex-column">
      <div class="flex flex-row w-full navbar-content sm:px-5">
        <h1 class="m-2">
          <img class="logo" src="/logo-dark-full-transparent.png" alt="museumsbahn-events.at Logo"></img>
        </h1>
        <div class="flex-grow-1"></div>
        <Menubar class="menubar sm:ml-6 borderless-menubar" :model="items" :breakpoint="'1190px'">
          <!--          [style.textAlign]="'right'"-->
          <template #item="{ item, props, hasSubmenu }">
            <router-link v-if="item.route" v-slot="{ href, navigate }" :to="item.route" custom>
              <a v-ripple :href="href" v-bind="props.action" @click="navigate">
                <span :class="item.icon" class="mr-2" v-if="item.icon != null" />
                <span>{{ item.label }}</span>
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
      <div class="navbar-line"></div>
    </div>
  </header>
</template>