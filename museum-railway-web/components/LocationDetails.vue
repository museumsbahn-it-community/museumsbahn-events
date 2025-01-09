<template>
  <div class="mx-2 mt-2">
    <div class="bg-mittelgrau p-3 lg:p-5 border-radius-small">
      <card v-if="museumLocation != undefined" class="p-5">
        <template #header>
          <h2>{{ museumLocation.name }}</h2>
        </template>
        <template #content>
          <div class="flex align-items-center mb-4">
            <span class="material-icons-outlined">location_on</span> {{ museumLocation.location.city }}
          </div>

          <div class="my-4">
            <EventCollectorIndicator :location="museumLocation"></EventCollectorIndicator>
          </div>

          <div class="mx-2 my-4">
            {{ museumLocation.description }}
          </div>

          <div class="flex align-items-end w-full">
            <div class="flex-grow-1"></div>
            <a :href="museumLocation.webUrl" target="_blank" rel="noopener noreferrer"
               class="p-button p-button-text font-bold dark-text">Zur Museums Webseite</a>
          </div>

        </template>
      </card>
    </div>

    <h2>Künftige Veranstaltungen</h2>

    <div v-if="museumLocation != undefined" class="mb-4">
      <EventList
          :events="events"
          @eventSelected="navigateToEventDetails"
      >
      </EventList>
    </div>
  </div>
</template>
<script setup lang="ts">
import {eventKey} from "~/model/util.ts";
import type {MuseumEventGroup} from "~/stores/EventsStore.ts";

const router = useRouter();
const props = defineProps<{
  museumLocation: MuseumLocation,
  events: MuseumEventGroup[],
}>();

function navigateToEventDetails(value: string) {
  router.push({name: 'eventDetails', params: {eventKey: eventKey(value)}});
}
</script>