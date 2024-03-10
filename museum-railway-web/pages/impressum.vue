<template>
  <ArticleList title="Impressum und Datenschutz" :path="path" :show-date="false">
    <template #additionalContent>
      <!-- this card is copied from article list, but for now here is the only place we have to use it as well -->
      <!-- if we need further usages we MUST create a new component ArticleCard for this -->
      <Card class="article-content px-5 py-2 mb-5">
        <template #header>
          <h2>Analysedaten</h2>
        </template>
        <template #content>
          <MatomoAnalyticsSwitch></MatomoAnalyticsSwitch>
        </template>
      </Card>
    </template>
  </ArticleList>
</template>
<script setup lang="ts">
const path: string = "/imprint"
const query: QueryBuilderParams = {path: 'imprint'};
</script>