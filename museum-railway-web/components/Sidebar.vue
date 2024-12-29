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

.sidebar-left {
  overflow: scroll;
  border-top-right-radius: variables.$border-radius-small;
  border-bottom-right-radius: variables.$border-radius-small;
}

.sidebar-right {
  overflow: scroll;
  border-top-left-radius: variables.$border-radius-small;
  border-bottom-left-radius: variables.$border-radius-small;
}

.sidebar-center {
  overflow: scroll;
  border-radius: variables.$border-radius-small;
}

.sidebar-content {
  overflow-y: scroll;
  min-height: 100px;
}
</style>