<template>
  <div>

    <h3>Buttons</h3>
    <div class="flex flex-row gap-3">
      <Button severity="primary">Primary</Button>
      <Button severity="secondary">Secondary</Button>
      <Button severity="success">Success</Button>
      <Button severity="info">Info</Button>
      <Button severity="warn">Warn</Button>
      <Button severity="help">Help</Button>
      <Button severity="danger">Danger</Button>

    </div>
    <h3>Toggle Buttons</h3>
    <div class="flex flex-row gap-3">
      <ToggleButton>Toggle Button</ToggleButton>
    </div>

    <div class="flex flex-row gap-3">
      <ToggleButton :v-model="toggleChecked">Toggle Button</ToggleButton>
    </div>


    <h3>Chips</h3>

    <Chip>Chip</Chip>
    <Chip >Chip</Chip>
    <Chip removable>Chip</Chip>
    <Chip removable>Chip</Chip>

    <h3>Badges</h3>
    <Badge value="2"/>
    <Badge value="6" severity="secondary"/>
    <Badge value="8" severity="success"/>
    <Badge value="4" severity="info"/>
    <Badge value="9" severity="warn"/>
    <Badge value="3" severity="danger"/>
    <Badge value="5" severity="contrast"/>

    <h3>Palettes</h3>

    <h4>surface</h4>

    <div class="flex flex-row">
      <ColorTile color="surface.50" label="surface.50"/>
      <ColorTile color="--p-surface-100" label="surface.100"/>
      <ColorTile color="--p-surface-200" label="surface.200"/>
      <ColorTile color="--p-surface-300" label="surface.300"/>
      <ColorTile color="--p-surface-400" label="surface.400"/>
      <ColorTile color="--p-surface-500" label="surface.500"/>
      <ColorTile color="--p-surface-600" label="surface.600"/>
      <ColorTile color="--p-surface-700" label="surface.700"/>
      <ColorTile color="--p-surface-800" label="surface.800"/>
      <ColorTile color="--p-surface-900" label="surface.900"/>
    </div>


  </div>
</template>
<script lang="ts" setup>

import ColorTile from "~/components/ui/ColorTile.vue";

const toggleChecked = ref(true);

</script>