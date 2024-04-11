<style lang="scss">
@use "../assets/colors" as colors;
@use "../assets/variables_impl" as variables;

/* hide the opposite side of the timeline */
.p-timeline-event-opposite {
  display: none;
}

.timeline-heading {
  color: colors.$text-color-dark;
}

.p-timeline-event-marker {
  width: 1.25rem;
  height: 1.25rem;
  background: colors.$text-color-light;
  border: 2px solid colors.$text-color-dark;
}

.p-timeline-event-connector {
  background: colors.$text-color-dark;
}

.p-timeline.p-timeline-vertical .p-timeline-event-connector {
  width: 4px;
}

.event-list-image {
  width: 200px;
}

.event-title {
  overflow-wrap: anywhere;
}

.event-card-no-details {
  min-height: 200px;
  @media screen and (min-width: variables.$md) {
    width: 41.6667%;
  }
  @media screen and (min-width: variables.$lg) {
    max-width: 700px;
  }
}

.event-card-with-details {
  min-height: 200px;
  @media screen and (min-width: variables.$lg) {
    width: 41.6667%;
  }
  @media screen and (min-width: variables.$xl) {
    max-width: 700px;
  }
}
</style>
<template>
  <div class="bg-verkehrsrot rounded-corners-small px-3 lg:px-5">
    <div class="flex flex-grow-1 min-h-0 w-full">
      <div v-if="events.length > 0" class="w-full">
        <div v-for="eventGroup in events" class="mb-4">
          <div class="flex flex-row my-4">
            <div class="flex timeline-heading align-items-center my-2">
              <h1 class="mx-6 my-2">{{ eventGroup.label }}</h1>
            </div>
          </div>
          <div class="flex flex-row flex-wrap">
            <div v-for="event in eventGroup.events"
                 class="event-card-no-details m-4 flex-grow-1 w-full bg-grauweiss cursor-pointer flex flex-row"
                 @click="selectEvent(event)" :class="{ selected: isHighlighted(event) }">
              <div v-if="viewport.isGreaterOrEquals('tablet')"
                   class="bg-umbragrau min-h-full light-text p-2 flex flex-column justify-content-center">
                <h3 class="m-1">{{ formatDayMon(event.date) }}</h3>
                <h1 class="m-1">{{ formatYear(event.date) }}</h1>
              </div>
              <div class="my-2 mx-4 flex-grow-1">
                <h2 class="event-title">{{ event.name }}</h2>
                <EventSummary :museum-event="event"></EventSummary>
              </div>
              <div v-if="event.pictureUrl && shouldDisplayEventPicture()"
                   class="min-h-full event-list-image flex-shrink-0">
                <Image :src="`/imgcache?url=${event.pictureUrl}`" alt="kein alt text vorhanden" class="h-full w-full"
                       image-class="h-full w-full object-fit-cover"/>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-else>
        <div class="w-11 flex timeline-heading align-items-center my-2">
          <h2 class="mx-8 my-2 align-content-center">Keine Veranstaltungen gefunden!</h2>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import {eventKey} from '~/model/util.ts';
import {formatDayMon, formatYear} from '../composables/formatDate.ts';
import type {MuseumEventGroup} from "~/stores/EventsStore.ts";

const EVENT_SELECTED_TOKEN = 'eventSelected';
const emit = defineEmits([EVENT_SELECTED_TOKEN]);
const props = withDefaults(defineProps<{
  events: MuseumEventGroup[],
  highlightedEvent: MuseumEvent | undefined,
}>(),
    {
      highlightedEvent: undefined,
    }
);
const viewport = useViewport();

function shouldDisplayEventPicture(): boolean {
  return viewport.isGreaterOrEquals('tablet') && !hasSelectedEvent() || viewport.isGreaterOrEquals('desktop-xxl')
}

function hasSelectedEvent(): boolean {
  return props.highlightedEvent != undefined;
}

function isHighlighted(value: MuseumEvent): boolean {
  return props.highlightedEvent != undefined && eventKey(value) == eventKey(props.highlightedEvent);
}

function selectEvent(value: MuseumEvent): void {
  emit(EVENT_SELECTED_TOKEN, value);
}
</script>