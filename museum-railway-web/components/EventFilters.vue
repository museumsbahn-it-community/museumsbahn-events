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
        v-if="filterOptions?.states?.length > 0 && showStates"
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
            v-if="selectedEventCategories.length > 0" label="löschen" size="small" text class="reset-button"
            @click="reseteventCategories"/>
      </div>
      <div class="flex flex-wrap gap-2">
        <ToggleButton
            v-for="type in filterOptions.eventCategories" :key="type"
            :model-value="selectedEventCategories.includes(type)"
            class="mb-2"
            @click="toggleEventType(type)">
          {{translateTag(type, EventCategoryLabels)}}
          <Badge
              v-if="filterCounts?.eventCategories && filterCounts.eventCategories[type] !== undefined"
              :value="filterCounts.eventCategories[type]"
              severity="primary"
              class="filter-badge"/>
        </ToggleButton>
      </div>
    </div>

    <!-- Train Type Filter -->
    <div class="filter-section mb-3">
      <div class="flex justify-content-between align-items-center">
        <h3>Fahrzeugtyp</h3>
        <Button
            v-if="selectedvehicleTypes.length > 0" label="löschen" size="small" text class="reset-button"
            @click="resetvehicleTypes"/>
      </div>
      <div class="flex flex-wrap gap-2">
        <ToggleButton
            v-for="type in filterOptions.vehicleTypes" :key="type"
            :model-value="selectedvehicleTypes.includes(type)"
            class="mb-2"
            @click="toggleTrainType(type)">
          {{translateTag(type, VehicleTypeLabels)}}
          <Badge
              v-if="filterCounts?.vehicleTypes && filterCounts.vehicleTypes[type] !== undefined"
              :value="filterCounts.vehicleTypes[type]"
              severity="primary"
              class="filter-badge"/>
        </ToggleButton>
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
            :on-label="translateTag(OperationType.VOLUNTEER, OperationTypeLabels)"
            :off-label="translateTag(OperationType.VOLUNTEER, OperationTypeLabels)"
            class="mb-2"/>
        <ToggleButton
            v-model="isCommercial"
            :on-label="translateTag(OperationType.COMMERCIAL, OperationTypeLabels)"
            :off-label="translateTag(OperationType.COMMERCIAL, OperationTypeLabels)"
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
            @click="toggleTag(tag)">
          {{tag}}
          <Badge
              v-if="filterCounts?.tags && filterCounts.tags[tag] !== undefined"
              :value="filterCounts.tags[tag]"
              severity="primary"
              class="filter-badge"/>
        </ToggleButton>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, watch, watchEffect} from 'vue';
import Button from 'primevue/button';
import Badge from 'primevue/badge';
import type {EventFilter, EventFilterOptions, EventFilterUpdate, FilterCounts} from '~/types/EventFilterTypes';
import {EventCategoryLabels, translateTag} from "~/composables/eventDataFunctions";
import {OperationType} from "~/apiModel/apiModel";

// Define props to receive filter options from parent component
const props = withDefaults(defineProps<{
  filterOptions: EventFilterOptions,
  filterState: EventFilter,
  filterCounts?: FilterCounts,
  showStates: boolean,
}>(), {
  showStates: true
});


// Define emits to send filter changes to parent component
const emit = defineEmits<{
  (e: 'update:filters', filters: EventFilterUpdate): void
}>();

// Initialize reactive refs with empty defaults
const dateRange = ref<Date[]>([]);
const selectedEventCategories = ref<string[]>([]);
const selectedStates = ref<string[]>([]);
const selectedvehicleTypes = ref<string[]>([]);
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
    selectedEventCategories.value = props.filterState.eventCategories || [];
    selectedvehicleTypes.value = props.filterState.vehicleTypes || [];
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
  if (selectedEventCategories.value.includes(type)) {
    selectedEventCategories.value = selectedEventCategories.value.filter(t => t !== type);
  } else {
    selectedEventCategories.value.push(type);
  }
};

const toggleTrainType = (type: string) => {
  if (selectedvehicleTypes.value.includes(type)) {
    selectedvehicleTypes.value = selectedvehicleTypes.value.filter(t => t !== type);
  } else {
    selectedvehicleTypes.value.push(type);
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

const reseteventCategories = () => {
  selectedEventCategories.value = [];
};

const resetvehicleTypes = () => {
  selectedvehicleTypes.value = [];
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
    [dateRange,selectedStates, selectedEventCategories, selectedvehicleTypes, isVolunteer, isCommercial, selectedTags],
    () => {
      if (!isUpdatingFromProps.value) {
        emit('update:filters', {
          dateRange: dateRange.value,
          states: selectedStates.value,
          eventCategories: selectedEventCategories.value,
          vehicleTypes: selectedvehicleTypes.value,
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

.filter-badge {
  min-width: 1.5rem;
  height: 1.5rem;
  font-size: 0.75rem;
}
</style>
