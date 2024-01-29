<template>
  <div
      ref="mapContainer"
      class="h-full w-full map-container"
  >
    Location Map
  </div>
</template>
<script setup lang="ts">
import type { MuseumLocation } from '@museumrailwayevents/museum-railway-client';

const props = defineProps<{
  locations: MuseumLocation[],
  highlightedLocation: MuseumLocation | undefined,
}>();
const mapContainer = ref();
const route = useRoute();
let locationMap = undefined;

const locationIdParam = route?.params?.locationId;

watch(() => props.locations, () => updateMapData());
watch(() => props.highlightedLocation, () => updateMapData());

onMounted(initMap);

function initMap() {
  const options: L.MapOptions = {
    center: L.latLng(47.609541, 13.784662),
    zoom: 7,
  };
  locationMap = L.map(mapContainer.value, options);
  L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
  }).addTo(locationMap);

  updateMapData();
}

function updateMapData() {
  if (locationIdParam != undefined || props.highlightedLocation != undefined) {
    let location = props.highlightedLocation;
    if (locationIdParam != undefined) {
      location = props.locations.filter((location) => location.locationId === locationIdParam)[0];
    }
    if (location != undefined) {
      locationMap.setView(L.latLng(location.location.lat, location.location.lon), 10);
    }
  }

  props.locations.forEach((location) => {
    const lat = location.location.lat;
    const lon = location.location.lon;

    if (lat != undefined && lon != undefined) {
      const marker = L.marker([lat, lon]);
      let mapPopupTemplate: any = `<b>${location.name}</b><br>${location.type}<br>`;
      marker.bindPopup(mapPopupTemplate);
      marker.addTo(locationMap);
    }
  });
}
</script>