<script setup lang="ts">
  const props = defineProps<{
    text: string,
    routerLink: string,
    imagePath: string,
    imageAltText: string,
  }>();
</script>

<template>
    <RouterLink :to="props.routerLink" class="" style="position:relative">
      <Image :src="props.imagePath"
             :alt="props.imageAltText"
             image-class="w-full object-fit-cover border-radius-small shadow-5 navigation-card-image">
      </Image>
      <div class="w-full flex flex-row justify-content-center image-text" style="position: absolute; bottom: 0">
        <h3>{{props.text}}</h3>
      </div>
    </RouterLink>
</template>

<style scoped lang="scss">

</style>