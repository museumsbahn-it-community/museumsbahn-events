<script setup lang="ts">

import remarkFrontmatter from 'remark-frontmatter';
import remarkParse from 'remark-parse';
import remarkStringify from 'remark-stringify';
import { onBeforeMount, ref } from 'vue';
import rehypeFormat from 'rehype-format';
import rehypeStringify from 'rehype-stringify';
import remarkRehype from 'remark-rehype';
import { unified } from 'unified';

const doc = `
# Pluto

Pluto is a dwarf planet in the Kuiper belt.

## Contents

## History

### Discovery

In the 1840s, Urbain Le Verrier used Newtonian mechanics to predict the position of…

### Name and symbol

The name Pluto is for the Roman god of the underworld, from a Greek epithet for Hades…

### Planet X disproved

Once Pluto was found, its faintness and lack of a viewable disc cast doubt…

## Orbit

Pluto's orbital period is about 248 years…
`;

const newsContent = ref('');
onBeforeMount(created);

async function created() {
  const data = await unified()
      .use(remarkParse)
      .use(remarkStringify)
      .use(remarkFrontmatter, ['yaml', 'toml'])
      .process(doc);

  console.log(String(data));

  const generatedHtml = await unified()
      .use(remarkParse)
      .use(remarkRehype)
      .use(rehypeFormat)
      .use(rehypeStringify)
      .process(doc);

  newsContent.value = String(generatedHtml.value);
}


</script>
<template>
  <h1>News</h1>

  <PCard>
    <template #content>
      Test Entry
      <div v-html="newsContent"></div>
    </template>
  </PCard>
</template>