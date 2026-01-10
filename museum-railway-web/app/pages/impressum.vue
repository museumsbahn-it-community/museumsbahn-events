<template>
  <ArticleList title="Impressum und Datenschutz" :content-collection="contentCollection" :show-date="false">
  </ArticleList>
  <div class="default-footer"></div>
</template>
<script setup lang="ts">
const contentCollection: string = "imprint"
</script>