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

// Import root component
import App from './App.vue';

const myApp = createApp(App);

myApp.use(Quasar, {
  plugins: {
    // import Quasar plugins here
  },
});

myApp.use(router);

myApp.mount('#q-app');
