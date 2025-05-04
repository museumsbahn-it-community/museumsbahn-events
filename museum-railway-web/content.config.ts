import { defineCollection, defineContentConfig, z } from '@nuxt/content'

export default defineContentConfig({
  collections: {
    about: defineCollection({
      source: 'about/**/*.md',
      type: 'page'
    }),
    imprint: defineCollection({
      source: 'imprint/**/*.md',
      type: 'page'
    }),
    news: defineCollection({
      source: 'news/**/*.md',
      type: 'page',
      schema: z.object({
        date: z.string()
      })
    }),
    participate: defineCollection({
      source: 'participate/**/*.md',
      type: 'page'
    })
  }
})
