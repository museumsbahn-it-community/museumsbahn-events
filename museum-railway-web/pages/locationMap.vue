<template>
  <div class="no-scroll-page">
      <LocationMap :locations="locations"></LocationMap>
  </div>
</template>
<script setup lang="ts">
const locationsStore = useLocationsStore();
const locations = ref<MuseumLocation[]>([]);

onMounted(mounted);

async function mounted(): Promise<void> {
  await locationsStore.loadLocations();
  locations.value = locationsStore.allLocations;
}
</script>