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
    <div class="flex flex-row h-full bg-verkehrsrot p-3 rounded-corners-small">
      <ScrollPanel class="flex-column h-full lg:w-4 w-full">
        <div v-for="location in locations">
          <Card>
            <template #title>
              <div class="flex flex-row mx-3 mt-3">
                <span class="w-8">
                {{ location.name }}
                </span>
                <div class="flex-grow-1"></div>
                <div>
                  <button class="p-link mx-2 md:mx-3" @click="showLocationOnMap(location)">
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
                  <EventCollectorIndicator :location="location"></EventCollectorIndicator>
                </div>
                <div class="my-4">
                  <span>{{ eventCount(location.locationId) }} Veranstaltungen gefunden</span>
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
        <LocationMap :locations="locations" :highlighted-location="highlightedLocation"></LocationMap>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Colors } from '~/app/colors.ts';
import EventCollectorIndicator from '~/components/EventCollectorIndicator.vue';

const viewport = useViewport();
const locationsStore = useLocationsStore();
const eventsStore = useEventsStore();
const locations = ref<MuseumLocation[]>([]);
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

function eventCount(locationId: string): number {
  return eventsStore.filteredEvents(undefined, undefined, undefined, locationId).length;
}



// TODO: add image locations at later date
// const imageLocation = (locationId: string): string => `/img/locations/${locationId}.jpg`

onMounted(mounted);

async function mounted(): Promise<void> {
  await eventsStore.loadEventsWithLocations();
  locations.value = locationsStore.allLocations;
}
</script>
