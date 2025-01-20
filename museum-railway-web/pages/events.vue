<template>
  <div class="flex flex-row w-full sticky-content justify-content-center">
    <div class="flex flex-column h-full content-center-column mx-2">
      <Message severity="info"> Achtung! Die Daten werden automatisch erfasst und nicht manuell geprüft.
        Abfahrtszeiten und aktuelle Informationen immer auf den Webseiten der jeweiligen Veranstalter
        kontrollieren!
      </Message>
      <div class="h-1rem"></div>
      <div class="flex flex-column h-full mx-2 mb-6 md:mx-5 align-items-center">
        <EventList :eventsGroupedByMonthAndDeparture="eventGroups"></EventList>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { useEventsStore } from "~/stores/EventsStore";
import { useLocationsStore } from "~/stores/LocationsStore";
import { useAsyncData, useState } from "nuxt/app";

useI18n()

interface StateOption {

  name: string,
  code: string

}

const locationsStore = useLocationsStore();
const eventsStore = useEventsStore();
await useAsyncData('locations', () => locationsStore.fetchLocations());
await useAsyncData('events', () => eventsStore.fetchAllEvents());

const eventGroups = useState('filtered-events', () => eventsStore.filteredEventsGroupedByMonthAndDepartureTime);

useSeoMeta({
  title: 'Veranstaltungsliste',
  ogTitle: 'Veranstaltungsliste',
  description: 'Hier finden sich Veranstaltungen und Sonderfahrten von österreichs Museumsbahnen.',
  ogDescription: 'Finde Veranstaltungen und Sonderfahrten von Museumsbahnen in deiner Nähe.',
  ogImage: 'https://museumsbahn-events.at/img/social_media_preview.jpg',
  twitterCard: 'summary_large_image',
})

</script>