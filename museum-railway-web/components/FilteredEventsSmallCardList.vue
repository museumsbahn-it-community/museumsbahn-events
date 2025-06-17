<template>
  <div :class="cardClass" v-for="eventEntry in filteredEvents" v-if="filteredEvents.length > 0">
    <EventCardSmall :event="eventEntry">
    </EventCardSmall>
  </div>
  <div :class="`${cardClass} flex h-full w-full bg-grauweiß border-radius-small`" v-else>
    <h2>Keine Veranstaltungen in diesem Zeitraum gefunden.</h2>
  </div>
</template>

<script lang="ts" setup>
import { isBefore } from 'date-fns';
import { computed } from 'vue';
import { useEvents } from '~/composables/useEvents';
import { useLocations } from '~/composables/useLocations';

// Use composables instead of Pinia stores
const { allLocations } = useLocations();
const { filteredEvents: allFilteredEvents } = useEvents();

const props = defineProps<{ dateFrom: Date, dateTo?: Date, cardClass?: string }>()

const filteredEvents = computed(() =>
  allFilteredEvents.value.filter((event) => isBefore(props.dateFrom, event.date) 
    && (props.dateTo == null || isBefore(event.date, props.dateTo)))
)
</script>

<style></style>
