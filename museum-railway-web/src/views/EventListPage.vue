<template>

  <ul>
    <li v-for="event in events">{{getEventDate(event["startDate"]).toDateString()}} - {{event["name"]}}</li>
  </ul>


</template>
<script setup lang="ts">

import { ref } from 'vue';
import { Entry, useEventsStore } from '../store/EventsStore';
import { onMounted } from 'vue';

const events = ref<Entry[]>([])

const eventsStore = useEventsStore()

onMounted(mounted);

async function mounted(): Promise<void> {
  await eventsStore.loadEvents();
  events.value = eventsStore.queriedEvents;
}

function getEventDate(value: string): Date {
  return new Date(value)
}
</script>