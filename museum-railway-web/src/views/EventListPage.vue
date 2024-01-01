<style>
/* hide the opposite side of the timeline */
.p-timeline-event-opposite {
  display: none;
}
</style>
<template>
  <div class="flex flex-column h-full w-full">
    <div class="flex flex-column md:w-5 md:flex-row m-2">
      <InlineMessage severity="warn"> Achtung! Die Daten werden automatisch erfasst und nicht manuell geprüft.
        Abfahrtszeiten und aktuelle Informationen immer auf den Webseiten der Vereine kontrollieren!</InlineMessage>
    </div>
    <div class="flex flex-column md:w-20rem md:flex-row m-2">
      <MultiSelect v-model="selectedCities" display="chip" :options="cities" optionLabel="name"
                   placeholder="Select Cities"
                   :maxSelectedLabels="3" class="w-full md:w-20rem"/>
      <Calendar v-model="dates" placeholder="Select Dates" selectionMode="range" :manualInput="false"/>
    </div>
    <div class="flex min-h-0 w-full md:w-5 m-2">
      <ScrollPanel>
        <Timeline :value="events" align="left" class="customized-timeline">
          <template #content="slotProps">
            <PCard class="mt-3">
              <template #title>
                {{ slotProps.item['name'] }}
              </template>
              <template #subtitle>
                <p> {{ getEventDate(slotProps.item['startDate']).toDateString() }}</p>
                <p> {{ slotProps.item['location'] }} </p>
              </template>
              <template #content>
                <!--          <img v-if="slotProps.item.image" :src="`/images/product/${slotProps.item.image}`" :alt="slotProps.item.name" width="200" class="shadow-1" />-->
                <p v-if="slotProps.item['description']">
                  {{ slotProps.item['description'] }}
                </p>
                <PButton label="Read more" text></PButton>
              </template>
            </PCard>
          </template>
        </Timeline>
      </ScrollPanel>
    </div>
  </div>
</template>
<script setup lang="ts">

import { onMounted, ref } from 'vue';
import { Entry, useEventsStore } from '../store/EventsStore';

const events = ref<Entry[]>([]);
const eventsStore = useEventsStore();

const selectedCities = ref();
const cities = ref([
  {name: 'New York', code: 'NY'},
  {name: 'Rome', code: 'RM'},
  {name: 'London', code: 'LDN'},
  {name: 'Istanbul', code: 'IST'},
  {name: 'Paris', code: 'PRS'},
]);
const dates = ref();

onMounted(mounted);

async function mounted(): Promise<void> {
  await eventsStore.loadEvents();
  events.value = eventsStore.queriedEvents;
}

function getEventDate(value: string): Date {
  return new Date(value);
}
</script>