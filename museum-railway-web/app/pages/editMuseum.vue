<script setup lang="ts">

import type {MuseumLocation} from "~/apiModel/apiModel";

interface NewImageData {
  url: string;
  copyright: string;
  alt: string;
}

interface ImageJsonData {
  googleDriveId: string;
  copyright: string;
  alt: string;
}

const {data: allLocations} = useAllLocations();
const selectedLocation = ref<MuseumLocation | null>(null);


const description = computed(() => selectedLocation.value?.description);
const newImages = ref<NewImageData[]>([]);
const imageJson = ref<string>("");

function addNewImage(): void {
  newImages.value.push({url: "", copyright: "", alt: ""})
}

function loadImagesFromJson() {
  // share links usually use the following format
  // https://drive.google.com/file/d/1BONz6GuPtba_vN4OxLAvKPMXlfIlpTBy/view?usp=sharing
  const dataRaw = JSON.parse(imageJson.value) as ImageJsonData[];
  newImages.value = dataRaw.map((raw) => {
        return {
          url: `https://drive.google.com/file/d/${raw.googleDriveId}`,
          copyright: raw.copyright,
          alt: raw.alt,
        }
      }
  );
}

function generateImageJson() {
  const mappedImages = newImages.value.map((image): ImageJsonData => {
    const id = image.url.replace("https://drive.google.com/file/d/", "").split("/")[0];

    return {
      googleDriveId: id,
      copyright: image.copyright,
      alt: image.alt,
    }
  })

  imageJson.value = JSON.stringify(mappedImages, null, 2);
}

</script>

<template>
  <div class="flex justify-content-center">
    <div class="flex flex-column w-10 gap-4">
      <h1>Museum Bearbeiten</h1>
      <inline-message severity="warn">
        Glückwunsch! Du hast eine interne Seite gefunden die zur Bearbeitung der Daten dient. Diese Seite hilft uns die
        Daten für museumsbahn-events
        zu verwalten, kann aber keine Daten ändern. Wenn du nach Informationen über Museumsbahnen suchst, dann nutze
        bitte die Navigation um zur Hauptseite zurückzukommen.
      </inline-message>
      <Select
          v-model="selectedLocation"
          :options="allLocations ?? []"
          option-label="name"
      />

      {{ description }}

      <h2>Images</h2>

      <div v-if="selectedLocation?.images != null && selectedLocation.images.length > 0">
        <h3>Already configured</h3>

        <div v-for="image in selectedLocation.images" :key="image.url" class="flex flex-column gap-4">
          <img :src="image.url" width="300">
          <div>
            <strong>Copyright: </strong>
            <p>{{ image.copyright }}</p>
          </div>
          <div>
            <strong>Alt-Text/Description: </strong>
            <p>{{ image.alt }}</p>
          </div>
        </div>
      </div>
      <div>
        <h3>Configure new images</h3>
        <button @click="addNewImage">Add new image</button>
        <div v-for="image in newImages" :key="image.url" class="flex flex-column gap-4">
          Image: <a :href="image.url" target="_blank">{{ image.url }}</a>
          <label>Url</label>
          <input v-model="image.url" type="text">
          <label>Copyright</label>
          <input v-model="image.copyright" type="text">
          <label>Alt-Text:</label>
          <textarea v-model="image.alt" rows="10" cols="120"/>
        </div>
      </div>

      <label>Json</label>
      <button @click="loadImagesFromJson">Load</button>
      <button @click="generateImageJson">Generate</button>
      <textarea v-model="imageJson" rows="10" cols="120"/>
    </div>
  </div>
</template>

<style scoped lang="scss">

</style>