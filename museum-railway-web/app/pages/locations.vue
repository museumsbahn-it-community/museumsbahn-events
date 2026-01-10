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
        <div v-for="location in allLocations" :key="location.locationId">
          <Card>
            <template #title>
              <div class="flex flex-row mx-3 mt-3">
                <NuxtLink class="invisible-link" :to="{name: 'locationDetails', params: {locationId: location.locationId}}">
                <span class="w-8 overflow-wrap-anywhere">
                  {{ location.name }}
                </span>
                </NuxtLink>
                <div class="flex-grow-1"></div>
                <div>
                  <Button variant="link" class="mx-1 px-2" @click="showLocationOnMap(location)">
                    <span class="header-icon pi pi-map"></span>
                  </Button>

                  <NuxtLink :to="{name: 'locationDetails', params: {locationId: location.locationId}}">
                    <Button variant="link" class="pr-0 pl-2">
                      <span class="header-icon pi pi-info-circle"></span>
                    </Button>
                  </NuxtLink>
                </div>
              </div>
            </template>
            <template #content>
              <div class="mx-3 mb-3">
                <!--              <Image class="banner-image" :src="imageLocation(location.locationId)"></Image>-->
                <div class="flex align-items-center text-sm my-2">
                  <span class="material-symbols-outlined">location_on</span> {{ location.location.city }},
                  {{ location.location.state }}
                </div>
                <div class="my-4">
                  <span>{{ eventCounts[location.locationId] }} Veranstaltungen gefunden</span>
                </div>
                <div class="flex align-items-end w-full">
                  <div class="flex-grow-1"></div>
                  <a :href="location.webUrl" target="_blank" rel="noopener noreferrer" class="font-bold">
                    Webseite des Veranstalters
                  </a>
                </div>
              </div>
            </template>
          </Card>
          <div class="h-1rem"/>
        </div>
      </ScrollPanel>
      <div class="flex-grow h-full w-full ml-5" v-if="viewport.isGreaterThan('tablet')">
        <LocationMap :locations="allLocations ?? []" :highlighted-location="highlightedLocation"></LocationMap>
      </div>
    </div>
  </div>
  <div class="default-footer"></div>
</template>

<script setup lang="ts">
import {useRouter} from 'nuxt/app';
import {computed, ref} from 'vue';
import type {MuseumLocation} from '~/apiModel/apiModel';

const {data: allLocations} = useAllLocations();
const events = useAllEvents();

const viewport = useViewport();

const eventCounts = computed(() => {
  const eventCountMap: { [key: string]: number } = {};
  allLocations.value?.forEach(location => {
    const locationId = location.locationId;
    const eventCount = events.data.value != null ? eventCountForLocationId(events.data.value, locationId) : 0;
    eventCountMap[locationId] = eventCount;
  });
  return eventCountMap;
});

const highlightedLocation = ref<MuseumLocation | undefined>(undefined);
const router = useRouter();

function showLocationOnMap(location: MuseumLocation) {
  highlightedLocation.value = location;

  if (!viewport.isGreaterThan('tablet')) {
    router.push({name: 'locationMapDetails', params: {locationId: location.locationId}});
  }
}

useSeoMeta({
  title: 'Museen und Bahnen',
  ogTitle: 'Museen und Bahnen',
  description: 'Übersicht über Museumsbahnen und Eisenbahnmuseen in Österreich.',
  ogDescription: 'Hier findest du eine Übersicht der Museumsbahnen und Eisenbahnmuseen die es in Österreich gibt.',
  ogImage: 'https://museumsbahn-events.at/img/social_media_preview.jpg',
  twitterCard: 'summary_large_image',
});
</script>
