<template>
  <div class="w-full flex align-items-center">
    <div v-if="museumLocation != undefined" class="flex flex-column gap-3">
      <div class="p-3 gap-3 flex flex-column">
        <h2>{{ museumLocation.name }}</h2>

        <div class="align-items-center mb-4" style="max-height: 60vh">
          <Galleria
              container-style="max-height: 60vh"
              :value="mappedImages"
              :num-visible="3"
              :show-item-navigators="true"
              :show-item-navigators-on-hover="true"
              :show-indicators="true"
              :show-indicators-on-item="true"
              :show-thumbnails="false" v
              :auto-play="true"
              :circular="true"
          >
            <template #item="slotProps">
              <img :src="slotProps.item.itemImageSrc" :alt="slotProps.item.alt" style="width: 100%; max-height: 60vh"/>
            </template>
          </Galleria>

          <!--          <div v-for="image in museumLocation.images" :key="image">-->
          <!--            &lt;!&ndash;TODO: copyright &ndash;&gt;-->
          <!--            <Image class="image-border-radius-small" :src="image.url" :alt="image.url"/>-->
          <!--          </div>-->
        </div>

        <div class="flex align-items-center">
          <span class="material-icons-outlined">location_on</span> {{ museumLocation.location.city }}
        </div>

        <div class="flex align-items-center">
          <span class="material-icons-outlined">map</span>
          <a
              :href="museumLocation.googleMapsUrl" target="_blank" rel="noopener noreferrer"
              class="p-button p-button-text font-bold dark-text"> auf Google
            Maps</a>
        </div>

        <div class="flex align-items-center">
          <span class="material-icons-outlined">map</span>
          <a
              :href="museumLocation.mapyczUrl" target="_blank" rel="noopener noreferrer"
              class="p-button p-button-text font-bold dark-text">auf mapy.cz</a>
        </div>

        <div class="my-4">
          <EventCollectorIndicator :location="museumLocation"/>
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

        <h2>Künftige Veranstaltungen</h2>

        <div v-if="museumLocation != undefined" class="mb-4">
          <EventList
              :events-grouped-by-month-and-departure="eventsGroupedByMonthAndDeparture"
              @event-selected="navigateToEventDetails"
          />
        </div>
      </div>
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

watch(mappedImages, mi => console.log(mi))

function navigateToEventDetails(value: string) {
  router.push({name: 'eventDetails', params: {eventKey: eventKey(value)}});
}
</script>
