<template>
  <q-page>
    <!-- 聯絡我們英雄區 -->
    <section class="hero-section hero-medium hero-orange relative-position">
      <div class="hero-background">
        <q-img
          src="https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80"
          class="full-height full-width"
          style="height: 400px"
        />
        <div class="hero-overlay"></div>
      </div>

      <div class="hero-content absolute-center text-center">
        <div class="fade-in-up">
          <h1 class="text-h1 text-weight-bold q-mb-md text-shadow">聯絡我們</h1>
          <p class="text-h5 text-shadow">讓我們為您打造完美的用餐體驗</p>
        </div>
      </div>
    </section>

    <!-- 地圖區域 -->
    <section class="section-padding">
      <div class="container q-mx-auto">
        <div class="text-center q-mb-xl fade-in-up">
          <h2 class="text-h2 text-weight-bold q-mb-md">來店參觀</h2>
          <div class="story-divider q-mx-auto q-mb-lg"></div>
          <p class="text-body1 text-grey-4">
            歡迎蒞臨我們的店面，親身體驗双台的用餐環境
          </p>
        </div>

        <location-map />
      </div>
    </section>
  </q-page>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useQuasar } from 'quasar';
import { Icon } from '@iconify/vue';
import LocationMap from 'src/components/official/LocationMap.vue';

const $q = useQuasar();
const submitting = ref(false);

const form = reactive({
  name: '',
  phone: '',
  email: '',
  eventType: '',
  guests: '',
  eventDate: '',
  location: '',
  budget: '',
  message: '',
});

const eventTypes = [
  { label: '企業活動', value: 'corporate' },
  { label: '婚宴慶典', value: 'wedding' },
  { label: '生日派對', value: 'birthday' },
  { label: '私人聚會', value: 'private' },
  { label: '其他活動', value: 'other' },
];

const submitForm = async () => {
  submitting.value = true;

  try {
    // 模擬提交表單
    await new Promise(resolve => setTimeout(resolve, 2000));

    $q.notify({
      message: '預約申請已送出！我們將在24小時內與您聯繫。',
      color: 'positive',
      position: 'top',
      timeout: 3000,
      actions: [{ label: '關閉', color: 'white' }],
    });

    // 清空表單
    Object.keys(form).forEach(key => {
      form[key] = '';
    });
  } catch (error) {
    $q.notify({
      message: '送出失敗，請稍後再試或直接電話聯繫。',
      color: 'negative',
      position: 'top',
      timeout: 3000,
    });
  } finally {
    submitting.value = false;
  }
};

const openGoogleMaps = () => {
  const address = encodeURIComponent('648雲林縣西螺鎮光復西路333-1號');
  const url = `https://www.google.com/maps/search/?api=1&query=${address}`;
  window.open(url, '_blank');
};
</script>

<style lang="scss" scoped>
.contact-icon {
  width: 60px;
  height: 60px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.social-btn {
  background: rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;

  &:hover {
    background: rgba(255, 255, 255, 0.2);
    transform: scale(1.1);
  }
}

.contact-form {
  background: rgba(30, 30, 30, 0.8);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(220, 20, 60, 0.2);
}

.contact-form-content {
  :deep(.q-field--filled) {
    .q-field__control {
      background: rgba(255, 255, 255, 0.05);

      &:hover {
        background: rgba(255, 255, 255, 0.08);
      }
    }
  }

  :deep(.q-field--focused) {
    .q-field__control {
      background: rgba(255, 255, 255, 0.1);
    }
  }
}

@media (max-width: 768px) {
  .contact-form {
    margin-top: 40px;
  }
}
</style>
