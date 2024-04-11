<template>
  <div class="flex flex-row justify-content-center">
    <div class="w-full lg:w-8 xl:w-6 lg:p-5">
      <Button
          class="m-3"
          icon="pi pi-arrow-left" rounded outlined aria-label="Zurück"
          @click="router.back()"/>
        <LocationDetails
          :museumLocation="museumLocation"
          :events="events"
        ></LocationDetails>
    </div>
  </div>
</template>
<script setup lang="ts">
const router = useRouter();
const route = useRoute();
const locationData = useLocationsData();
const eventsData = useEventListData();
const locationId = route?.params?.locationId;
const museumLocation = locationData.locationById(locationId);
const events = eventsData.filteredEventsGroupedByMonth;

onMounted(mounted);

async function mounted(): Promise<void> {
  await eventsData.loadEvents({
    tagFilters: [{key: "location_id", options: [locationId]}]
  })
}

</script>