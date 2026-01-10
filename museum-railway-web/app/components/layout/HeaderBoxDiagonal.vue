<style lang="scss">
@use "../../assets/colors" as colors;

.container {
  container-type: inline-size;
}

@container (min-width: 0) {
  .header-box {
    width: 100%;
    height: 100%;
    background: colors.$color-umbragrau;
    clip-path: polygon( 0% 0%,100% 0%,97.239% 75.978%,97.239% 75.978%,97.065% 80.335%,96.875% 84.298%,96.671% 87.853%,96.453% 90.983%,96.223% 93.674%,95.983% 95.911%,95.734% 97.677%,95.477% 98.957%,95.215% 99.737%,94.948% 100%,0% 100%,0% 0% );
  }
}

</style>
<template>
  <div class="w-full h-full container">
    <div class="header-box">
      <slot></slot>
    </div>
  </div>
</template>