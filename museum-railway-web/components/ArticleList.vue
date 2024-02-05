<template>
  <PageTitle :title="title"></PageTitle>
  <PageContent>
    <ContentList :query="query">
      <template v-slot="{ list }">
        <div v-for="article in list" :key="article._path">
          <Card class="article-content px-5 py-2 mb-5">
            <template #header>
              <h2>{{ article.title }}</h2>
              <div class="flex align-items-center text-sm my-3" v-if="showDate">
                <i class="pi pi-clock mr-2"/>{{ article.date }}
              </div>
            </template>
            <template #content>
              <ContentRendererMarkdown :value="article"/>
            </template>
          </Card>
        </div>
      </template>
      <template #not-found>
        <Card>
          <template #content>
            <p>Keine {{ title }} gefunden.</p>
          </template>
        </Card>
      </template>
    </ContentList>
  </PageContent>
  <div class="h-3rem"></div>
</template>
<script setup lang="ts">

import PageTitle from '~/components/PageTitle.vue';

const props = defineProps<{
  title: string,
  showDate: boolean,
  query: QueryBuilderParams,
}>();
</script>