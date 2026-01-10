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
import {isBefore} from 'date-fns';
import {computed} from 'vue';
import { useAllEvents } from '~/composables/eventComposables';

const { data: events } = useAllEvents();

const props = defineProps<{ dateFrom: Date, dateTo?: Date, cardClass?: string }>()

const filteredEvents = computed(() =>
    events?.value?.filter((event) => isBefore(props.dateFrom, event.date)
        && (props.dateTo == null || isBefore(event.date, props.dateTo))) ?? []
)
</script>

<style></style>
