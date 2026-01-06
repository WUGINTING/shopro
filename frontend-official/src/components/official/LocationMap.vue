<template>
  <div class="location-map">
    <div class="row q-gutter-lg items-stretch">
      <!-- 地圖區域 -->
      <div class="col-12 col-md-7">
        <q-card class="glass full-height" flat bordered>
          <div class="map-container">
            <iframe
              src="https://maps.google.com/maps?width=100%25&height=400&hl=zh&q=648%E9%9B%B2%E6%9E%97%E7%B8%A3%E8%A5%BF%E8%9E%BA%E9%8E%AE%E5%85%89%E5%BE%A9%E8%A5%BF%E8%B7%AF333-1%E8%99%9F+(伸遠國際)&t=&z=16&ie=UTF8&iwloc=B&output=embed"
              width="100%"
              height="100%"
              style="border: 0; border-radius: 12px"
              allowfullscreen=""
              loading="lazy"
              referrerpolicy="no-referrer-when-downgrade"
              class="google-map"
              title="双台居酒屋位置地圖"
            ></iframe>
          </div>
        </q-card>
      </div>

      <!-- 資訊區域 -->
      <div class="col-12 col-md-4">
        <q-card
          class="glass q-pa-lg full-height location-info-card"
          flat
          bordered
        >
          <div class="text-h5 text-weight-bold q-mb-lg">店面位置</div>

          <div class="location-info">
            <div class="info-item q-mb-md">
              <Icon
                icon="mdi:map-marker-outline"
                class="text-secondary q-mr-sm"
              />
              <div>
                <div class="text-weight-medium">地址</div>
                <div class="text-grey-4 text-body2">
                  648雲林縣西螺鎮光復西路333-1號
                </div>
              </div>
            </div>

            <div class="info-item q-mb-md">
              <Icon icon="mdi:phone-outline" class="text-secondary q-mr-sm" />
              <div>
                <div class="text-weight-medium">電話</div>
                <div class="text-grey-4 text-body2">05-5880870</div>
              </div>
            </div>

            <div class="info-item q-mb-md">
              <Icon icon="mdi:clock-outline" class="text-secondary q-mr-sm" />
              <div>
                <div class="text-weight-medium">營業時間</div>
                <div class="text-grey-4 text-body2">
                  <div>中午：11:00 - 13:00</div>
                  <div>下午：17:00 - 01:00</div>
                  <!-- <div class="text-caption text-grey-5">週一公休</div> -->
                </div>
              </div>
            </div>

            <div class="info-item q-mb-lg">
              <Icon icon="mdi:subway" class="text-secondary q-mr-sm" />
              <div>
                <div class="text-weight-medium">交通資訊</div>
                <div class="text-grey-4 text-body2">
                  <div>近西螺慈惠堂</div>
                </div>
              </div>
            </div>

            <q-separator class="q-my-lg" color="grey-7" />

            <div class="text-center">
              <q-btn
                class="btn-primary full-width q-mb-sm"
                size="md"
                label="導航到店面"
                @click="openGoogleMaps"
              />
            </div>
          </div>
        </q-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Icon } from '@iconify/vue';

const openGoogleMaps = () => {
  const address = encodeURIComponent('648雲林縣西螺鎮光復西路333-1號');
  const url = `https://www.google.com/maps/search/?api=1&query=${address}`;
  window.open(url, '_blank');
};
</script>

<style lang="scss" scoped>
.location-map {
  .map-container {
    height: 100%;
    min-height: 450px;
    border-radius: $border-radius-lg;
    overflow: hidden;
    position: relative;

    .google-map {
      border-radius: 12px;
      transition: opacity 0.3s ease;

      &:hover {
        opacity: 0.95;
      }
    }
  }

  .location-info-card {
    display: flex;
    flex-direction: column;
    min-height: 450px;
  }

  .info-item {
    display: flex;
    align-items: flex-start;
    gap: 12px;
  }
}

@media (max-width: 768px) {
  .location-map {
    .map-container {
      min-height: 300px;
    }

    .location-info-card {
      min-height: 300px;
    }
  }
}
</style>
