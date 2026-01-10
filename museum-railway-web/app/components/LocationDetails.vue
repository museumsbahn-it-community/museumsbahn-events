<template>
  <div class="w-full flex flex-column align-items-center">
    <div v-if="museumLocation" class="w-full flex flex-column gap-4 align-items-center">
      <div class="flex flex-column w-full align-items-center gap-4" :class="{'lg:flex-row': hasImages}">
        <Galleria
            v-if="hasImages"
            container-class="w-full lg:w-6"
            :value="mappedImages"
            :num-visible="3"
            :show-item-navigators="true"
            :show-item-navigators-on-hover="true"
            :show-indicators="true"
            :show-indicators-on-item="true"
            :show-thumbnails="false"
            :auto-play="true"
            :circular="true"
        >
          <template #item="slotProps">
            <img :src="slotProps.item.itemImageSrc" :alt="slotProps.item.alt"
                 style="width: 100%; max-height: 60vh"/>
          </template>
        </Galleria>

        <card class="w-full lg:h-full" :class="{'lg:w-6': hasImages}">
          <template #title>{{ museumLocation.name }}</template>
          <template #content>
            <div class="flex align-items-center">
              <span class="material-symbols-outlined">location_on</span> <span
                class="m-3 font-bold">{{ museumLocation.location.city }}</span>
            </div>

            <div class="flex align-items-center">
              <span class="material-symbols-outlined">map</span>
              <a
                  :href="museumLocation.googleMapsUrl" target="_blank" rel="noopener noreferrer"
                  class="p-button p-button-text font-bold dark-text"> auf Google
                Maps</a>
            </div>

            <div class="flex align-items-center">
              <span class="material-symbols-outlined">map</span>
              <a
                  :href="museumLocation.mapyczUrl" target="_blank" rel="noopener noreferrer"
                  class="p-button p-button-text font-bold dark-text">auf mapy.cz</a>
            </div>

            <div class="flex align-items-center">
              <span class="material-symbols-outlined">calendar_clock</span>
              <a
                  :href="museumLocation.openingHoursUrl" target="_blank" rel="noopener noreferrer"
                  class="p-button p-button-text font-bold dark-text">Öffnungszeiten</a>
            </div>

            <div class="mx-2 my-4">
              {{ museumLocation.description }}
            </div>

            <div class="flex align-items-end w-full">
              <div class="flex-grow-1"/>
              <a
                  :href="museumLocation.webUrl" target="_blank" rel="noopener noreferrer"
                  class="p-button p-button-text font-bold dark-text">zur Webseite</a>
            </div>
          </template>
        </card>
      </div>

      <card class="w-full">
        <template #title>Künftige Veranstaltungen</template>
        <template #content>
          <div class="flex flex-column gap-3">
            <div v-if="museumLocation.eventListUrl" class="flex align-items-center">
              <span class="material-symbols-outlined">calendar_month</span>
              <a
                  :href="museumLocation.eventListUrl" target="_blank" rel="noopener noreferrer"
                  class="p-button p-button-text font-bold dark-text">Veranstaltungsliste des Museums</a>
            </div>
            <EventList
                :events-grouped-by-month-and-departure="eventsGroupedByMonthAndDeparture"
                @event-selected="navigateToEventDetails"
            />
          </div>
        </template>
      </card>
    </div>
  </div>
</template>
<script setup lang="ts">
import {eventKey} from "~/model/util.ts";
import type {MuseumEventGroupGroup} from "~/composables/eventDataFunctions";
import type {MuseumLocation} from "~/apiModel/apiModel";

const router = useRouter();
const viewport = useViewport();
const props = defineProps<{
  museumLocation: MuseumLocation,
  eventsGroupedByMonthAndDeparture: MuseumEventGroupGroup[],
}>();

const mappedImages = computed(() => props.museumLocation?.images?.map(image => {
  return {
    itemImageSrc: `${image.url}?size=1024`,
    thumbnailImageSrc: `${image.url}?size=256`,
    alt: image.alt,
    title: ""
  }
}) ?? [])

const hasImages = computed(() => mappedImages.value.length > 0)

watch(mappedImages, mi => console.log(mi))

function navigateToEventDetails(value: string) {
  router.push({name: 'eventDetails', params: {eventKey: eventKey(value)}});
}
</script>
