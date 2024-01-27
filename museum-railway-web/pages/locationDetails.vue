<template>
  <div class="flex flex-row justify-content-center">
    <div class="w-full md:w-8 xl:w-4">
      <Button
          class="m-3"
          icon="pi pi-arrow-left" rounded outlined aria-label="Zurück"
          @click="router.back()"/>
      <LocationDetails></LocationDetails>
    </div>
  </div>
</template>
<script setup lang="ts">
const router = useRouter();
</script>