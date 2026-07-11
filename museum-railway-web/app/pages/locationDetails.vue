<template>
  <div class="location-details w-full h-full align-items-center flex flex-column p-2">
    <div class="w-full xl:w-8 mb-5">
      <LocationDetails :museumLocation="museumLocation" :eventsGroupedByMonthAndDeparture="events"></LocationDetails>
    </div>
  </div>
  <div class="default-footer"></div>
</template>
<style lang="scss">
@use '../assets/variables_impl' as variables;

.location-details {
  top: calc(variables.$navbar-height);
  min-height: calc(100vh - variables.$navbar-height);
}
</style>
<script setup lang="ts">
import {useRoute} from "nuxt/app";
import {getLocationById} from "~/composables/locationDataFunctions";

const {data: locations} = useAllLocations();
const {data: eventsRaw} = useAllEvents();

const route = useRoute();
const locationId = route?.params?.locationId as string;
const viewport = useViewport();

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
