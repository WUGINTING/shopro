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

    <!-- 聯絡表單 -->
    <section class="section-padding">
      <div class="container q-mx-auto contact-form-wrap">
        <div class="text-center q-mb-lg fade-in-up">
          <h2 class="text-h2 text-weight-bold q-mb-md">線上留言</h2>
          <div class="story-divider q-mx-auto q-mb-lg"></div>
          <p class="text-body1 text-grey-4">訂位、團購、活動合作或商品問題，留下訊息我們會盡快回覆</p>
        </div>

        <q-form ref="formRef" class="contact-form" @submit.prevent="submitForm">
          <div class="row q-col-gutter-md">
            <div class="col-12 col-sm-6">
              <q-input v-model="form.name" filled dark label="姓名 *" maxlength="50" :rules="[val => !!val?.trim() || '請輸入姓名']" />
            </div>
            <div class="col-12 col-sm-6">
              <q-input v-model="form.phone" filled dark label="電話" maxlength="20" />
            </div>
            <div class="col-12 col-sm-6">
              <q-input
                v-model="form.email"
                filled
                dark
                type="email"
                label="Email *"
                maxlength="100"
                :rules="[val => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val || '') || '請輸入正確的 Email']"
              />
            </div>
            <div class="col-12 col-sm-6">
              <q-select v-model="form.eventType" filled dark emit-value map-options label="詢問類型" :options="eventTypes" />
            </div>
            <div class="col-12">
              <q-input
                v-model="form.message"
                filled
                dark
                type="textarea"
                autogrow
                label="留言內容 *"
                maxlength="400"
                counter
                :rules="[val => (val || '').trim().length >= 5 || '請輸入至少 5 個字']"
              />
            </div>
          </div>
          <div class="text-center q-mt-md">
            <q-btn type="submit" class="btn-primary" size="lg" label="送出留言" :loading="submitting" />
          </div>
        </q-form>
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
import LocationMap from 'src/components/official/LocationMap.vue';
import http from 'src/utils/request.js';

const $q = useQuasar();
const submitting = ref(false);

const formRef = ref(null);
const form = reactive({
  name: '',
  phone: '',
  email: '',
  eventType: '',
  message: '',
});

const eventTypes = [
  { label: '商品 / 訂單問題', value: 'order' },
  { label: '團購 / 大量訂購', value: 'bulk' },
  { label: '企業活動', value: 'corporate' },
  { label: '婚宴慶典', value: 'wedding' },
  { label: '生日派對', value: 'birthday' },
  { label: '私人聚會', value: 'private' },
  { label: '其他活動', value: 'other' },
];

const submitForm = async () => {
  submitting.value = true;
  try {
    const subject = eventTypes.find(type => type.value === form.eventType)?.label || '一般詢問';
    await http.post('/storefront/contact', {
      name: form.name.trim(),
      phone: form.phone.trim(),
      email: form.email.trim(),
      subject,
      message: form.message.trim(),
    });
    $q.notify({
      message: '已收到您的留言，我們會盡快與您聯繫！',
      color: 'positive',
      position: 'top',
      timeout: 3000,
    });
    Object.keys(form).forEach(key => {
      form[key] = '';
    });
    formRef.value?.resetValidation();
  } catch (error) {
    // 錯誤訊息已由 request 攔截器顯示
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
.contact-form-wrap {
  max-width: 760px;
}

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
