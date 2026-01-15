import { createApp } from 'vue';
import { Quasar } from 'quasar';

// Import icon libraries
import '@quasar/extras/material-icons/material-icons.css';

// Import Quasar css
import 'quasar/src/css/index.sass';

// Import app style - 修正路徑
import 'src/css/app.scss';

// Import router
import router from 'src/router/index';

// Import Pinia store
import pinia from 'src/store/index';

// Import root component
import App from './App.vue';

const myApp = createApp(App);

// 必須先註冊 Pinia，因為 router 或組件可能會在初始化時使用 store
myApp.use(pinia);

myApp.use(Quasar, {
  plugins: {
    // import Quasar plugins here
  },
});

myApp.use(router);

myApp.mount('#q-app');
