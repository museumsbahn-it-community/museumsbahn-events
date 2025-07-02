<style>
.top-filters {
  max-width: 40rem;
}
</style>
<template>
  <div class="flex flex-row w-full sticky-content justify-content-center">
    <div class="flex flex-column h-full content-center-column mx-2">
      <Message class="my-2" severity="info" icon="pi pi-info-circle"> Achtung! Die Daten werden automatisch erfasst und
        nicht manuell geprüft.
        Abfahrtszeiten und aktuelle Informationen immer auf den Webseiten der jeweiligen Veranstalter
        kontrollieren!
      </Message>
      <div class="h-1rem"></div>
      <div class="flex flex-column h-full mx-2 mb-6 md:mx-5 align-items-center">
        <!-- Search and Filter Section -->
        <div class="top-filters w-full flex flex-column align-items-center mb-4">
          <!-- Search Bar -->
          <InputGroup class="w-full flex mb-3">
            <InputGroupAddon>
              <i class="pi pi-search"></i>
            </InputGroupAddon>
            <InputText v-model="searchTerm" placeholder="Suche nach Veranstaltungen, Orten oder Beschreibungen"
                       class="w-full"/>
          </InputGroup>

          <!-- State Filter -->
          <div class="mb-3 flex flex-row flex-wrap justify-content-center">
            <ToggleButton v-for="state in stateList" :key="state.code"
                          :modelValue="selectedStates.includes(state.code)"
                          :onLabel="state.name"
                          :offLabel="state.name"
                          class="mx-1 mb-2"
                          @click="toggleState(state.code)"/>
          </div>
        </div>

        <EventList :eventsGroupedByMonthAndDeparture="filteredEventGroups"></EventList>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import {useAllEvents} from "~/composables/eventComposables";
import {useAllLocations} from "~/composables/locationComposables";
import {
  eventsGroupedByMonthAndDepartureTime,
  filterEvents,
  type MuseumEventGroupGroup
} from "~/composables/eventDataFunctions";
import {getStateList, type StateInfo} from "~/composables/locationDataFunctions";
import type {MuseumEvent} from "~/apiModel/apiModel";

const {data: events} = useAllEvents();
const {data: locations} = useAllLocations();

const searchTerm = ref('');
const selectedStates = ref<string[]>([]);
const stateList = computed<StateInfo[]>(() => locations.value ? getStateList(locations.value) : []);

// Toggle state selection
const toggleState = (stateCode: string) => {
  if (selectedStates.value.includes(stateCode)) {
    selectedStates.value = selectedStates.value.filter(s => s !== stateCode);
  } else {
    selectedStates.value.push(stateCode);
  }
};

// Filter events by search term and selected states
const filteredEvents = computed(() => {
  if (!events.value) return [];
  return filterEvents(
      events.value ?? [],
      locations.value ?? [],
      {
        searchTerm: searchTerm.value,
        selectedStates: selectedStates.value,
        allStates: stateList.value.map(state => state.code)
      });
});

// Group filtered events
const filteredEventGroups = computed<MuseumEventGroupGroup[]>(() => {
  return filteredEvents.value.length > 0 ? eventsGroupedByMonthAndDepartureTime(filteredEvents.value) : [];
});

useSeoMeta({
  title: 'Veranstaltungsliste',
  ogTitle: 'Veranstaltungsliste',
  description: 'Hier finden sich Veranstaltungen und Sonderfahrten von österreichs Museumsbahnen.',
  ogDescription: 'Finde Veranstaltungen und Sonderfahrten von Museumsbahnen in deiner Nähe.',
  ogImage: 'https://museumsbahn-events.at/img/social_media_preview.jpg',
  twitterCard: 'summary_large_image',
})

</script>
