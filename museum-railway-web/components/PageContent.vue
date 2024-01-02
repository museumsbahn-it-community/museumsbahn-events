<template>
<div class="flex flex-column h-full w-full align-items-center">
  <div class="h-full w-full md:w-5 text-content">
    <div class="flex flex-column h-full">
      <div class="flex flex-row mb-2">
        <div class="page-heading">
          <h1 class="m-2">News</h1>
        </div>
        <div class="flex-grow-1"></div>
      </div>

      <ScrollPanel>
        <slot></slot>
      </ScrollPanel>
    </div>
  </div>
</div>
</template>
<script setup lang="ts">
</script>