<template>
  <div class="flex align-items-center text-sm my-2">
    <i class="pi pi-clock ml-1 mr-2"/> {{ formatDate(museumEvent?.date) }}
  </div>
  <div class="flex align-items-center text-sm my-2" v-if="museumEvent.location">
    <span class="material-icons-outlined mr-1">train</span> {{ museumEvent.location.name }}</div>

  <div class="flex align-items-center text-sm my-2" v-if="museumEvent.location">
    <i class="pi pi-map-marker ml-1 mr-2"/>{{ museumEvent.location.location.city }}, {{
      museumEvent.location.location.state
    }}
  </div>

</template>
<script setup lang="ts">
import type { MuseumEvent } from '~/model/museumEvent';

const props = defineProps<{
  museumEvent: MuseumEvent,
}>();
</script>