<template>
  <div class="event-filters">
    <!-- Date Range Filter -->
    <div class="filter-section mb-3">
      <div class="flex justify-content-between align-items-center">
        <h3>Zeitraum</h3>
        <Button v-if="dateRange.length > 0" label="löschen" size="small" text class="reset-button" @click="resetDateRange" />
      </div>
      <div class="flex flex-column gap-2">
        <Calendar v-model="dateRange" selectionMode="range" dateFormat="dd.mm.yy" 
                 placeholder="Zeitraum auswählen" class="w-full" />
      </div>
    </div>

    <!-- Event Type Filter -->
    <div class="filter-section mb-3">
      <div class="flex justify-content-between align-items-center">
        <h3>Veranstaltungstyp</h3>
        <Button v-if="selectedEventTypes.length > 0" label="löschen" size="small" text class="reset-button" @click="resetEventTypes" />
      </div>
      <div class="flex flex-wrap gap-2">
        <ToggleButton v-for="type in eventTypes" :key="type"
                     :modelValue="selectedEventTypes.includes(type)"
                     :onLabel="type"
                     :offLabel="type"
                     class="mb-2"
                     @click="toggleEventType(type)" />
      </div>
    </div>

    <!-- Train Type Filter -->
    <div class="filter-section mb-3">
      <div class="flex justify-content-between align-items-center">
        <h3>Fahrzeugtyp</h3>
        <Button v-if="selectedTrainTypes.length > 0" label="löschen" size="small" text class="reset-button" @click="resetTrainTypes" />
      </div>
      <div class="flex flex-wrap gap-2">
        <ToggleButton v-for="type in trainTypes" :key="type"
                     :modelValue="selectedTrainTypes.includes(type)"
                     :onLabel="type"
                     :offLabel="type"
                     class="mb-2"
                     @click="toggleTrainType(type)" />
      </div>
    </div>

    <!-- Volunteer/Commercial Toggle Buttons -->
    <div class="filter-section mb-3">
      <div class="flex justify-content-between align-items-center">
        <h3>Art der Veranstaltung</h3>
        <Button v-if="isVolunteer || isCommercial" label="löschen" size="small" text class="reset-button" @click="resetEventType" />
      </div>
      <div class="flex flex-wrap gap-2">
        <ToggleButton v-model="isVolunteer" 
                     onLabel="Ehrenamtlich" 
                     offLabel="Ehrenamtlich" 
                     class="mb-2" />
        <ToggleButton v-model="isCommercial" 
                     onLabel="Kommerziell" 
                     offLabel="Kommerziell" 
                     class="mb-2" />
      </div>
    </div>

    <!-- Tag Filters -->
    <div class="filter-section mb-3" v-if="tags.length > 0">
      <div class="flex justify-content-between align-items-center">
        <h3>Tags</h3>
        <Button v-if="selectedTags.length > 0" label="löschen" size="small" text class="reset-button" @click="resetTags" />
      </div>
      <div class="flex flex-wrap gap-2">
        <ToggleButton v-for="tag in tags" :key="tag"
                     :modelValue="selectedTags.includes(tag)"
                     :onLabel="tag"
                     :offLabel="tag"
                     class="mb-2"
                     @click="toggleTag(tag)" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import Button from 'primevue/button';

// Define props to receive tags from parent component
const props = defineProps<{
  availableTags?: string[]
}>();

// Define emits to send filter changes to parent component
const emit = defineEmits<{
  (e: 'update:filters', filters: {
    dateRange: Date[],
    eventTypes: string[],
    trainTypes: string[],
    isVolunteer: boolean,
    isCommercial: boolean,
    tags: string[]
  }): void
}>();

// Date Range
const dateRange = ref<Date[]>([]);

// Event Types
const eventTypes = ['Museumsbahn', 'Museum', 'Sonderfahrt', 'Veranstaltung'];
const selectedEventTypes = ref<string[]>([]);

// Train Types
const trainTypes = ['Dampf', 'Diesel', 'Elektro', 'Tram', 'Schiff'];
const selectedTrainTypes = ref<string[]>([]);

// Volunteer/Commercial
const isVolunteer = ref(false);
const isCommercial = ref(false);

// Tags
const tags = computed(() => props.availableTags || []);
const selectedTags = ref<string[]>([]);

// Toggle functions
const toggleEventType = (type: string) => {
  if (selectedEventTypes.value.includes(type)) {
    selectedEventTypes.value = selectedEventTypes.value.filter(t => t !== type);
  } else {
    selectedEventTypes.value.push(type);
  }
};

const toggleTrainType = (type: string) => {
  if (selectedTrainTypes.value.includes(type)) {
    selectedTrainTypes.value = selectedTrainTypes.value.filter(t => t !== type);
  } else {
    selectedTrainTypes.value.push(type);
  }
};

const toggleTag = (tag: string) => {
  if (selectedTags.value.includes(tag)) {
    selectedTags.value = selectedTags.value.filter(t => t !== tag);
  } else {
    selectedTags.value.push(tag);
  }
};

// Reset functions for each filter type
const resetDateRange = () => {
  dateRange.value = [];
};

const resetEventTypes = () => {
  selectedEventTypes.value = [];
};

const resetTrainTypes = () => {
  selectedTrainTypes.value = [];
};

const resetEventType = () => {
  isVolunteer.value = false;
  isCommercial.value = false;
};

const resetTags = () => {
  selectedTags.value = [];
};

// Watch for changes in filters and emit them to parent
watch(
  [dateRange, selectedEventTypes, selectedTrainTypes, isVolunteer, isCommercial, selectedTags],
  () => {
    emit('update:filters', {
      dateRange: dateRange.value,
      eventTypes: selectedEventTypes.value,
      trainTypes: selectedTrainTypes.value,
      isVolunteer: isVolunteer.value,
      isCommercial: isCommercial.value,
      tags: selectedTags.value
    });
  },
  { deep: true }
);
</script>

<style scoped>
.event-filters {
  width: 100%;
}

.filter-section h3 {
  margin-bottom: 0.5rem;
  font-size: 1.1rem;
}

.reset-button {
  font-size: 0.8rem;
  font-weight: normal;
  padding: 0.25rem 0.5rem;
}
</style>
