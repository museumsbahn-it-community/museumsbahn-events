<template>
  <div class="text-sm flex flex-row">
    <div class="collectorIndicatorBar mr-2"
         :style="{ background: collectorStatusColor(location.eventCollectorType)}">
    </div>
    <div>
      <label>Automatische Eventsammlung:</label><br/>
      <span>{{ location.eventCollectorType }}</span>
    </div>
  </div>
</template>
<script setup lang="ts">
import { Colors } from '~/app/colors.ts';

const props = defineProps<{
  location: MuseumLocation
}>();

function collectorStatusColor(eventCollectorType: string): string {
  switch (eventCollectorType) {
    case 'Aktiv':
      return Colors.Pastel_Green;
    case 'Möglich, derzeit nicht umgesetzt.':
      return Colors.Pastel_Yellow;
    case 'Teilweise möglich.':
      return Colors.Pastel_Orange;
    case 'Nicht möglich.':
      return Colors.Pastel_Red;
    default:
      return Colors.Pastel_Grey;
  }
}
</script>