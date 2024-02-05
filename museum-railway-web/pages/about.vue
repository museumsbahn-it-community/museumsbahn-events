<template>
  <ArticleList title="Über uns" :query="query"></ArticleList>
</template>
<script setup lang="ts">
const query: QueryBuilderParams = {path: 'about'};
</script>