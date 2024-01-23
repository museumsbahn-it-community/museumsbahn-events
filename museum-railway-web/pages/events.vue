<style lang="scss">
/* hide the opposite side of the timeline */
.p-timeline-event-opposite {
  display: none;
}
</style>
<template>
  <div class="flex flex-row h-full w-full">
    <div class="flex flex-column h-full w-full md:w-6">
      <EventList :highlighted-event="selectedEvent" @eventSelected="selectEvent" ></EventList>
    </div>
    <div class="m-5 md:w-6 sticky" v-if="!$device.isMobile">
      <EventDetails :museum-event="selectedEvent">
        Event Details
      </EventDetails>
    </div>
  </div>
</template>
<script setup lang="ts">
const { isMobile } = useDevice();
const selectedEvent = ref<MuseumEvent | undefined>(undefined);

function selectEvent(value: MuseumEvent) {
  if (!isMobile) {
    console.log("update selected Event")
    selectedEvent.value = value;
  }
}

function isSelected(value: MuseumEvent) {
  return value == selectedEvent.value;
}

</script>