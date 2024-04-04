<template>
  <div class="mx-2">
    <div class="bg-verkehrsrot p-3 lg:p-5 rounded-corners-small">
      <card v-if="museumLocation != undefined" class="p-5">
        <template #header>
          <h2>{{ museumLocation.name }}</h2>
        </template>
        <template #content>
          <div class="flex align-items-center mb-4">
            <span class="material-icons-outlined">location_on</span> {{ museumLocation.location.city }}
          </div>

          <div class="my-4">
            <EventCollectorIndicator :location="museumLocation"></EventCollectorIndicator>
          </div>

          <div class="mx-2 my-4">
            {{ museumLocation.description }}
          </div>

          <div class="flex align-items-end w-full">
            <div class="flex-grow-1"></div>
            <a :href="museumLocation.webUrl" target="_blank" rel="noopener noreferrer"
               class="p-button p-button-text font-bold dark-text">Zur Museums Webseite</a>
          </div>

        </template>
      </card>
    </div>

    <h2>Künftige Veranstaltungen</h2>

    <div v-if="museumLocation != undefined" class="mb-4">
      <EventList :location-id-filter="museumLocation.locationId"
                 :show-details-button="false"
                 :show-filters="false">
      </EventList>
    </div>
  </div>
</template>
<script setup lang="ts">
import type { MuseumLocation } from '@museumrailwayevents/museum-railway-client';

const eventsStore = useEventsStore();
const locationsStore = useLocationsStore();
const route = useRoute();

const locationIdParam = route?.params?.locationId;
const museumLocation = ref<MuseumLocation | undefined>(undefined);

onMounted(mounted);

async function mounted(): Promise<void> {
  // unfortunately it is not possible to do this in the outer component reliably, thus we must load the data here, because
  // we have to run the locationById getter AFTER loading the data
  await eventsStore.loadEventsWithLocations();
  museumLocation.value = locationsStore.locationById(locationIdParam);
}

</script>