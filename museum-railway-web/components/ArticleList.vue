<template>
  <PageTitle :title="title"></PageTitle>
  <PageContent>
    <template v-if="posts">
      <div v-for="article in posts" :key="article._path">
        <Card class="article-content px-5 py-2 mb-5">
          <template #header>
            <h2>{{ article.title }}</h2>
            <div class="flex align-items-center text-sm my-3" v-if="showDate">
              <i class="pi pi-clock mr-2" />{{ article.date }}
            </div>
          </template>
          <template #content>
            <ContentRenderer :value="article" />
          </template>
        </Card>
      </div>
    </template>
    <template v-else>
      <Card class="article-content  px-5 py-2 mb-5">
        <template #content>
          <p>Keine Posts für {{ title }} gefunden.</p>
        </template>
      </Card>
    </template>
    <slot name="additionalContent"/>
  </PageContent>
  <div class="h-3rem"></div>
</template>
<script setup lang="ts">
import { useAsyncData } from 'nuxt/app';
import PageTitle from '~/components/PageTitle.vue';

const props = defineProps<{
  title: string,
  showDate: boolean,
  contentCollection: string | undefined,
}>();

const { data: posts } = props.showDate ? await useAsyncData(`${props.contentCollection}-content`, () => queryCollection(props.contentCollection)
  .order('date', 'DESC')
  .all()
) :
  await useAsyncData(`${props.contentCollection}-content`, () => queryCollection(props.contentCollection)
    .all()
  )
</script>
