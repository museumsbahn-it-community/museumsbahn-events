<template>
  <ArticleList title="Neuigkeiten" :query="query" show-date></ArticleList>
</template>
<script setup lang="ts">
const query: QueryBuilderParams = {path: 'news', sort: [{date: -1}]};
</script>