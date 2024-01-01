<template>
  <div class="flex flex-row h-full">
    <ScrollPanel class="flex-column h-full w-4">

      <div v-for="location in locations">
        <Panel collapsed toggleable>
          <template #header>
            <div class="flex flex-column w-8">
                <span class="font-bold">
                {{ location.name }}
                </span>
              <div class="flex align-items-center m-1 text-sm">
                <span class="material-icons-outlined">location_on</span> {{ location.location.city }},
                {{ location.location.state }}
              </div>
            </div>
          </template>
          <template #icons>
            <button class="p-panel-header-icon p-link mr-2">
              <span class="pi pi-info-circle"></span>
            </button>
            <button class="p-panel-header-icon p-link mr-2">
              <span class="pi pi-map"></span>
            </button>
          </template>
          <p>
            {{ location.description }}
            <i class="pi pi-globe"/> <a href="{{location.webUrl}}">{{ location.webUrl }}</a>
          </p>
        </Panel>
        <div class="h-1rem"/>
      </div>
    </ScrollPanel>
    <div class="flex-grow h-full w-full ml-5">
      <div
          ref="mapContainer"
          class="flex-grow-1 h-full w-full map-container"
      >
        map
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { MuseumLocation } from 'museum-railway-api-client';
import { onMounted, ref } from 'vue';
import { latLng, map, MapOptions, marker, tileLayer } from 'leaflet';
import { useLocationsStore } from '../store/LocationsStore.ts';

const locationsStore = useLocationsStore();
const locations = ref<MuseumLocation[]>([]);
const mapContainer = ref();

onMounted(mounted);

async function mounted(): Promise<void> {
  console.log('locations component mounted');
  const options: MapOptions = {
    center: latLng(47.609541, 13.784662),
    zoom: 7,
  };
  const locationMap = map(mapContainer.value, options);
  tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
  }).addTo(locationMap);

  await locationsStore.loadEvents();
  locations.value = locationsStore.allLocations;

  locations.value.forEach((location) => {
    const lat = location.location.lat;
    const lon = location.location.lon;

    if (lat != undefined && lon != undefined) {
      marker([lat, lon]).addTo(locationMap);
    }
  });
}
</script>
