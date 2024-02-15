<script setup lang="ts">
import SiteHeader from './components/SiteHeader.vue';
</script>

<template>
  <div>
    <SiteHeader></SiteHeader>
    <main class="content w-full sticky-content">

      <!-- route outlet -->
      <!-- component matched by the route will render here -->
      <NuxtPage/>
    </main>
    <div class="footer"></div>
  </div>
</template>

<style lang="scss">
@import 'assets/theme/theme.scss';
@import 'primeflex/primeflex.css';
@import 'primeicons/primeicons.css';
@import 'leaflet/dist/leaflet.css';
@import 'material-icons/iconfont/material-icons.css';
</style>