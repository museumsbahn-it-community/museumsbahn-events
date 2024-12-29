<template>
  <div class="flex flex-row justify-content-center">
    <div class="w-full lg:w-10 xl:w-8 xxl:w-6 lg:p-5">
        <LocationDetails
          :museumLocation="museumLocation"
          :events="events"
        ></LocationDetails>
    </div>
  </div>
</template>
<script setup lang="ts">
import {subDays} from "date-fns";

const router = useRouter();
const route = useRoute();
const globalConfig = useGlobalConfigStore();
const locationData = useLocationsData();
const eventsData = useEventListData();
const locationId = route?.params?.locationId;
const museumLocation = locationData.locationById(locationId);
const events = eventsData.filteredEventsGroupedByMonth;

onMounted(mounted);

async function mounted(): Promise<void> {
  await locationData.loadLocations();
  await eventsData.loadEvents({
    fromDate: subDays(new Date(), 1),
    tagFilters: [{key: "location_id", options: [locationId]}]
  });
}
</script>