<style>
.event-details-image {
  height: 200px;
}
</style>
<template>
  <div class="bg-verkehrsrot rounded-corners-small p-3 lg:h-full">
    <div v-if="museumEvent != undefined" class="p-4 lg:h-full bg-white">
      <div class="w-full flex flex-column md:flex-row">
        <div class="w-full" :class="{ 'md:w-6': museumEvent.pictureUrl}">
          <h2 class="my-1">{{ museumEvent.name }}</h2>
          <div class="my-2">
            <EventSummary v-if="museumEvent" :museum-event="museumEvent"></EventSummary>
          </div>
        </div>
        <Image
            v-if="museumEvent.pictureUrl"
            :src="`/imgcache?url=${museumEvent.pictureUrl}`"
            alt="kein alt text vorhanden"
            class="w-full md:w-6 p-3 md:p-0 event-details-image diagonal-box"
            image-class="h-full w-full object-fit-cover"/>
      </div>
      <div class="my-2 flex flex-column" style="height:80%">
        <ScrollPanel class="flex-shrink-1 w-full" style="height:80%">
          <div class="mx-2 my-5 text-block">
            {{ museumEvent.description }}
          </div>
        </ScrollPanel>

        <div class="flex align-items-end w-full">
          <div class="flex-grow-1"></div>
          <a :href="museumEvent.url" target="_blank" rel="noopener noreferrer"
             class="p-button p-button-text font-bold dark-text">Zur Veranstaltungs Webseite</a>
        </div>
      </div>
    </div>
    <div class="p-5 bg-white" v-else>
      <h2>{{ noEventSelectedPlaceholderText }}</h2>
    </div>
  </div>
</template>
<script setup lang="ts">
import EventSummary from '~/components/EventSummary.vue';
import type { MuseumEvent } from '~/model/museumEvent';

withDefaults(defineProps<{
  museumEvent: MuseumEvent | undefined,
  noEventSelectedPlaceholderText?: string | undefined,
}>(), {
  noEventSelectedPlaceholderText: 'Keine Veranstaltung ausgewählt!',
});

</script>