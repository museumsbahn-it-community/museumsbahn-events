<script setup lang="ts">
import SiteHeader from './components/SiteHeader.vue';

useSeoMeta({
  title: 'museumsbahn-events.at',
  ogTitle: 'museumsbahn-events.at',
  description: 'Alle Infos zu Eisenbahnmuseen, Modellbahnen und Sonderfahrten in Österreich und Umgebung.',
  ogDescription: 'Alle Infos zu Eisenbahnmuseen, Modellbahnen und Sonderfahrten in Österreich und Umgebung.',
  ogImage: 'https://museumsbahn-events.at/img/social_media_preview.jpg',
  twitterCard: 'summary_large_image',
})
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
@import 'material-icons/iconfont/material-icons.css';
</style>