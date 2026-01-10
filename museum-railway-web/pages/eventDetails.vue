<style lang="scss">
@use "../assets/variables_impl.scss" as variables;

.content-left-column,
.content-right-column {
  flex: auto 2 1;
  padding: 0;
  overflow: hidden;
  height: variables.$min-content-height;
}

.event-details {
  top: calc(variables.$navbar-height);
  min-height: calc(100vh - variables.$navbar-height);
}
</style>

<template>
  <div class="event-details w-full h-full align-items-center flex flex-column p-2">
    <div class="w-full flex flex-column xl:w-8 mb-5 gap-4">
      <EventDetails :event="selectedEvent" :no-event-selected-placeholder-text="noEventSelectedPlaceholderText">
        Event Details
      </EventDetails>
      <card v-if="selectedEvent != null" class="w-full">
        <template #title>Künftige Veranstaltungen</template>
        <template #content>
          <div class="flex flex-column gap-3">
            <div v-if="museumLocation?.eventListUrl" class="flex align-items-center">
              <span class="material-symbols-outlined">calendar_month</span>
              <a
                  :href="museumLocation.eventListUrl" target="_blank" rel="noopener noreferrer"
                  class="p-button p-button-text font-bold dark-text">Veranstaltungsliste des Museums</a>
            </div>
            <!-- we have to build a custom event list here, because the normal event list does a grouping, which we don't need -->
            <div
                v-for="eventEntry in eventsForSameLocation"
                :key="eventKey(eventEntry)"
            >
              <EventCardSmall :event="eventEntry"/>
            </div>
          </div>
        </template>
      </card>
    </div>
  </div>
  <div class="default-footer"></div>
</template>

<script setup lang="ts">
import {computed} from 'vue';
import {useRoute} from 'vue-router';
import EventDetails from '~/components/EventDetails.vue';
import {useAllEvents} from '~/composables/eventComposables';
import {useAllLocations} from '~/composables/locationComposables';
import {getLocationById} from '~/composables/locationDataFunctions';
import {eventKey} from '~/model/util';
import type {MuseumEvent} from "~/apiModel/apiModel";

const route = useRoute();
const viewport = useViewport();
const locations = useAllLocations().data
const events = useAllEvents().data

const eventKeyParam = route?.params?.eventKey as string;
const selectedEvent = computed(() => {
  const event = getEventByKey(eventKeyParam);
  if (import.meta.server && event == null) {
    // only throw 404 on server to satisfy crawlers, but on client it can take some time to load the data
    throw createError({
      statusCode: 404,
      statusMessage: 'Kein Event unter dieser Adresse gefunden! Vielleicht liegt die Veranstaltung bereits in der Vergangenheit?'
    })
  }
  return event;
});
const locationId = computed(() => selectedEvent.value?.locationId);
const museumLocation = computed(() => locationId.value == null ? null : getLocationById(locations.value ?? [], locationId.value));

const eventsForSameLocation = computed(() => {
  const locId = locationId.value;
  if (locId == null) {
    return [];
  }

  return eventsForLocationId(events.value ?? [], locId);
});

const noEventSelectedPlaceholderText = "Leider konnte die Veranstaltung nicht gefunden werden.";

useSeoMeta({
  title: () => `Veranstaltung | ${selectedEvent.value?.name}`,
  ogTitle: () => `Details zur Veranstaltung ${selectedEvent.value?.name}`,
  description: () => `${selectedEvent.value?.description}`,
  ogDescription: () => `${selectedEvent.value?.description}`,
  ogImage: () => selectedEvent.value?.pictureUrl != null ? `https://museumsbahn-events.at/imgcache?url=${selectedEvent.value?.pictureUrl}` : `https://museumsbahn-events.at/img/social_media_preview.jpg`,
  twitterCard: 'summary_large_image',
});

function getEventByKey(eventKeyParam: string): MuseumEvent | null {
  const result = events.value?.filter((it) => eventKey(it) == eventKeyParam)
  if (result == null || result?.length === 0) {
    return null;
  }
  return result[0];
}
</script>
