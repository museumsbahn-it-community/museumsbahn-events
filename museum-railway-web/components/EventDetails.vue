<script setup lang="ts">

defineProps<{
  museumEvent: MuseumEvent | undefined
}>();

</script>
<template>
  <div>
    <card v-if="museumEvent != undefined" class="p-5">
      <template #header>
        <h2>{{ museumEvent.name }}</h2>
      </template>
      <template #content>
        <div class="flex align-items-center text-sm">
          <i class="pi pi-clock m-1"/> {{ formatDate(museumEvent?.date) }}
        </div>
        <div class="flex align-items-center">
          <span class="material-icons-outlined">location_on</span> {{ museumEvent.location.name }} -
          {{ museumEvent.location.location.city }}
        </div>

        <div class="mx-2 my-5">
          {{ museumEvent.description }}
        </div>

        <div class="flex align-items-end w-full">
          <div class="flex-grow-1"></div>
          <a :href="museumEvent.url" target="_blank" rel="noopener noreferrer"
             class="p-button p-button-text font-bold dark-text">Zur Veranstaltungs Webseite</a>
        </div>

      </template>
    </card>
    <card v-else>Keine Veranstaltung ausgewählt!</card>
  </div>
</template>