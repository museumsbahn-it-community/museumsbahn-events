<script setup lang="ts">
import SiteHeader from './components/SiteHeader.vue';
import { useGlobalConfig } from "~/composables/useGlobalConfig";

const route = useRoute();
const config = useRuntimeConfig();
const siteUrl = config.public.siteUrl || 'https://museumsbahn-events.at';
const canonicalUrl = computed(() => `${siteUrl}${route.fullPath}`);

useSeoMeta({
  title: 'Startseite',
  ogTitle: 'Startseite | MUSEUMSBAHN-events.at',
  description: 'Alle Infos zu Eisenbahnmuseen und Sonderfahrten in Österreich und Umgebung. Finden Sie historische Züge, Dampflokomotiven und Schmalspurbahnen.',
  ogDescription: 'Alle Infos zu Eisenbahnmuseen und Sonderfahrten in Österreich und Umgebung. Finden Sie historische Züge, Dampflokomotiven und Schmalspurbahnen.',
  ogImage: 'https://museumsbahn-events.at/img/social_media_preview.jpg',
  twitterCard: 'summary_large_image',
  twitterTitle: 'MUSEUMSBAHN-events.at - Eisenbahnmuseen und Sonderfahrten',
  twitterDescription: 'Alle Infos zu Eisenbahnmuseen und Sonderfahrten in Österreich und Umgebung.',
  twitterImage: 'https://museumsbahn-events.at/img/social_media_preview.jpg',
})

const { initHistoryCount } = useGlobalConfig();
onMounted(() => initHistoryCount());

</script>

<template>
  <div>
    <SiteHeader></SiteHeader>
    <main class="content w-full sticky-content">

      <!-- route outlet -->
      <!-- component matched by the route will render here -->
      <NuxtPage/>
    </main>
  </div>
  <Toast />
</template>
