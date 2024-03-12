<script setup lang="ts">

withDefaults(defineProps<{
  museumEvent: MuseumEvent | undefined,
  noEventSelectedPlaceholderText?: string | undefined,
}>(), {
  noEventSelectedPlaceholderText: 'Keine Veranstaltung ausgewählt!',
});

</script>
<template>
  <div class="bg-verkehrsrot rounded-corners-small p-3 h-full">
    <div v-if="museumEvent != undefined" class="p-4 h-full bg-white">
        <h2 class="my-1">{{ museumEvent.name }}</h2>
        <div class="my-2 flex flex-column">
          <div class="flex align-items-center text-sm my-2">
            <i class="pi pi-clock mr-2"/> {{ formatDate(museumEvent?.date) }}
          </div>
          <div class="flex align-items-center text-sm my-2">
            <i class="pi pi-map-marker mr-2"/> {{ museumEvent.location.name }} -
            {{ museumEvent.location.location.city }}
          </div>

          <ScrollPanel class="flex-shrink-1" style="height:80%">
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