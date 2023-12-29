import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/searchApi': {
        target: 'http://127.0.0.1:8082',
        secure: false,
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/searchApi/, ''),
      },
      '/api': {
        target: 'http://127.0.0.1:8080',
        secure: false,
        changeOrigin: true,
      },
    }
  }
})
