<template>
  <div
      ref="mapContainer"
      class="h-full w-full map-container"
  >
    Location Map
  </div>
</template>
<script setup lang="ts">
const props = defineProps<{
  locations: MuseumLocation[]
}>()
const mapContainer = ref();

onMounted(mounted);

async function mounted(): Promise<void> {
  const options: L.MapOptions = {
    center: L.latLng(47.609541, 13.784662),
    zoom: 7,
  };
  const locationMap = L.map(mapContainer.value, options);
  L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
  }).addTo(locationMap);

  props.locations.forEach((location) => {
    const lat = location.location.lat;
    const lon = location.location.lon;

    if (lat != undefined && lon != undefined) {
      const marker = L.marker([lat, lon])
      let mapPopupTemplate: any = `<b>${location.name}</b><br>${location.type}<br>`;
      marker.bindPopup(mapPopupTemplate)
      marker.addTo(locationMap);
    }
  });
}
</script>