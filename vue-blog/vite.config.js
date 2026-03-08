// ...existing code...
import { defineConfig } from "vite";
// ...existing code...
// { changed code }
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'
import copy from "rollup-plugin-copy";
// ...existing code...
export default defineConfig(() => {
  return {
    plugins: [
      vue(),
    ],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      }
    },
    // 开发环境
    server: {
      host: "0.0.0.0",
      port: 5173,
      open: true,
      proxy: {
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, '')
        }
      }
    },
    build: {
      rollupOptions: {
        plugins: [
          copy({
            targets: [{ src: "node_modules/vditor/dist/*", dest: "dist/dist" }],
            hook: "writeBundle",
          }),
        ],
      },
    },
    // 开发环境
    preview: {
      host: "0.0.0.0",
      port: 8080,
      open: true,
      proxy: {
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, '')
        }
      }
    },
  };
});
