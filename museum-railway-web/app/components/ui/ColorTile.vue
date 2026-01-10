<template>
  <div class="color-tile-container">
    <div 
      class="color-tile" 
      :style="{ backgroundColor: `var(${color})` }"
    ></div>
    <div class="color-label">{{ label }}</div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  color: string;
  label: string;
}

defineProps<Props>();
</script>

<style scoped>
.color-tile-container {
  display: inline-block;
  text-align: center;
}

.color-tile {
  width: 3rem;
  height: 3rem;
  border: 1px solid #ccc;
}

.color-label {
  margin-top: 0.5rem;
  font-size: 0.875rem;
}
</style>