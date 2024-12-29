<style lang="scss">
@use "../assets/colors" as colors;
@use "../assets/variables_impl" as variables;

.event-card-small {
  overflow: hidden;
  border-radius: variables.$border-radius-small;
}

a.event-card-small {
  text-decoration: none;
  color: inherit;
}

.event-card-image {
  min-width: 100px;
  width: 100px;
  min-height: 20%;
  height: auto;
}

.event-title {
  overflow-wrap: anywhere;
}
</style>
<template>
  <RouterLink :to="`/eventDetails/${eventKey(event)}`" class="flex flex-1 event-card-small">
    <div class="w-full bg-grauweiß flex flex-row event-card-small">
      <!-- image -->
      <div v-if="event.pictureUrl" class="event-card-image">
        <Image :src="imgSource" :alt="imgAltText" class="h-full"
          image-class="h-full object-fit-cover" />
      </div>
      <!-- summary -->
      <div class="flex flex-column justify-content-center p-2">
        <div class="m-1">{{ formatDate(event.date) }}</div>
        <h2 class="event-title m-1">{{ event.name }}</h2>
      </div>
    </div>
  </RouterLink>
</template>
<script setup lang="ts">
import { computed } from 'vue';
import { getAltTextOrDefault, getImageUrlOrDefault } from '~/composables/eventImage';
import { eventKey } from '~/model/util.ts';

const props = defineProps<{
  event: MuseumEvent
}>()

const imgSource = computed(() => getImageUrlOrDefault(props.event?.pictureUrl));
const imgAltText = computed(() => getAltTextOrDefault(props.event?.pictureAltText));
</script>