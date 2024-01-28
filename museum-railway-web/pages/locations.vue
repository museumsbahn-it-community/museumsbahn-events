<template>
  <div class="no-scroll-page">
    <div class="flex flex-row h-full">
      <ScrollPanel class="flex-column h-full lg:w-4 w-full">
        <div v-for="location in locations">
          <Panel>
            <template #header>
              <div class="flex flex-column w-8">
                <span class="font-bold">
                {{ location.name }}
                </span>
              </div>
            </template>
            <template #icons>
              <button class="p-panel-header-icon p-link mr-2">
                <span class="pi pi-map"></span>
              </button>
              <button class="p-panel-header-icon p-link mr-2" @click="openLocationDetails(location.locationId)">
                <span class="pi pi-info-circle"></span>
              </button>
            </template>
            <p>
              <div class="flex align-items-center m-1 text-sm">
                <span class="material-icons-outlined">location_on</span> {{ location.location.city }},
                {{ location.location.state }}
              </div>
              <i class="pi pi-globe"/> <a href="{{location.webUrl}}">{{ location.webUrl }}</a>
            </p>
          </Panel>
          <div class="h-1rem"/>
        </div>
      </ScrollPanel>
      <div class="flex-grow h-full w-full ml-5" v-if="viewport.isGreaterThan('tablet')">
        <LocationMap :locations="locations"></LocationMap>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const viewport = useViewport();
const locationsStore = useLocationsStore();
const locations = ref<MuseumLocation[]>([]);
const router = useRouter();

function openLocationDetails(locationId: string) {
  router.push({name: 'locationDetails', params: {locationId}});
}

onMounted(mounted);

async function mounted(): Promise<void> {
  await locationsStore.loadLocations();
  locations.value = locationsStore.allLocations;
}
</script>
