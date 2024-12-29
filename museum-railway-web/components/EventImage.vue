<style lang="scss">
@use "../assets/variables_impl.scss" as variables;

.image-container {
  position: relative;
}

.copyright-overlay {
  border-top-left-radius: variables.$border-radius-small;
  position: absolute;
  bottom: 0;
  right: 0;
}

img {
  /* this will make the image stretch and no overflow*/
  /* height:0;*/
  max-width: 100%;
  max-height: 100%;
  flex-shrink: 1;
  display: block;
}
</style>
<template>
  <div class="flex flex-grow-1 flex-column justify-content-center">
    <div class="flex flex-row justify-content-end image-container">
      <div v-if="imgCopyright != null" class="bg-mittelgrau text-xs px-2 py-1 copyright-overlay">{{ imgCopyright }}
      </div>
      <Image :src="imgSource" :alt="imgAltText" class="w-full h-full flex justify-content-end max-dimensions"
        image-class="flex-1" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import { getAltTextOrDefault, getCopyrightOrDefault, getImageUrlOrDefault } from '~/composables/eventImage';

const props = defineProps<{ event: MuseumEvent | undefined }>()

const imgSource = computed(() => getImageUrlOrDefault(props.event?.pictureUrl))
const imgAltText = computed(() => getAltTextOrDefault(props.event?.pictureAltText))
const imgCopyright = computed(() => getCopyrightOrDefault(props.event?.pictureCopyright))
</script>