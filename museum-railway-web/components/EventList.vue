<style lang="scss">
.event-card {
  max-width: 400px;
}

.event-card-flex-basis {
  flex-basis: 400px;
}
</style>
<template>
  <div v-if="events.length > 0" class="flex flex-column">
    <div v-for="eventGroup in events" class="bg-grauweiß mb-4 w-full">
      <h1 class="my-2">{{ eventGroup.label }}</h1>
      <div class="flex flex-row flex-wrap w-full justify-content-center">
        <div v-for="event in eventGroup.events"
          class="mb-2 md:m-2 flex flex-grow-1 justify-content-center event-card-flex-basis">
          <EventCard :event="event"></EventCard>
        </div>
      </div>
    </div>
  </div>

  <div v-else>
    <div class="w-11 flex align-items-center my-2">
      <h2 class="mx-8 my-2 align-content-center">Keine Veranstaltungen gefunden!</h2>
    </div>
  </div>
</template>
<script setup lang="ts">
import type { MuseumEventGroup } from "~/stores/EventsStore.ts";
const EVENT_SELECTED_TOKEN = 'eventSelected';
const emit = defineEmits([EVENT_SELECTED_TOKEN]);
const props = defineProps<{
  events: MuseumEventGroup[],
}>();
</script>