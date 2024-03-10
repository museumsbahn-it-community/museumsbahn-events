<style lang="scss">
@use "../assets/variables_impl" as variables;

.map-container {
  border-radius: variables.$border-radius-small;
}
</style>
<template>
  <LMap
      class="h-full w-full map-container"
      ref="locationMap"
      :center="[47.609541, 13.784662]"
      :zoom="7">
    <LTileLayer
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        attribution="&amp;copy; <a href=&quot;https://www.openstreetmap.org/&quot;>OpenStreetMap</a> contributors"
        layer-type="base"
        name="OpenStreetMap"
    />
    <LMarker v-for="location in locationsWithCoords" :lat-lng="getLocationLatLng(location)">
      <LPopup>
        <b>{{location.name}}</b><br>{{location.type}}<br>
      </LPopup>
    </LMarker>
  </LMap>
</template>
<script setup lang="ts">
import type { MuseumLocation } from '@museumrailwayevents/museum-railway-client';

const props = defineProps<{
  locations: MuseumLocation[],
  highlightedLocation: MuseumLocation | undefined,
}>();
const locationMap = ref();
const route = useRoute();

const locationIdParam = route?.params?.locationId;

const locationsWithCoords = computed(() => props.locations.filter((loc: MuseumLocation) => {
      let isLocation = true;
      if (locationIdParam != undefined) {
        isLocation = loc.locationId === locationIdParam;
      }
      return isLocation && loc.location.lat != null && loc.location.lon != null;
    }))

watch(() => props.locations, () => updateMapData());
watch(() => props.highlightedLocation, () => updateMapData());

onMounted(initMap);

function initMap() {
  updateMapData();
}

function updateMapData() {
  if (locationIdParam != undefined || props.highlightedLocation != undefined) {
    let location = props.highlightedLocation;
    if (locationIdParam != undefined) {
      location = props.locations.filter((location) => location.locationId === locationIdParam)[0];
    }
    if (location != undefined) {
      // locationMap.setView({lat: location.location.lat, lng: location.location.lon}, 10);
    }
  }
}

function getLocationLatLng(location: MuseumLocation) {
  // at this point locations without coordinates should already have been filtered out
  return [location.location.lat ?? 0, location.location.lon ?? 0]
}
</script>