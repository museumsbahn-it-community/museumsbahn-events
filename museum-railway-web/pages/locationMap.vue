<template>
  <div class="fill-page-height flex flex-column">
    <Button
        class="m-3"
        icon="pi pi-arrow-left" rounded outlined aria-label="Zurück"
        v-if="locationIdParam != undefined"
        @click="router.back()"/>
    <LocationMap :locations="locations"></LocationMap>
  </div>
</template>
<script setup lang="ts">
const locationsStore = useLocationsStore();
const locations = ref<MuseumLocation[]>([]);
const router = useRouter();
const route = useRoute();
const locationIdParam = route?.params?.locationId;

onMounted(mounted);

async function mounted(): Promise<void> {
  await locationsStore.loadLocations();
  locations.value = locationsStore.allLocations;
}
</script>