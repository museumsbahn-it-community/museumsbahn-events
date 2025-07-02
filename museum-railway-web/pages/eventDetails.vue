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
}
</style>

<template>
    <div class="event-details w-full grid m-0 p-2 md:p-0">
        <div class="xl:col-4 flex flex-1 content-left-column" v-if="viewport.isGreaterOrEquals('desktop-xl')"></div>
        <div class="col-12 md:col-8 xl:col-6 content-center-column m-0 md:m-2 mb-5 p-0">
            <EventDetails :event="selectedEvent" :no-event-selected-placeholder-text="noEventSelectedPlaceholderText">
                Event Details
            </EventDetails>
            <CustomSidebar class="mt-2 w-full" v-if="viewport.isLessThan('tablet')"
                :title="`Weitere Veranstaltungen von ${location?.name}`" side="center">
                <div class="flex flex-column gap-2">
                    <EventCardSmall v-for="eventEntry in eventsForSameLocation" :event="eventEntry">
                    </EventCardSmall>
                </div>
            </CustomSidebar>
        </div>
        <div class="col-2 flex flex-1 content-right-column" v-if="viewport.isGreaterOrEquals('tablet')">
            <div class="w-full flex flex-column justify-content-center align-items-end overflow-hidden">
                <CustomSidebar class="w-full lg:w-11" style="height: 80%;"
                    :title="`Weitere Veranstaltungen von ${location?.name}`" side="right">
                    <div class="flex flex-column gap-2">
                        <EventCardSmall v-for="eventEntry in eventsForSameLocation" :event="eventEntry">
                        </EventCardSmall>
                    </div>
                </CustomSidebar>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import EventCardSmall from '~/components/EventCardSmall.vue';
import EventDetails from '~/components/EventDetails.vue';
import { useAllEvents } from '~/composables/eventComposables';
import { useAllLocations } from '~/composables/locationComposables';
import { getLocationById } from '~/composables/locationDataFunctions';
import { eventKey } from '~/model/util';
import type {MuseumEvent} from "~/apiModel/apiModel";
import CustomSidebar from "~/components/CustomSidebar.vue";

const route = useRoute();
const viewport = useViewport();
const locations = useAllLocations().data
const events = useAllEvents().data

const eventKeyParam = route?.params?.eventKey as string;
const selectedEvent = computed(() => {
  const event = getEventByKey(eventKeyParam);
  if(import.meta.server && event == null) {
    // only throw 404 on server to satisfy crawlers, but on client it can take some time to load the data
    throw createError({
      statusCode: 404,
      statusMessage: 'Kein Event unter dieser Adresse gefunden! Vielleicht liegt die Veranstaltung bereits in der Vergangenheit?'
    })
  }
  return event;
});
const locationId = computed(() => selectedEvent.value?.locationId);
const location = computed(() => locationId.value == null ? null : getLocationById(locations.value ?? [], locationId.value));


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
    const result = events.value?.filter((it)=> eventKey(it) == eventKeyParam)
    if (result == null || result?.length === 0) {
        return null;
    }
    return result[0];
}
</script>
