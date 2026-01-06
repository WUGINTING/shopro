import { configure } from 'quasar/wrappers';
import path from 'path';
import { fileURLToPath } from 'url';
import { loadEnv } from 'vite';

const __dirname = path.dirname(fileURLToPath(import.meta.url));

export default configure(function (ctx) {
  const env = loadEnv(ctx.mode, process.cwd());

  return {
    supportTS: false,

    boot: [],

    css: ['app.scss', 'animate.scss'],

    extras: ['roboto-font', 'material-icons'],

    build: {
      vueRouterMode: 'history',

      extendViteConf(viteConf) {
        // 路徑別名設定
        viteConf.resolve = viteConf.resolve || {};
        viteConf.resolve.alias = viteConf.resolve.alias || {};

        Object.assign(viteConf.resolve.alias, {
          '@': path.resolve(__dirname, 'src'),
          src: path.resolve(__dirname, 'src'),
          components: path.resolve(__dirname, 'src/components'),
          layouts: path.resolve(__dirname, 'src/layouts'),
          pages: path.resolve(__dirname, 'src/pages'),
          assets: path.resolve(__dirname, 'src/assets'),
          css: path.resolve(__dirname, 'src/css'),
        });

        // SCSS 配置 - 重新啟用 additionalData 讓每個 Vue 元件都能使用變數
        viteConf.css = viteConf.css || {};
        viteConf.css.preprocessorOptions =
          viteConf.css.preprocessorOptions || {};
        viteConf.css.preprocessorOptions.scss = {
          additionalData: `@import "${path
            .resolve(__dirname, 'src/css/variables.scss')
            .replace(/\\/g, '/')}";`,
          silenceDeprecations: ['legacy-js-api'],
        };
      },
    },

    devServer: {
      open: true,
      port: Number(env.VITE_PORT),
      host: '0.0.0.0',
      proxy: {
        '/api': {
          target: env.VITE_API_BASE_URL || 'http://localhost:8080',
          changeOrigin: true,
          secure: false,
          rewrite: path => path, // 如果後端有 /api 前綴
          // rewrite: (path) => path.replace(/^\/api/, '')  // 如果後端沒有 /api 前綴
        },
      },
    },

    framework: {
      config: {
        brand: {
          primary: '#F08304',
          secondary: '#ff6b35',
          accent: '#ffd700',
          dark: '#1a1a1a',
          positive: '#21ba45',
          negative: '#c10015',
          info: '#31ccec',
          warning: '#f2c037',
        },
      },
      plugins: ['Notify', 'Dialog', 'Loading'],
    },

    animations: 'all',
  };
});
