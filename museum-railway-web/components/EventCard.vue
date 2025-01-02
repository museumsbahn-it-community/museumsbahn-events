<style lang="scss">
@use "../assets/colors" as colors;
@use "../assets/variables_impl" as variables;

.event-card {
  overflow: hidden; // needed for border radius clipping
}

a.event-card {
  text-decoration: none;
  color: inherit;
}

.event-title {
  overflow-wrap: anywhere;
}

.event-info-corner {
  position: absolute;
  border-bottom-right-radius: variables.$border-radius-small;
  border-top-left-radius: variables.$border-radius-small;
  z-index: 1;
}
</style>
<template>
  <RouterLink :to="`/eventDetails/${eventKey(event)}`" class="flex-grow-1 event-card">
    <div class="w-full h-full bg-mittelgrau flex flex-column border-radius-small event-card">
      <!-- info corner -->
      <div class="bg-mittelgrau flex flex-column p-2 event-info-corner">
        <span v-if="event.eventCategory != null">{{ $t(event.eventCategory) }}</span>
        <span>{{ formatDate(event.date) }}</span>
      </div>
      <!-- image -->
      <div class="flex-shrink-1">
        <EventImage :event="event"></EventImage>
      </div>
      <!-- summary -->
      <div class="my-2 mx-4 flex-grow-1">
        <div class="flex align-items-center text-sm my-2">{{ event?.location?.name }}</div>
        <h2 class="event-title">{{ event.name }}</h2>
        <div class="flex align-items-center text-sm my-2" v-if="event.location">
          <i class="pi pi-map-marker ml-1 mr-2" />{{ event.location.location.city }}
        </div>
      </div>
    </div>
  </RouterLink>
</template>
<script setup lang="ts">
import { eventKey } from '~/model/util.ts';
import { formatDate } from '~/composables/formatDate';
import { computed } from 'vue';
import { getImageUrlOrDefault } from '~/composables/eventImage';
import EventImage from './EventImage.vue';

const props = defineProps<{
  event: MuseumEvent
}>()

useI18n();
const imgSource = computed(() => getImageUrlOrDefault(props.event?.pictureUrl))
</script>