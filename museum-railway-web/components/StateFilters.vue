<template>
  <div class="mb-3 flex flex-row flex-wrap justify-content-center">
    <div v-for="state in stateList" :key="state.code" class="mx-1 mb-2">
      <ToggleButton
          :model-value="selectedStates?.includes(state.code)"
          :on-label="state.name"
          :off-label="state.name"
          class="state-toggle-button"
          @click="emit(TOGGLE_STATE_EMIT, state.code)">
        {{ state.name }}
        <Badge
            :value="eventCountForState(state.code)"
            severity="primary"
            class="state-badge"/>
      </ToggleButton>
    </div>
  </div>
</template>
<script setup lang="ts">
import type {StateInfo} from "~/composables/locationDataFunctions";
import {TOGGLE_STATE_EMIT} from "~/model/emitConstants";

const emit = defineEmits([TOGGLE_STATE_EMIT]);
const props = defineProps<{
  stateList: StateInfo[],
  selectedStates: string[] | undefined,
  eventCountForState: (stateCode: string) => number,
}>();

</script>
<style lang="scss">
@use "../assets/variables_impl.scss" as variables;

// Add margin to main content to prevent overlap with fixed sidebar

@media (min-width: 992px) { // lg breakpoint
}

// Mobile footer styles

// Custom styles for the bottom drawer

// Style footer buttons

.footer-button i {
  font-size: 1.5rem;
}

.footer-button span {
  font-size: 0.8rem;
}

.state-badge {
  min-width: 1.5rem;
  height: 1.5rem;
  font-size: 0.75rem;
}
</style>