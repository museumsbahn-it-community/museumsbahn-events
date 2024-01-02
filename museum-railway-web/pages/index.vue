<template>
  <PageContent>
    <ContentList path="/news" v-slot="{ list }">
      <div v-for="article in list" :key="article._path">
        <Card class="article-content px-5 py-2">
          <template #header>
            <h2>{{ article.title }}</h2>
            <p class="text-sm">{{ article.date }}</p>
          </template>
          <template #content>
            <ContentRenderer :value="article"/>
          </template>
        </Card>
      </div>
    </ContentList>
  </PageContent>
</template>