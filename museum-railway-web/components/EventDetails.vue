<style lang="scss">
.event-details-header {
  max-height: 350px;
}

.event-details-header-desktop {
  min-height: 250px;
}

.event-summary {
  @media (min-width: 768px) {
    min-width: 50%;
    max-width: 50%;
  }
}
</style>
<template>
  <div class="bg-mittelgrau border-radius-small overflow-hidden">
    <div v-if="event != undefined" class="flex flex-column gap-3">
      <EventImage :event="event" v-if="viewport.isLessThan('tablet')" class="flex justify-content-end max-dimensions" />
      <div class="p-3 gap-3 flex flex-column">
        <div class="flex flex-row gap-3 event-details-header">
          <div class="flex flex-grow-1 flex-column event-summary">
            <span v-if="event.eventCategory != null">{{ $t(event.eventCategory) }}</span>
            <span><NuxtTime :datetime="event.date" year="numeric" month="numeric" day="numeric" locale="de-AT" timeZone="Europe/Vienna"/></span>
            <h2 class="my-1">{{ event.name }}</h2>
          </div>
          <EventImage v-if="!viewport.isLessThan('tablet')" :event="event" class="border-radius-small overflow-hidden">
          </EventImage>
        </div>
        <div class="flex flex-column">
          <div class="flex align-items-end w-full">
            <div class="flex-grow-1"></div>
            <a :href="event.url" target="_blank" rel="noopener noreferrer"
              class="p-button p-button-outlined font-bold dark-text">
              <span><i class="pi pi-link mr-2"></i>Zur Veranstaltungs Webseite</span>
            </a>
          </div>
          <h3>Veranstalter</h3>
          <div>
            <RouterLink class="p-button p-button-text" v-if="location != null"
              :to="`/locations/${location.locationId}`">{{ location.name }}
            </RouterLink>
            <div class="flex" v-else>
              <Message severity="warn" :closable="false">Es ist Problem beim Laden der Veranstalterinformation
                aufgetreten.</Message>
            </div>
          </div>

          <h3>Beschreibung</h3>
          <div class="mx-2 my-5 text-block">
            {{ event.description }}
          </div>

          <div class="flex align-items-end w-full">
            <div class="flex-grow-1"></div>
            <a :href="event.url" target="_blank" rel="noopener noreferrer"
              class="p-button p-button-outlined font-bold dark-text">
              <span><i class="pi pi-link mr-2"></i>Zur Veranstaltungs Webseite</span>
            </a>
          </div>
        </div>
      </div>
    </div>
    <div class="p-5 bg-white" v-else>
      <h2>{{ noEventSelectedPlaceholderText }}</h2>
    </div>
  </div>
</template>
<script setup lang="ts">
import { computed } from 'vue';
import EventImage from './EventImage.vue';

useI18n();

const props = withDefaults(defineProps<{
  event: MuseumEvent | undefined,
  noEventSelectedPlaceholderText?: string | undefined,
}>(), {
  noEventSelectedPlaceholderText: 'Keine Veranstaltung ausgewählt!',
});
const viewport = useViewport();

const location = computed(() => props.event?.location)
</script>