<template>
  <div class="bg-white">
    <div class="h-3rem"></div>
    <LayoutHeaderBoxDiagonal class="w-11 lg:w-9">
      <div class="h-full w-full flex align-items-center">
        <div class="w-1 lg:w-4"></div>
        <div class="w-11 lg:w-8 justify-content-center">
          <h1 class="light-text my-2">{{ title }}</h1>
        </div>
      </div>
    </LayoutHeaderBoxDiagonal>
    <div class="h-3rem"></div>
  </div>
</template>
<script setup lang="ts">
const props = defineProps<{
  title: string
}>();
</script>