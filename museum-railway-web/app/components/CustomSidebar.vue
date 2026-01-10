<template>
  <div>
    <div
    class="h-full flex flex-column"
    :class="{
      'sidebar-left': side == 'left',
      'sidebar-right': side == 'right',
      'sidebar-center': side == 'center',
    }">
      <div class="flex justify-content-center bg-umbragrau light-text p-4">
        <h2 class="m-0">{{ props.title }}</h2>
      </div>
      <div class="flex-1 bg-mittelgrau sidebar-content p-3">
        <slot></slot>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>

const props = defineProps<{
  title: string,
  side: 'left' | 'right' | 'center',
}>()


</script>

<style lang="scss">
@use "../assets/variables_impl" as variables;

/* Common styles for all sidebar types */
.sidebar-left, .sidebar-right, .sidebar-center {
  overflow: hidden;
  box-sizing: border-box;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  position: relative; /* Use relative positioning instead of fixed */
  width: 100%; /* Let parent control the width */
  height: 100%; /* Let parent control the height */
}

/* Specific styles for each sidebar type that don't assume parent layout */
.sidebar-left {
  border-top-right-radius: variables.$border-radius-small;
  border-bottom-right-radius: variables.$border-radius-small;
}

.sidebar-right {
  border-top-left-radius: variables.$border-radius-small;
  border-bottom-left-radius: variables.$border-radius-small;
}

.sidebar-center {
  border-radius: variables.$border-radius-small;
}

.sidebar-content {
  overflow-y: auto;
  min-height: 100px;
  height: 100%; /* Let parent control the height */
}
</style>
