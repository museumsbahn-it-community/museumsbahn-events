<template>
  <div class="flex flex-row justify-content-center">
    <div class="w-full lg:w-10 xl:w-8 xxl:w-6 lg:p-5">
        <LocationDetails
          :museumLocation="museumLocation"
          :eventsGroupedByMonthAndDeparture="events"
        ></LocationDetails>
    </div>
  </div>
</template>
<script setup lang="ts">
import { useRoute, useState } from "nuxt/app";
import { useLocations } from "~/composables/useLocations";
import { useEvents } from "~/composables/useEvents";

// Use composables instead of Pinia stores
const { locationById } = useLocations();
const { eventsForLocationIdGrouped } = useEvents();

const route = useRoute();
const locationId = route?.params?.locationId as string;

// Get the museum location using the composable
const museumLocation = locationById(locationId);

// Get events for the location using the composable
const events = useState(`location-${locationId}-events`, () => eventsForLocationIdGrouped(locationId));

useSeoMeta({
  title: () => `${museumLocation?.name}`,
  ogTitle: () => `Details und Veranstaltungen zu ${museumLocation?.name}`,
  description: 'Details zu den Veranstaltungen des Museums.',
  ogDescription: 'Details zu den Veranstaltungen des Museums.',
  ogImage: 'https://museumsbahn-events.at/img/social_media_preview.jpg',
  twitterCard: 'summary_large_image',
})
</script>
