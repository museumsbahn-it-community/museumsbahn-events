<template>
  <div class="event-filters">
    <!-- Date Range Filter -->
    <div class="flex flex-column gap-2">
      <Calendar v-model="dateRange" selectionMode="range" :showIcon="true"
                placeholder="Zeitraum auswählen" dateFormat="dd.mm.yy" class="w-full"
                :maxDate="maxDate"/>
    </div>

    <!-- State Filter (if enabled) -->
    <Fieldset v-if="showStates" legend="Bundesländer" :toggleable="true" class="mt-3">
      <div class="grid">
        <div v-for="state in filterOptions.states" :key="state.code" class="col-6 mb-2">
          <div class="flex align-items-center">
            <Checkbox :id="'state-' + state.code"
                      v-model="selectedStates"
                      :value="state.code"
                      :disabled="getStateCount(state.code) === 0"/>
            <label :for="'state-' + state.code" class="ml-2 font-medium">
              {{ state.name }} ({{ getStateCount(state.code) }})
            </label>
          </div>
        </div>
      </div>
    </Fieldset>

    <!-- Event Category Filter -->
    <Fieldset legend="Veranstaltungstyp" :toggleable="true" class="mt-3">
      <div class="grid">
        <div v-for="category in filterOptions.eventCategories" :key="category" class="col-12 mb-2">
          <div class="flex align-items-center">
            <Checkbox :id="'category-' + category"
                      v-model="selectedEventCategories"
                      :value="category"
                      :disabled="getEventCategoryCount(category) === 0"/>
            <label :for="'category-' + category" class="ml-2 font-medium">
              {{ translateTag(category, EventCategoryLabels) }} ({{ getEventCategoryCount(category) }})
            </label>
          </div>
        </div>
      </div>
    </Fieldset>

    <!-- Recurrence Type Filter -->
    <Fieldset legend="Häufigkeit" :toggleable="true" class="mt-3">
      <div class="grid">
        <div v-for="type in filterOptions.recurrenceTypes" :key="type" class="col-12 mb-2">
          <div class="flex align-items-center">
            <Checkbox :id="'recurrence-' + type"
                      v-model="selectedRecurrenceTypes"
                      :value="type"
                      :disabled="getRecurrenceTypeCount(type) === 0"/>
            <label :for="'recurrence-' + type" class="ml-2 font-medium">
              {{ translateTag(type, RecurrenceTypeLabels) }} ({{ getRecurrenceTypeCount(type) }})
            </label>
          </div>
        </div>
      </div>
    </Fieldset>

    <!-- Registration Type Filter -->
    <Fieldset legend="Anmeldung" :toggleable="true" class="mt-3">
      <div class="grid">
        <div v-for="type in filterOptions.registrationTypes" :key="type" class="col-12 mb-2">
          <div class="flex align-items-center">
            <Checkbox :id="'registration-' + type"
                      v-model="selectedRegistrationTypes"
                      :value="type"
                      :disabled="getRegistrationTypeCount(type) === 0"/>
            <label :for="'registration-' + type" class="ml-2 font-medium">
              {{ translateTag(type, RegistrationTypeLabels) }} ({{ getRegistrationTypeCount(type) }})
            </label>
          </div>
        </div>
      </div>
    </Fieldset>

    <!-- Vehicle Type Filter -->
    <Fieldset legend="Fahrzeugtyp" :toggleable="true" class="mt-3">
      <div class="grid">
        <div v-for="type in filterOptions.vehicleTypes" :key="type" class="col-12 mb-2">
          <div class="flex align-items-center">
            <Checkbox :id="'vehicle-' + type"
                      v-model="selectedVehicleTypes"
                      :value="type"
                      :disabled="getVehicleTypeCount(type) === 0"/>
            <label :for="'vehicle-' + type" class="ml-2 font-medium">
              {{ translateTag(type, VehicleTypeLabels) }} ({{ getVehicleTypeCount(type) }})
            </label>
          </div>
        </div>
      </div>
    </Fieldset>

    <!-- Operation Type Filter -->
    <Fieldset legend="Betriebsart" :toggleable="true" class="mt-3">
      <div class="grid">
        <div class="col-12 mb-2">
          <div class="flex align-items-center">
            <Checkbox id="volunteer"
                      v-model="isVolunteer"
                      :binary="true"
                      :disabled="!filterCounts || !filterCounts.operationVolunteer"/>
            <label for="volunteer" class="ml-2 font-medium">
              Ehrenamtlicher Betrieb ({{ filterCounts?.operationVolunteer || 0 }})
            </label>
          </div>
        </div>
        <div class="col-12 mb-2">
          <div class="flex align-items-center">
            <Checkbox id="commercial"
                      v-model="isCommercial"
                      :binary="true"
                      :disabled="!filterCounts || !filterCounts.operationCommercial"/>
            <label for="commercial" class="ml-2 font-medium">
              Kommerzieller Betrieb ({{ filterCounts?.operationCommercial || 0 }})
            </label>
          </div>
        </div>
      </div>
    </Fieldset>

    <!-- Tag Filter -->
    <Fieldset legend="Tags" :toggleable="true" class="mt-3">
      <div class="grid">
        <div v-for="tag in filterOptions.tags" :key="tag" class="col-12 mb-2">
          <div class="flex align-items-center">
            <Checkbox :id="'tag-' + tag"
                      v-model="selectedTags"
                      :value="tag"
                      :disabled="getTagCount(tag) === 0"/>
            <label :for="'tag-' + tag" class="ml-2 font-medium">
              {{ tag }} ({{ getTagCount(tag) }})
            </label>
          </div>
        </div>
      </div>
    </Fieldset>
  </div>
</template>

<script setup lang="ts">
import {computed, ref, watch} from 'vue';
import type {EventFilter, EventFilterOptions, EventFilterUpdate, FilterCounts} from '~/types/EventFilterTypes';
import {
  EventCategoryLabels,
  RecurrenceTypeLabels,
  RegistrationTypeLabels,
  translateTag,
  VehicleTypeLabels
} from '~/composables/eventDataFunctions';

// Defining props and emits
const props = defineProps<{
  filterOptions: EventFilterOptions;
  filterState: EventFilter;
  filterCounts: FilterCounts;
  showStates?: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:filters', payload: EventFilterUpdate): void;
}>();

// Define the maximum date for the date picker (today + 365 days)
const maxDate = new Date();
maxDate.setDate(maxDate.getDate() + 365);

// Date range
const dateRange = ref<Date[]>([]);

const isUpdatingFromProps = ref(false);

// Internal state to prevent infinite loops in computed properties
const internalState = ref({
  states: [] as string[],
  eventCategories: [] as string[],
  vehicleTypes: [] as string[],
  recurrenceTypes: [] as string[],
  registrationTypes: [] as string[],
  isVolunteer: false,
  isCommercial: false,
  tags: [] as string[]
});

// Initialize internal state from filter state
watch(() => props.filterState, (newState) => {
  isUpdatingFromProps.value = true;
  // Update date range
  const dates: Date[] = [];
  if (newState.fromDate) {
    dates.push(newState.fromDate);
  }
  if (newState.toDate) {
    dates.push(newState.toDate);
  }
  dateRange.value = dates;
  
  // Update internal state to match filter state
  internalState.value = {
    states: newState.selectedStates || [],
    eventCategories: newState.eventCategories || [],
    vehicleTypes: newState.vehicleTypes || [],
    recurrenceTypes: newState.recurrenceTypes || [],
    registrationTypes: newState.registrationTypes || [],
    isVolunteer: newState.isVolunteer || false,
    isCommercial: newState.isCommercial || false,
    tags: newState.tags || []
  };

  nextTick(() => {
    isUpdatingFromProps.value = false;
  });
}, {immediate: true, deep: true});

// Selected states
const selectedStates = computed({
  get: () => internalState.value.states,
  set: (value: string[]) => {
    internalState.value.states = value;
    emitFilterUpdate({states: value});
  }
});

// Selected event categories
const selectedEventCategories = computed({
  get: () => internalState.value.eventCategories,
  set: (value: string[]) => {
    internalState.value.eventCategories = value;
    emitFilterUpdate({eventCategories: value});
  }
});

// Selected vehicle types
const selectedVehicleTypes = computed({
  get: () => internalState.value.vehicleTypes,
  set: (value: string[]) => {
    internalState.value.vehicleTypes = value;
    emitFilterUpdate({vehicleTypes: value});
  }
});

// Selected recurrence types
const selectedRecurrenceTypes = computed({
  get: () => internalState.value.recurrenceTypes,
  set: (value: string[]) => {
    internalState.value.recurrenceTypes = value;
    emitFilterUpdate({recurrenceTypes: value});
  }
});

// Selected registration types
const selectedRegistrationTypes = computed({
  get: () => internalState.value.registrationTypes,
  set: (value: string[]) => {
    internalState.value.registrationTypes = value;
    emitFilterUpdate({registrationTypes: value});
  }
});

// Is volunteer
const isVolunteer = computed({
  get: () => internalState.value.isVolunteer,
  set: (value: boolean) => {
    internalState.value.isVolunteer = value;
    emitFilterUpdate({isVolunteer: value});
  }
});

// Is commercial
const isCommercial = computed({
  get: () => internalState.value.isCommercial,
  set: (value: boolean) => {
    internalState.value.isCommercial = value;
    emitFilterUpdate({isCommercial: value});
  }
});

// Selected tags
const selectedTags = computed({
  get: () => internalState.value.tags,
  set: (value: string[]) => {
    internalState.value.tags = value;
    emitFilterUpdate({tags: value});
  }
});

// Watch for date range changes
watch(dateRange, (newRange) => {
  emitFilterUpdate({dateRange: newRange || []});
});

// Helper functions to get counts for each filter option
const getStateCount = (stateCode: string): number => {
  return props.filterCounts?.states?.[stateCode] || 0;
};

const getEventCategoryCount = (category: string): number => {
  return props.filterCounts?.eventCategories?.[category] || 0;
};

const getVehicleTypeCount = (type: string): number => {
  return props.filterCounts?.vehicleTypes?.[type] || 0;
};

const getRecurrenceTypeCount = (type: string): number => {
  return props.filterCounts?.recurrenceTypes?.[type] || 0;
};

const getRegistrationTypeCount = (type: string): number => {
  return props.filterCounts?.registrationTypes?.[type] || 0;
};

const getTagCount = (tag: string): number => {
  return props.filterCounts?.tags?.[tag] || 0;
};

// Function to emit filter updates
const emitFilterUpdate = (partialUpdate: Partial<EventFilterUpdate>) => {
  if (isUpdatingFromProps.value) {
    return;
  }

  const update: EventFilterUpdate = {
    dateRange: dateRange.value || [],
    states: internalState.value.states,
    eventCategories: internalState.value.eventCategories,
    vehicleTypes: internalState.value.vehicleTypes,
    recurrenceTypes: internalState.value.recurrenceTypes,
    registrationTypes: internalState.value.registrationTypes,
    isVolunteer: internalState.value.isVolunteer,
    isCommercial: internalState.value.isCommercial,
    tags: internalState.value.tags,
    ...partialUpdate
  };

  emit('update:filters', update);
};
</script>

<style scoped>
.event-filters {
  width: 100%;
}
</style>