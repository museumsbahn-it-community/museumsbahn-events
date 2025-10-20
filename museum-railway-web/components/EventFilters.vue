<template>
  <div class="event-filters">
    <!-- Date Range Filter -->
    <div class="filter-section mb-3">
      <div class="flex justify-content-between align-items-center">
        <h3>Zeitraum</h3>
        <Button
            v-if="dateRange.length > 0" label="löschen" size="small" text class="reset-button"
            @click="resetDateRange"/>
      </div>
      <div class="flex flex-column gap-2">
        <Calendar
            v-model="dateRange" selection-mode="range" date-format="dd.mm.yy"
            placeholder="Zeitraum auswählen" class="w-full"/>
      </div>
    </div>


    <div
        v-if="filterOptions?.states?.length > 0"
        class="filter-section mb-3">
      <div class="flex justify-content-between align-items-center">
        <h3>Bundesland</h3>
        <Button
            v-if="selectedStates.length > 0" label="löschen" size="small" text class="reset-button"
            @click="resetStates"/>
      </div>
      <div class="flex flex-wrap gap-2">
        <ToggleButton
            v-for="state in filterOptions.states" :key="state.code"
            :model-value="selectedStates.includes(state.code)"
            :on-label="state.name"
            :off-label="state.name"
            class="mx-1 mb-2"
            @click="toggleState(state.code)"/>
      </div>
    </div>

    <!-- Event Type Filter -->
    <div class="filter-section mb-3">
      <div class="flex justify-content-between align-items-center">
        <h3>Veranstaltungstyp</h3>
        <Button
            v-if="selectedEventTypes.length > 0" label="löschen" size="small" text class="reset-button"
            @click="resetEventTypes"/>
      </div>
      <div class="flex flex-wrap gap-2">
        <ToggleButton
            v-for="type in filterOptions.eventTypes" :key="type"
            :model-value="selectedEventTypes.includes(type)"
            :on-label="type"
            :off-label="type"
            class="mb-2"
            @click="toggleEventType(type)"/>
      </div>
    </div>

    <!-- Train Type Filter -->
    <div class="filter-section mb-3">
      <div class="flex justify-content-between align-items-center">
        <h3>Fahrzeugtyp</h3>
        <Button
            v-if="selectedTrainTypes.length > 0" label="löschen" size="small" text class="reset-button"
            @click="resetTrainTypes"/>
      </div>
      <div class="flex flex-wrap gap-2">
        <ToggleButton
            v-for="type in filterOptions.trainTypes" :key="type"
            :model-value="selectedTrainTypes.includes(type)"
            :on-label="type"
            :off-label="type"
            class="mb-2"
            @click="toggleTrainType(type)"/>
      </div>
    </div>

    <!-- Volunteer/Commercial Toggle Buttons -->
    <div class="filter-section mb-3">
      <div class="flex justify-content-between align-items-center">
        <h3>Art der Veranstaltung</h3>
        <Button
            v-if="isVolunteer || isCommercial" label="löschen" size="small" text class="reset-button"
            @click="resetEventType"/>
      </div>
      <div class="flex flex-wrap gap-2">
        <ToggleButton
            v-model="isVolunteer"
            on-label="Ehrenamtlich"
            off-label="Ehrenamtlich"
            class="mb-2"/>
        <ToggleButton
            v-model="isCommercial"
            on-label="Kommerziell"
            off-label="Kommerziell"
            class="mb-2"/>
      </div>
    </div>

    <!-- Tag Filters -->
    <div v-if="filterOptions.tags.length > 0" class="filter-section mb-3">
      <div class="flex justify-content-between align-items-center">
        <h3>Tags</h3>
        <Button
            v-if="selectedTags.length > 0" label="löschen" size="small" text class="reset-button"
            @click="resetTags"/>
      </div>
      <div class="flex flex-wrap gap-2">
        <ToggleButton
            v-for="tag in filterOptions.tags" :key="tag"
            :model-value="selectedTags.includes(tag)"
            :on-label="tag"
            :off-label="tag"
            class="mb-2"
            @click="toggleTag(tag)"/>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, watch, watchEffect} from 'vue';
import Button from 'primevue/button';
import type {EventFilter, EventFilterOptions, EventFilterUpdate} from '~/types/EventFilterTypes';

// Define props to receive filter options from parent component
const props = defineProps<{
  filterOptions: EventFilterOptions,
  filterState: EventFilter,
}>();

// Define emits to send filter changes to parent component
const emit = defineEmits<{
  (e: 'update:filters', filters: EventFilterUpdate): void
}>();

// Initialize reactive refs with empty defaults
const dateRange = ref<Date[]>([]);
const selectedEventTypes = ref<string[]>([]);
const selectedStates = ref<string[]>([]);
const selectedTrainTypes = ref<string[]>([]);
const isVolunteer = ref(false);
const isCommercial = ref(false);
const selectedTags = ref<string[]>([]);

const isUpdatingFromProps = ref(false);

// Initialize values from props.filterState
watchEffect(() => {
  isUpdatingFromProps.value = true;
  if (props.filterState) {
    // Initialize date range from filterState
    const dates: Date[] = [];
    if (props.filterState.fromDate) {
      dates.push(props.filterState.fromDate);
    }
    if (props.filterState.fromDate && props.filterState.toDate) {
      dates.push(props.filterState.toDate);
    }
    dateRange.value = dates;

    // Initialize other filter values from filterState
    selectedStates.value = props.filterState.states || [];
    selectedEventTypes.value = props.filterState.eventTypes || [];
    selectedTrainTypes.value = props.filterState.trainTypes || [];
    isVolunteer.value = props.filterState.volunteer || false;
    isCommercial.value = props.filterState.commercial || false;
    selectedTags.value = props.filterState.tags || [];

    nextTick(() => {
      isUpdatingFromProps.value = false;
    });
  }
});

// Toggle functions
const toggleState = (state: string) => {
  if (selectedStates.value.includes(state)) {
    selectedStates.value = selectedStates.value.filter(t => t !== state);
  } else {
    selectedStates.value.push(state);
  }
};

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

const resetStates = () => {
  selectedStates.value = [];
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
    [dateRange,selectedStates, selectedEventTypes, selectedTrainTypes, isVolunteer, isCommercial, selectedTags],
    () => {
      if (!isUpdatingFromProps.value) {
        emit('update:filters', {
          dateRange: dateRange.value,
          states: selectedStates.value,
          eventTypes: selectedEventTypes.value,
          trainTypes: selectedTrainTypes.value,
          isVolunteer: isVolunteer.value,
          isCommercial: isCommercial.value,
          tags: selectedTags.value
        });
      }
    },
    {deep: true}
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