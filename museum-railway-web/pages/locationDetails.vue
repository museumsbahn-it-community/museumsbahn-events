<template>
  <div class="flex flex-row justify-content-center">
    <div class="w-full lg:w-10 xl:w-8 xxl:w-6 lg:p-5">
      <LocationDetails :museumLocation="museumLocation" :eventsGroupedByMonthAndDeparture="events"></LocationDetails>
    </div>
  </div>
  <div class="default-footer"></div>
</template>
<script setup lang="ts">
import { useRoute } from "nuxt/app";
import { getLocationById } from "~/composables/locationDataFunctions";

const { data: locations } = useAllLocations()
const { data: eventsRaw } = useAllEvents()

const route = useRoute();
const locationId = route?.params?.locationId as string;

const museumLocation = computed(() => getLocationById(locations.value ?? [], locationId));
const events = computed(() => eventsForLocationIdGrouped(eventsRaw.value ?? [], locationId));

useSeoMeta({
  title: () => `${museumLocation.value?.name}`,
  ogTitle: () => `Details und Veranstaltungen zu ${museumLocation.value?.name}`,
  description: 'Details zu den Veranstaltungen des Museums.',
  ogDescription: 'Details zu den Veranstaltungen des Museums.',
  ogImage: 'https://museumsbahn-events.at/img/social_media_preview.jpg',
  twitterCard: 'summary_large_image',
})
</script>
