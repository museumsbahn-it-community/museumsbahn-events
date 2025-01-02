<style>
.p-card-title {
  font-size: 1.25rem;
}

.banner-image > img {
  height: 7rem;
  width: 100%;
  object-fit: cover;
}

.header-icon {
  font-size: 1.75rem;
}

.collectorIndicatorBar {
  width: 8px;
}

</style>
<template>
  <div class="fill-page-height p-3">
    <div class="flex flex-row h-full bg-verkehrsrot p-3 border-radius-small">
      <ScrollPanel class="flex-column h-full lg:w-6 xxl:w-4 w-full">
        <div v-for="location in allLocations">
          <Card>
            <template #title>
              <div class="flex flex-row mx-3 mt-3">
                <span class="w-8 overflow-wrap-anywhere">
                {{ location.name }}
                </span>
                <div class="flex-grow-1"></div>
                <div>
                  <button class="p-link mx-2" @click="showLocationOnMap(location)">
                    <span class="header-icon pi pi-map"></span>
                  </button>
                  <button class="p-link" @click="openLocationDetails(location.locationId)">
                    <span class="header-icon pi pi-info-circle"></span>
                  </button>
                </div>
              </div>
            </template>
            <template #content>
              <div class="mx-3 mb-3">
                <!--              <Image class="banner-image" :src="imageLocation(location.locationId)"></Image>-->
                <div class="flex align-items-center text-sm my-2">
                  <span class="material-icons-outlined">location_on</span> {{ location.location.city }},
                  {{ location.location.state }}
                </div>
                <div class="my-4">
                  <span>{{ eventCounts[location.locationId] }} Veranstaltungen gefunden</span>
                </div>
                <div class="flex align-items-end w-full">
                  <div class="flex-grow-1"></div>
                  <a :href="location.webUrl" target="_blank" rel="noopener noreferrer"
                     class="p-button p-button-text font-bold dark-text">Zur Museums Webseite</a>
                </div>
              </div>
            </template>
          </Card>
          <div class="h-1rem"/>
        </div>
      </ScrollPanel>
      <div class="flex-grow h-full w-full ml-5" v-if="viewport.isGreaterThan('tablet')">
        <LocationMap :locations="allLocations" :highlighted-location="highlightedLocation"></LocationMap>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAsyncData, useLazyAsyncData, useRouter } from 'nuxt/app';
import { computed, ref } from 'vue';
import { useEventsStore } from '~/stores/EventsStore';
import { useLocationsStore } from '~/stores/LocationsStore';
import { storeToRefs } from 'pinia';
const locationsStore = useLocationsStore();
const eventsStore = useEventsStore();
await useLazyAsyncData('locations', () => locationsStore.fetchLocations());
await useAsyncData('events', () => eventsStore.fetchAllEvents());

const viewport = useViewport();
const {allLocations} = storeToRefs(locationsStore);
const {eventCountForLocationId} = storeToRefs(eventsStore)
const eventCounts = computed(() => {
  const eventCountMap: {[key: string]: number} = {};
  allLocations.value.forEach(location => {
    const locationId = location.locationId;
    const eventCount = eventCountForLocationId.value(locationId)
    eventCountMap[locationId] = eventCount;
  });
  return eventCountMap
})

const highlightedLocation = ref<MuseumLocation | undefined>(undefined);
const router = useRouter();

function openLocationDetails(locationId: string) {
  router.push({name: 'locationDetails', params: {locationId}});
}

function showLocationOnMap(location: MuseumLocation) {
  highlightedLocation.value = location;

  if (!viewport.isGreaterThan('tablet')) {
    router.push({name: 'locationMapDetails', params: {locationId: location.locationId}});
  }
}
</script>
