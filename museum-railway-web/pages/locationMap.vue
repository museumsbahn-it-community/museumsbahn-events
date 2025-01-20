<template>
  <div class="fill-page-height flex flex-column">
    <Button
        class="m-3"
        icon="pi pi-arrow-left" rounded outlined aria-label="Zurück"
        v-if="locationIdParam != undefined"
        @click="router.back()"/>
    <LocationMap :locations="locations"></LocationMap>
  </div>
</template>
<script setup lang="ts">
import { useAsyncData, useRoute, useRouter } from 'nuxt/app';
import { useLocationsStore } from '~/stores/LocationsStore';

const locationsStore = useLocationsStore()
await useAsyncData('locations', () => locationsStore.fetchLocations());
const locations = locationsStore.allLocations;

const router = useRouter();
const route = useRoute();
const locationIdParam = route?.params?.locationId;

useSeoMeta({
  title: () => `Übersichtskarte`,
  ogTitle: `Übersichtskarte über Museumsbahnen in Österreich`,
  description: 'Übersichtskarte über Eisenbahnmuseen und Museumsbahnen in Österreich',
  ogDescription: 'Auf dieser Übersichtskarte findest du Eisenbahnmuseen und Museumsbahnen in Österreich.',
  ogImage: 'https://museumsbahn-events.at/img/social_media_preview.jpg',
  twitterCard: 'summary_large_image',
})
</script>