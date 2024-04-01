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
      :center="DEFAULT_CENTER"
      :zoom="DEFAULT_ZOOM"
  >
    <LTileLayer
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        attribution="&amp;copy; <a href=&quot;https://www.openstreetmap.org/&quot;>OpenStreetMap</a> contributors"
        layer-type="base"
        name="OpenStreetMap"
    />
    <LMarker v-for="location in locationsWithCoords" :lat-lng="getLocationLatLng(location)">
      <LPopup>
        <div class="flex flex-column"> 
        <h3 class="m-1">{{location.name}}</h3>
        <p class="m-1">
        {{location.type}}
        </p>
        <div class="flex justify-content-end">
        <Button  @click="openLocationDetails(location.locationId)" text>
                    Details
        </Button>
      </div>
        </div>
      </LPopup>
    </LMarker>
  </LMap>
</template>
<script setup lang="ts">
import type { MuseumLocation } from '@museumrailwayevents/museum-railway-client';

const DEFAULT_CENTER = [47.609541, 13.784662];
const DEFAULT_ZOOM = 7;
const props = withDefaults(defineProps<{
  locations: MuseumLocation[],
  highlightedLocation?: MuseumLocation | undefined,
}>(), {
  highlightedLocation: undefined,
});
const locationMap = ref();
const route = useRoute();
const router = useRouter();
const locationIdParam = route?.params?.locationId;

const locationsWithCoords = computed(() => props.locations.filter((loc: MuseumLocation) => {
      let isLocation = true;
      if (locationIdParam != undefined) {
        isLocation = loc.locationId === locationIdParam;
      }
      return isLocation && loc.location.lat != null && loc.location.lon != null;
    }))

watch(() => locationMap?.value?.ready, () => updateMapData());
watch(() => props.locations, () => updateMapData());
watch(() => props.highlightedLocation, () => updateMapData());

onMounted(initMap);

function initMap() {
  updateMapData();
}

function openLocationDetails(locationId: string) {
  router.push({name: 'locationDetails', params: {locationId}});
}

function updateMapData() {
  if (locationIdParam != undefined || props.highlightedLocation != undefined) {
    let location = props.highlightedLocation;
    if (locationIdParam != undefined) {
      location = props.locations.filter((location) => location.locationId === locationIdParam)[0];
    }
    if (location != undefined && locationMap?.value?.leafletObject != undefined) {
      locationMap.value.leafletObject.setView({lat: location.location.lat, lng: location.location.lon}, 10);
    }
  }
}

function getLocationLatLng(location: MuseumLocation) {
  // at this point locations without coordinates should already have been filtered out
  return [location.location.lat ?? 0, location.location.lon ?? 0]
}
</script>