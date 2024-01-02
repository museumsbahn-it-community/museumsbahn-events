<script setup lang="ts">
import SiteHeader from './components/SiteHeader.vue';
</script>

<template>
  <div class="flex flex-row">
    <SiteHeader></SiteHeader>

    <main class="content w-full flex-grow md:p-5">
      <!-- route outlet -->
      <!-- component matched by the route will render here -->
      <NuxtPage/>
    </main>
  </div>
</template>

<style lang="scss">
@import 'primeflex/primeflex.css';
@import 'primeicons/primeicons.css';
@import 'leaflet/dist/leaflet.css';
@import 'material-icons/iconfont/material-icons.css';
</style>