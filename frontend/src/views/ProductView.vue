<template>
  <q-page class="q-pa-md product-admin-page">
    <div class="page-container">
      <!-- Page Header -->
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">商品管理</div>
          <div class="text-caption text-grey-7">管理商品資料、價格、庫存與上架狀態</div>
        </div>
        <div class="row q-gutter-sm">
          <q-btn
            flat
            dense
            round
            icon="help_outline"
            color="grey-7"
            @click="handleStartTour"
          >
            <q-tooltip>商品管理導覽</q-tooltip>
          </q-btn>
          <q-btn
            color="primary"
            icon="add"
            label="新增商品"
            unelevated
            @click="showDialog = true; form = { name: '', description: '', price: 0, stock: 0, status: 'DRAFT', salesMode: 'NORMAL', categoryId: null }"
          />
        </div>
      </div>

      <div class="row q-col-gutter-md q-mb-md">
        <div class="col-12 col-sm-6 col-lg-3">
          <q-card class="metric-card metric-card--blue">
            <q-card-section>
              <div class="metric-label">商品總數</div>
              <div class="metric-value">{{ productMetrics.total }}</div>
              <div class="metric-sub">目前列表中的商品筆數</div>
            </q-card-section>
          </q-card>
        </div>
        <div class="col-12 col-sm-6 col-lg-3">
          <q-card class="metric-card metric-card--green">
            <q-card-section>
              <div class="metric-label">已上架商品</div>
              <div class="metric-value">{{ productMetrics.published }}</div>
              <div class="metric-sub">目前對外可販售商品</div>
            </q-card-section>
          </q-card>
        </div>
        <div class="col-12 col-sm-6 col-lg-3">
          <q-card class="metric-card metric-card--amber">
            <q-card-section>
              <div class="metric-label">草稿商品</div>
              <div class="metric-value">{{ productMetrics.draft }}</div>
              <div class="metric-sub">尚未上架或待補資料</div>
            </q-card-section>
          </q-card>
        </div>
        <div class="col-12 col-sm-6 col-lg-3">
          <q-card class="metric-card metric-card--rose">
            <q-card-section>
              <div class="metric-label">低庫存商品</div>
              <div class="metric-value">{{ productMetrics.lowStock }}</div>
              <div class="metric-sub">庫存小於等於 10</div>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <!-- 庫存警示提示 -->
      <q-banner v-if="inventoryAlerts.length > 0" class="bg-orange-1 q-mb-md" rounded>
        <template v-slot:avatar>
          <q-icon name="warning" color="orange" />
        </template>
        <div class="text-weight-bold">庫存警示</div>
        <div class="text-caption">
          有 {{ inventoryAlerts.length }} 個商品庫存不足，請及時補貨
        </div>
      </q-banner>

      <!-- Search and Filter Bar -->
      <q-card class="q-mb-md filter-card">
        <q-card-section>
          <div class="filter-card__top q-mb-md">
            <div>
              <div class="text-subtitle1 text-weight-bold">搜尋與篩選</div>
              <div class="text-caption text-grey-7">可依商品名稱、狀態與分類快速篩選，提升管理效率</div>
            </div>
            <q-chip dense square color="blue-1" text-color="blue-9">
              目前顯示 {{ products.length }} 筆
            </q-chip>
          </div>
          <div class="row q-col-gutter-md">
            <div class="col-12 col-sm-6 col-md-4">
              <q-input
                v-model="searchQuery"
                outlined
                dense
                placeholder="搜尋商品名稱"
                name="product-admin-search"
                autocomplete="off"
                clearable
              >
                <template v-slot:prepend>
                  <q-icon name="search" />
                </template>
              </q-input>
            </div>
            <div class="col-12 col-sm-6 col-md-3">
              <q-select
                v-model="statusFilter"
                outlined
                dense
                :options="statusOptions"
                option-value="value"
                option-label="label"
                emit-value
                map-options
                clearable
                placeholder="商品狀態"
              />
            </div>
            <div class="col-12 col-sm-6 col-md-3">
              <q-select
                  v-model="categoryFilter"
                  outlined
                  dense
                  :options="categoryOptions"
                  clearable
                  placeholder="商品分類"

                  option-value="value"
                  option-label="label"
                  emit-value
                  map-options
              />
            </div>
            <div class="col-12 col-sm-6 col-md-2">
              <q-btn color="primary" unelevated class="full-width" label="查詢" icon="search" />
            </div>
          </div>
        </q-card-section>
      </q-card>

      <!-- Products Table -->
      <q-card class="table-shell-card">
        <q-table
          :rows="products"
          :columns="columns"
          row-key="id"
          :loading="loading"
          :pagination="pagination"
          flat
          :grid="$q.screen.lt.md"
          class="product-table"
          aria-label="商品列表"
        >
          <!-- 行動版卡片 -->
          <template v-slot:item="props">
            <div class="q-pa-xs col-xs-12 col-sm-6">
              <q-card flat bordered class="mobile-product-card">
                <q-card-section class="row items-center">
                  <q-avatar v-if="getProductImageUrl(props.row)" rounded size="60px" class="q-mr-md">
                    <q-img :src="getProductImageUrl(props.row)" :ratio="1" loading="lazy" @error="handleImageError($event, props.row)">
                      <template v-slot:error>
                        <div class="absolute-full flex flex-center bg-grey-3">
                          <q-icon name="broken_image" color="grey-6" size="24px" />
                        </div>
                      </template>
                    </q-img>
                  </q-avatar>
                  <q-avatar v-else rounded size="60px" color="grey-3" class="q-mr-md">
                    <q-icon name="image" />
                  </q-avatar>
                  <div class="col">
                    <div class="text-weight-bold">{{ props.row.name }}</div>
                    <div class="text-primary text-weight-bold">${{ (props.row.price || 0).toFixed(2) }}</div>
                    <div class="row items-center q-gutter-xs q-mt-xs">
                      <q-badge :color="getStatusColor(props.row.status)" :label="getStatusLabel(props.row.status)" />
                      <q-badge
                        :color="props.row.stock > 10 ? 'positive' : props.row.stock > 0 ? 'warning' : 'negative'"
                        :label="'庫存: ' + props.row.stock"
                      />
                    </div>
                  </div>
                </q-card-section>
                <q-separator />
                <q-card-actions align="right">
                  <q-btn flat dense icon="edit" color="primary" @click="handleEdit(props.row)" aria-label="編輯商品">
                    <q-tooltip>編輯</q-tooltip>
                  </q-btn>
                  <q-btn
                    flat
                    dense
                    :icon="props.row.status === 'PUBLISHED' ? 'visibility_off' : 'visibility'"
                    :color="props.row.status === 'PUBLISHED' ? 'warning' : 'positive'"
                    @click="handlePublishToggle(props.row)"
                    :aria-label="props.row.status === 'PUBLISHED' ? '下架商品' : '上架商品'"
                  >
                    <q-tooltip>{{ props.row.status === 'PUBLISHED' ? '下架' : '上架' }}</q-tooltip>
                  </q-btn>
                  <q-btn flat dense icon="delete" color="negative" @click="handleDelete(props.row.id)" aria-label="刪除商品">
                    <q-tooltip>刪除</q-tooltip>
                  </q-btn>
                </q-card-actions>
              </q-card>
            </div>
          </template>
          <template v-slot:body-cell-image="props">
            <q-td :props="props">
              <q-avatar v-if="getProductImageUrl(props.row)" rounded size="50px">
                <q-img
                  :src="getProductImageUrl(props.row)"
                  :ratio="1"
                  loading="lazy"
                  @error="handleImageError($event, props.row)"
                >
                  <template v-slot:loading>
                    <q-spinner-dots color="primary" />
                  </template>
                  <template v-slot:error>
                    <div class="absolute-full flex flex-center bg-grey-3">
                      <q-icon name="broken_image" color="grey-6" size="24px" />
                    </div>
                  </template>
                </q-img>
              </q-avatar>
              <q-avatar v-else rounded size="50px" color="grey-3">
                <q-icon name="image" />
              </q-avatar>
            </q-td>
          </template>

          <template v-slot:body-cell-status="props">
            <q-td :props="props">
              <q-badge :color="getStatusColor(props.row.status)" :label="getStatusLabel(props.row.status)" />
            </q-td>
          </template>

          <template v-slot:body-cell-price="props">
            <q-td :props="props">
    <span class="text-weight-bold text-primary">
      ${{ (props.row.price || 0).toFixed(2) }}
    </span>
            </q-td>
          </template>

          <template v-slot:body-cell-stock="props">
            <q-td :props="props">
              <div class="row items-center no-wrap q-gutter-xs">
                <q-badge
                  :color="(props.row.stock ?? 0) > 10 ? 'positive' : (props.row.stock ?? 0) > 0 ? 'warning' : 'negative'"
                  :label="props.row.stock ?? 0"
                />
                <q-icon
                  v-if="(props.row.stock ?? 0) <= 10"
                  name="warning"
                  :color="(props.row.stock ?? 0) === 0 ? 'negative' : 'warning'"
                  size="16px"
                >
                  <q-tooltip>{{ (props.row.stock ?? 0) === 0 ? '已缺貨' : '庫存不足' }}</q-tooltip>
                </q-icon>
                <q-btn
                  v-if="(props.row.stock ?? 0) <= 10"
                  flat
                  dense
                  round
                  icon="add"
                  color="teal"
                  size="xs"
                  @click.stop="openRestockDialog(props.row)"
                >
                  <q-tooltip>快速補貨</q-tooltip>
                </q-btn>
              </div>
            </q-td>
          </template>

          <template v-slot:body-cell-actions="props">
            <q-td :props="props">
              <q-btn flat dense round icon="edit" color="primary" size="sm" @click="handleEdit(props.row)">
                <q-tooltip>編輯</q-tooltip>
              </q-btn>

              <q-btn
                flat
                dense
                round
                icon="inventory"
                color="teal"
                size="sm"
                @click="openRestockDialog(props.row)"
              >
                <q-tooltip>快速補貨</q-tooltip>
              </q-btn>

              <q-btn
                flat
                dense
                round
                :icon="props.row.status === 'PUBLISHED' ? 'visibility_off' : 'visibility'"
                :color="props.row.status === 'PUBLISHED' ? 'warning' : 'positive'"
                size="sm"
                @click="handlePublishToggle(props.row)"
              >
                <q-tooltip>{{ props.row.status === 'PUBLISHED' ? '下架' : '上架' }}</q-tooltip>
              </q-btn>

              <q-btn flat dense round icon="delete" color="negative" size="sm" @click="handleDelete(props.row.id)">
                <q-tooltip>刪除</q-tooltip>
              </q-btn>
            </q-td>
          </template>
        </q-table>
      </q-card>

      <!-- Add/Edit Dialog -->
      <q-dialog v-model="showDialog" persistent maximized-on-mobile>
        <q-card class="product-dialog-card" :style="$q.screen.lt.md ? 'width: 100%' : 'min-width: 800px; max-width: 90vw'">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">{{ form.id ? '編輯商品' : '新增商品' }}</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section class="product-dialog-card__body">
            <q-tabs v-model="dialogTab" class="text-grey product-dialog-tabs" active-color="primary" indicator-color="primary" align="left">
              <q-tab name="basic" label="基本資料" />
              <q-tab name="specifications" label="商品規格（SKU）" :disable="!form.id" />
              <q-tab name="description" label="商品描述區塊" :disable="!form.id" />
            </q-tabs>

            <q-tab-panels v-model="dialogTab" animated>
              <!-- 基本資訊標籤頁 -->
              <q-tab-panel name="basic">
                <q-form>
              <q-input
                v-model="form.name"
                label="商品名稱 *"
                outlined
                class="q-mb-md"
                :rules="[val => !!val || '請輸入商品名稱']"
              />

              <q-input
                v-model="form.description"
                label="商品描述"
                outlined
                type="textarea"
                rows="3"
                class="q-mb-md"
              />

              <div class="row q-col-gutter-md q-mb-md">
                <div class="col-6">
                  <q-input
                    v-model.number="form.price"
                    label="價格 *"
                    outlined
                    type="number"
                    prefix="$"
                    :rules="[val => val >= 0 || '價格不可小於 0']"
                  />
                </div>
                <div class="col-6">
                  <q-input
                    v-model.number="form.stock"
                    label="庫存 *"
                    outlined
                    type="number"
                    :rules="[val => val >= 0 || '庫存不可小於 0']"
                  />
                </div>
              </div>

              <div class="row q-col-gutter-md q-mb-md">
                <div class="col-6">
                  <q-select
                    v-model="form.status"
                    label="商品狀態 *"
                    outlined
                    :options="statusOptions"
                    option-value="value"
                    option-label="label"
                    emit-value
                    map-options
                  />
                </div>
                <div class="col-6">
                  <q-select
                    v-model="form.salesMode"
                    label="銷售模式 *"
                    outlined
                    :options="salesModeOptions"
                    option-value="value"
                    option-label="label"
                    emit-value
                    map-options
                  />
                </div>
              </div>
              <div class="row q-col-gutter-md q-mb-md">
                <div class="col-12">
                  <q-select
                      v-model="form.categoryId"
                      label="商品分類"
                      outlined
                      :options="categoryOptions"

                      option-value="value"
                      option-label="label"
                      emit-value
                      map-options
                  />
                </div>
              </div>

              <div class="product-image-setup q-mb-md">
                <div class="row items-start justify-between q-col-gutter-md">
                  <div class="col-12 col-md">
                    <div class="text-subtitle1 text-weight-bold">商品圖片設定</div>
                    <div class="text-caption text-grey-7">
                      第 1 張圖片會作為主圖顯示在商品列表與商品頁。
                    </div>
                  </div>
                  <div class="col-12 col-md-auto">
                    <q-chip dense square color="blue-1" text-color="blue-9" icon="collections">
                      已選 {{ selectedImageCount }} 張
                    </q-chip>
                  </div>
                </div>

                <q-banner dense rounded class="bg-blue-1 text-blue-10 q-mt-sm">
                  建議主圖使用清晰正方形圖片（例如 1200 x 1200），可提升前台列表辨識度。
                </q-banner>

                <div class="row q-col-gutter-md q-mt-sm">
                  <div class="col-12 col-md-6">
                    <q-card flat bordered class="image-setup-card">
                      <q-card-section class="q-pb-sm">
                        <div class="text-subtitle2 text-weight-medium">直接上傳圖片</div>
                        <div class="text-caption text-grey-7">上傳後會加入本次商品圖片清單。</div>
                      </q-card-section>
                      <q-card-section class="q-pt-none">
                        <q-file
                          v-model="productImage"
                          label="上傳商品圖片"
                          name="product-image-upload"
                          outlined
                          accept="image/*"
                          class="q-mb-sm"
                        >
                          <template v-slot:prepend>
                            <q-icon name="image" />
                          </template>
                        </q-file>
                        <div class="text-caption text-grey-7" v-if="selectedProductImageFileName">
                          已選檔案：{{ selectedProductImageFileName }}
                        </div>
                        <div class="text-caption text-grey-6" v-else>
                          可選 jpg / png / webp 等圖片格式
                        </div>
                      </q-card-section>
                    </q-card>
                  </div>

                  <div class="col-12 col-md-6">
                    <q-card flat bordered class="image-setup-card">
                      <q-card-section class="q-pb-sm">
                        <div class="text-subtitle2 text-weight-medium">從相簿選圖</div>
                        <div class="text-caption text-grey-7">適合重複使用品牌圖、活動圖或商品情境圖。</div>
                      </q-card-section>
                      <q-card-section class="q-pt-none">
                        <q-btn
                          outline
                          color="primary"
                          icon="photo_library"
                          label="開啟相簿選圖"
                          class="full-width"
                          @click="showAlbumSelector = true"
                          :disable="!form.id"
                        />
                        <div class="text-caption text-grey-7 q-mt-xs" v-if="!form.id">
                          請先儲存商品，再從相簿選擇圖片。
                        </div>
                        <div class="text-caption text-positive q-mt-xs" v-else>
                          已儲存商品後，可從相簿加入多張圖片。
                        </div>
                      </q-card-section>
                    </q-card>
                  </div>
                </div>

                <q-card flat bordered class="image-setup-card q-mt-md">
                  <q-card-section class="row items-center justify-between q-pb-sm">
                    <div>
                      <div class="text-subtitle2 text-weight-medium">已選圖片清單</div>
                      <div class="text-caption text-grey-7">可調整順序、設定主圖、移除不需要的圖片。</div>
                    </div>
                    <q-btn
                      flat
                      dense
                      color="negative"
                      icon="clear_all"
                      label="清空"
                      :disable="selectedAlbumImages.length === 0"
                      @click="clearSelectedImages"
                    />
                  </q-card-section>

                  <q-card-section class="q-pt-none">
                    <div v-if="form.id && selectedAlbumImages.length > 0" class="row q-col-gutter-sm">
                      <div
                        v-for="(img, index) in selectedAlbumImages"
                        :key="img.id"
                        class="col-12 col-sm-6 col-md-4 col-lg-3"
                      >
                        <div class="selected-image-card" :class="{ 'selected-image-card--primary': isPrimarySelectedImage(index) }">
                          <q-img :src="img.imageUrl" :ratio="1" class="selected-image-card__thumb">
                            <div class="absolute-top-left q-pa-xs row q-gutter-xs">
                              <q-chip
                                dense
                                square
                                size="sm"
                                :color="isPrimarySelectedImage(index) ? 'primary' : 'grey-3'"
                                :text-color="isPrimarySelectedImage(index) ? 'white' : 'grey-8'"
                              >
                                {{ isPrimarySelectedImage(index) ? '主圖' : `#${index + 1}` }}
                              </q-chip>
                            </div>
                            <div class="absolute-top-right q-pa-xs">
                              <q-btn
                                round
                                flat
                                color="white"
                                icon="close"
                                size="sm"
                                @click="removeSelectedImage(img.id)"
                                aria-label="移除圖片"
                              >
                                <q-tooltip>移除圖片</q-tooltip>
                              </q-btn>
                            </div>
                          </q-img>

                          <div class="selected-image-card__actions">
                            <q-btn
                              flat
                              dense
                              size="sm"
                              icon="star"
                              color="amber-8"
                              label="設為主圖"
                              :disable="isPrimarySelectedImage(index)"
                              @click="setSelectedImageAsPrimary(index)"
                            />
                            <div class="row q-gutter-xs">
                              <q-btn
                                flat
                                dense
                                round
                                size="sm"
                                icon="arrow_back"
                                :disable="index === 0"
                                @click="moveSelectedImage(index, -1)"
                                aria-label="圖片往前"
                              >
                                <q-tooltip>往前</q-tooltip>
                              </q-btn>
                              <q-btn
                                flat
                                dense
                                round
                                size="sm"
                                icon="arrow_forward"
                                :disable="index === selectedAlbumImages.length - 1"
                                @click="moveSelectedImage(index, 1)"
                                aria-label="圖片往後"
                              >
                                <q-tooltip>往後</q-tooltip>
                              </q-btn>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>

                    <div v-else class="image-setup-empty">
                      <q-icon name="photo_size_select_actual" size="36px" color="grey-5" />
                      <div class="q-mt-sm text-body2 text-grey-7">尚未加入商品圖片</div>
                      <div class="text-caption text-grey-6">可直接上傳，或先儲存商品後從相簿選圖。</div>
                    </div>
                  </q-card-section>
                </q-card>
              </div>
                </q-form>
              </q-tab-panel>

              <!-- 商品規格（SKU）標籤頁 -->
              <q-tab-panel name="specifications">
                <div v-if="!form.id" class="text-center text-grey q-pa-xl">
                  <q-icon name="info" size="48px" />
                  <div class="q-mt-md">請先儲存商品後，再管理商品規格</div>
                </div>

                <div v-else>
                  <div class="row items-center justify-between q-mb-md">
                    <div class="text-subtitle1 text-weight-bold">商品規格（SKU）管理</div>
                    <q-btn
                      unelevated
                      color="primary"
                      icon="add"
                      label="新增規格"
                      @click="showSpecDialog = true; specForm = { productId: form.id, specName: '', sku: '', price: 0, cost: 0, stock: 0, enabled: true }"
                    />
                  </div>

                  <!-- 規格列表 -->
                  <q-table
                    :rows="specifications"
                    :columns="specColumns"
                    row-key="id"
                    :loading="specLoading"
                    flat
                    bordered
                    class="q-mb-md"
                  >
                    <template v-slot:body-cell-image="props">
                      <q-td :props="props">
                        <q-avatar v-if="props.value" rounded size="50px">
                          <q-img :src="props.value" :ratio="1" />
                        </q-avatar>
                        <q-avatar v-else rounded size="50px" color="grey-3">
                          <q-icon name="image" />
                        </q-avatar>
                      </q-td>
                    </template>

                    <template v-slot:body-cell-price="props">
                      <q-td :props="props">
                        <span class="text-weight-bold text-primary">
                          ${{ (props.value || 0).toFixed(2) }}
                        </span>
                      </q-td>
                    </template>

                    <template v-slot:body-cell-stock="props">
                      <q-td :props="props">
                        <q-badge
                          :color="props.value > 10 ? 'positive' : props.value > 0 ? 'warning' : 'negative'"
                          :label="props.value || 0"
                        />
                      </q-td>
                    </template>

                    <template v-slot:body-cell-enabled="props">
                      <q-td :props="props">
                        <q-toggle
                          :model-value="props.value"
                          @update:model-value="toggleSpecEnabled(props.row, $event)"
                        />
                      </q-td>
                    </template>

                    <template v-slot:body-cell-actions="props">
                      <q-td :props="props">
                        <q-btn
                          flat
                          dense
                          round
                          icon="edit"
                          color="primary"
                          size="sm"
                          @click="editSpecification(props.row)"
                        >
                          <q-tooltip>編輯</q-tooltip>
                        </q-btn>
                        <q-btn
                          flat
                          dense
                          round
                          icon="delete"
                          color="negative"
                          size="sm"
                          @click="deleteSpecification(props.row.id)"
                        >
                          <q-tooltip>刪除</q-tooltip>
                        </q-btn>
                      </q-td>
                    </template>
                  </q-table>

                  <div v-if="specifications.length === 0" class="text-center text-grey q-pa-xl">
                    <q-icon name="inventory_2" size="64px" />
                    <div class="q-mt-md">尚未建立商品規格</div>
                    <div class="text-caption q-mt-xs">點擊右上角「新增規格」開始建立 SKU</div>
                  </div>
                </div>
              </q-tab-panel>

              <!-- 描述區塊標籤頁 -->
              <q-tab-panel name="description">
                <div v-if="!form.id" class="text-center text-grey q-pa-xl">
                  <q-icon name="info" size="48px" />
                  <div class="q-mt-md">請先保存商品後再編輯描述區塊</div>
                </div>

                <div v-else>
                  <!-- 手動區塊（區塊1~3） -->
                  <div class="q-mb-lg">
                    <div class="text-subtitle1 q-mb-md text-weight-bold">手動區塊（區塊1~3）</div>
                    <div v-for="blockNum in 3" :key="`manual-${blockNum}`" class="q-mb-md">
                      <q-card flat bordered>
                        <q-card-section>
                          <div class="text-subtitle2 q-mb-sm">區塊 {{ blockNum }}</div>
                          <q-input
                            v-model="manualBlocks[blockNum - 1].title"
                            label="區塊標題"
                            outlined
                            dense
                            class="q-mb-sm"
                          />
                          <q-input
                            v-model="manualBlocks[blockNum - 1].content"
                            label="區塊內容"
                            outlined
                            type="textarea"
                            rows="4"
                            class="q-mb-sm"
                          />
                          <q-input
                            v-model="manualBlocks[blockNum - 1].imageUrl"
                            label="區塊圖片URL"
                            outlined
                            dense
                          />
                          <q-toggle
                            v-model="manualBlocks[blockNum - 1].enabled"
                            label="啟用此區塊"
                            class="q-mt-sm"
                          />
                        </q-card-section>
                      </q-card>
                    </div>
                  </div>

                  <!-- 自動區塊（區塊1~7） -->
                  <div class="q-mb-lg">
                    <div class="row items-center justify-between q-mb-md">
                      <div class="text-subtitle1 text-weight-bold">自動區塊（區塊1~7）</div>
                      <q-btn
                        outline
                        color="primary"
                        label="初始化自動區塊"
                        icon="refresh"
                        size="sm"
                        @click="initializeAutoBlocks"
                      />
                    </div>
                    <div v-for="blockNum in 7" :key="`auto-${blockNum}`" class="q-mb-md">
                      <q-card flat bordered>
                        <q-card-section>
                          <div class="row items-center justify-between q-mb-sm">
                            <div class="text-subtitle2">自動區塊 {{ blockNum }}</div>
                            <q-badge v-if="autoBlocks[blockNum - 1]?.isAutoGenerated" color="info" label="自動生成" />
                          </div>
                          <q-input
                            v-model="autoBlocks[blockNum - 1].title"
                            label="區塊標題"
                            outlined
                            dense
                            class="q-mb-sm"
                          />
                          <q-input
                            v-model="autoBlocks[blockNum - 1].content"
                            label="區塊內容"
                            outlined
                            type="textarea"
                            rows="4"
                            class="q-mb-sm"
                          />
                          <q-input
                            v-model="autoBlocks[blockNum - 1].imageUrl"
                            label="區塊圖片URL"
                            outlined
                            dense
                          />
                          <q-toggle
                            v-model="autoBlocks[blockNum - 1].enabled"
                            label="啟用此區塊"
                            class="q-mt-sm"
                          />
                        </q-card-section>
                      </q-card>
                    </div>
                  </div>

                  <q-btn
                    unelevated
                    color="primary"
                    label="保存所有描述區塊"
                    icon="save"
                    class="full-width"
                    @click="saveDescriptionBlocks"
                  />
                </div>
              </q-tab-panel>
            </q-tab-panels>
          </q-card-section>

          <q-card-actions align="right" class="q-px-md q-pb-md product-dialog-card__actions">
            <q-btn flat label="取消" color="grey-7" @click="closeDialog" />
            <q-btn unelevated label="儲存" color="primary" @click="handleSubmit" />
            <q-btn v-if="form.id" unelevated label="完成" color="positive" @click="handleDone" />
          </q-card-actions>
        </q-card>
      </q-dialog>

      <!-- Specification Dialog -->
      <q-dialog v-model="showSpecDialog" persistent maximized-on-mobile>
        <q-card :style="$q.screen.lt.md ? 'width: 100%' : 'min-width: 600px; max-width: 90vw'">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">{{ specForm.id ? '編輯規格' : '新增規格' }}</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <q-form>
              <q-input
                v-model="specForm.specName"
                label="規格名稱 *"
                outlined
                class="q-mb-md"
                hint="例如：顏色:紅色,尺寸:L"
                :rules="[val => !!val || '請輸入規格名稱']"
              />

              <q-input
                v-model="specForm.sku"
                label="規格 SKU"
                outlined
                class="q-mb-md"
                hint="規格編號，需唯一"
              />

              <div class="row q-col-gutter-md q-mb-md">
                <div class="col-6">
                  <q-input
                    v-model.number="specForm.price"
                    label="規格價格 *"
                    outlined
                    type="number"
                    prefix="¥"
                    :rules="[val => val >= 0 || '價格不能為負數']"
                  />
                </div>
                <div class="col-6">
                  <q-input
                    v-model.number="specForm.cost"
                    label="規格成本"
                    outlined
                    type="number"
                    prefix="¥"
                  />
                </div>
              </div>

              <div class="row q-col-gutter-md q-mb-md">
                <div class="col-6">
                  <q-input
                    v-model.number="specForm.stock"
                    label="庫存數量 *"
                    outlined
                    type="number"
                    :rules="[val => val >= 0 || '庫存不能為負數']"
                  />
                </div>
                <div class="col-6">
                  <q-input
                    v-model.number="specForm.weight"
                    label="重量（克）"
                    outlined
                    type="number"
                  />
                </div>
              </div>

              <q-input
                v-model="specForm.image"
                label="規格圖片URL"
                outlined
                class="q-mb-md"
                hint="規格專屬圖片"
              />

              <q-toggle
                v-model="specForm.enabled"
                label="啟用此規格"
                class="q-mb-md"
              />
            </q-form>
          </q-card-section>

          <q-card-actions align="right" class="q-px-md q-pb-md">
            <q-btn flat label="取消" color="grey-7" v-close-popup />
            <q-btn unelevated label="儲存" color="primary" @click="saveSpecification" />
          </q-card-actions>
        </q-card>
      </q-dialog>

      <!-- Album Image Selector Dialog -->
      <q-dialog v-model="showAlbumSelector" persistent maximized-on-mobile>
        <q-card :style="$q.screen.lt.md ? 'width: 100%' : 'min-width: 800px; max-width: 90vw'">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">從相冊選擇圖片</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <!-- Album Selection -->
            <q-select
              v-model="selectedAlbum"
              label="選擇相冊"
              outlined
              :options="albums"
              option-label="name"
              option-value="id"
              emit-value
              map-options
              class="q-mb-md"
              @update:model-value="loadAlbumImages"
            >
              <template v-slot:prepend>
                <q-icon name="photo_library" />
              </template>
            </q-select>

            <!-- Images Grid -->
            <div v-if="albumImages.length > 0">
              <div class="text-subtitle2 q-mb-md">選擇圖片（可多選）</div>
              <div class="row q-col-gutter-md" style="max-height: 400px; overflow-y: auto">
                <div
                  v-for="image in albumImages"
                  :key="image.id"
                  class="col-6 col-sm-4 col-md-3"
                >
                  <q-card
                    flat
                    bordered
                    class="cursor-pointer"
                    :class="{ 'bg-blue-1 border-primary': isImageSelected(image.id) }"
                    @click="toggleImageSelection(image)"
                  >
                    <q-img
                      :src="image.imageUrl"
                      :ratio="1"
                      style="height: 150px"
                    >
                      <div v-if="isImageSelected(image.id)" class="absolute-top-right q-ma-sm">
                        <q-icon name="check_circle" color="primary" size="md" />
                      </div>
                    </q-img>
                    <q-card-section v-if="image.title" class="q-pa-sm">
                      <div class="text-caption text-weight-medium ellipsis">
                        {{ image.title }}
                      </div>
                    </q-card-section>
                  </q-card>
                </div>
              </div>
            </div>

            <div v-else-if="selectedAlbum" class="text-center text-grey q-pa-xl">
              <q-icon name="image" size="64px" />
              <div class="q-mt-md">此相冊暫無圖片</div>
            </div>

            <div v-else class="text-center text-grey q-pa-xl">
              <q-icon name="photo_library" size="64px" />
              <div class="q-mt-md">請選擇相冊</div>
            </div>
          </q-card-section>

          <q-card-actions align="right" class="q-px-md q-pb-md">
            <q-btn flat label="取消" color="grey-7" v-close-popup />
            <q-btn
              unelevated
              label="確認添加"
              color="primary"
              @click="addSelectedImagesToProduct"
              :disable="tempSelectedImages.length === 0"
            />
          </q-card-actions>
        </q-card>
      </q-dialog>

      <!-- 快速補貨對話框 -->
      <q-dialog v-model="showRestockDialog">
        <q-card style="min-width: 350px">
          <q-card-section>
            <div class="text-h6">
              <q-icon name="inventory" color="teal" class="q-mr-sm" />
              快速補貨
            </div>
          </q-card-section>

          <q-card-section class="q-pt-none">
            <div v-if="restockProduct" class="q-mb-md">
              <div class="text-subtitle2 text-weight-bold">{{ restockProduct.name }}</div>
              <div class="text-caption text-grey-7">
                目前庫存：
                <q-badge
                  :color="(restockProduct.stock ?? 0) > 10 ? 'positive' : (restockProduct.stock ?? 0) > 0 ? 'warning' : 'negative'"
                >
                  {{ restockProduct.stock ?? 0 }}
                </q-badge>
              </div>
            </div>

            <q-input
              v-model.number="restockQuantity"
              type="number"
              label="補貨數量"
              outlined
              dense
              :min="1"
              :rules="[val => val > 0 || '數量必須大於 0']"
            >
              <template v-slot:prepend>
                <q-icon name="add_shopping_cart" />
              </template>
            </q-input>

            <div class="row q-gutter-sm q-mt-sm">
              <q-btn dense flat label="+10" color="grey-7" @click="restockQuantity += 10" />
              <q-btn dense flat label="+50" color="grey-7" @click="restockQuantity += 50" />
              <q-btn dense flat label="+100" color="grey-7" @click="restockQuantity += 100" />
              <q-btn dense flat label="+500" color="grey-7" @click="restockQuantity += 500" />
            </div>
          </q-card-section>

          <q-card-actions align="right">
            <q-btn flat label="取消" color="grey-7" v-close-popup />
            <q-btn
              unelevated
              label="確認補貨"
              color="teal"
              :loading="restockLoading"
              @click="handleRestock"
            />
          </q-card-actions>
        </q-card>
      </q-dialog>

    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, nextTick, watch } from 'vue'
import { useQuasar } from 'quasar'
import { productApi, categoryApi, productDescriptionBlockApi, productSpecificationApi, type Product, type ProductCategory, type ProductDescriptionBlock, type ProductSpecification, type PageResponse } from '@/api'
import { albumApi, type Album, type AlbumImage } from '@/api/album'
import { inventoryApi, type InventoryAlert } from '@/api/inventory'
import { startProductTour, isProductTourCompleted } from '@/utils/productTour'
import { useDebouncedRef } from '@/composables/useDebounce'

const $q = useQuasar()

const products = ref<Product[]>([])
const loading = ref(false)
const showDialog = ref(false)
const dialogTab = ref('basic')
const searchQuery = ref('')
const debouncedSearchQuery = useDebouncedRef(searchQuery, 300)
const statusFilter = ref(null)
const categoryFilter = ref(null)
const productImage = ref(null)

// 監聽防抖後的搜尋查詢，自動過濾商品
watch(debouncedSearchQuery, () => {
  // 搜尋時可以在這裡觸發 API 或本地過濾
})

// 商品規格相關狀態
const specifications = ref<ProductSpecification[]>([])
const specLoading = ref(false)
const showSpecDialog = ref(false)
const specForm = ref<ProductSpecification>({
  productId: undefined,
  specName: '',
  sku: '',
  price: 0,
  cost: 0,
  stock: 0,
  image: '',
  weight: undefined,
  enabled: true
})

const specColumns = [
  { name: 'specName', label: '規格名稱', align: 'left' as const, field: 'specName', sortable: true },
  { name: 'sku', label: 'SKU', align: 'left' as const, field: 'sku', sortable: true },
  { name: 'image', label: '圖片', align: 'center' as const, field: 'image' },
  { name: 'price', label: '價格', align: 'left' as const, field: 'price', sortable: true },
  { name: 'cost', label: '成本', align: 'left' as const, field: 'cost', sortable: true },
  { name: 'stock', label: '庫存', align: 'center' as const, field: 'stock', sortable: true },
  { name: 'enabled', label: '啟用', align: 'center' as const, field: 'enabled' },
  { name: 'actions', label: '操作', align: 'center' as const, field: 'actions' }
]

// 描述區塊相關狀態
const manualBlocks = ref<ProductDescriptionBlock[]>([
  { blockType: 'MANUAL', blockNumber: 1, title: '', content: '', enabled: true },
  { blockType: 'MANUAL', blockNumber: 2, title: '', content: '', enabled: true },
  { blockType: 'MANUAL', blockNumber: 3, title: '', content: '', enabled: true }
])
const autoBlocks = ref<ProductDescriptionBlock[]>([
  { blockType: 'AUTO', blockNumber: 1, title: '', content: '', enabled: true, isAutoGenerated: true },
  { blockType: 'AUTO', blockNumber: 2, title: '', content: '', enabled: true, isAutoGenerated: true },
  { blockType: 'AUTO', blockNumber: 3, title: '', content: '', enabled: true, isAutoGenerated: true },
  { blockType: 'AUTO', blockNumber: 4, title: '', content: '', enabled: true, isAutoGenerated: true },
  { blockType: 'AUTO', blockNumber: 5, title: '', content: '', enabled: true, isAutoGenerated: true },
  { blockType: 'AUTO', blockNumber: 6, title: '', content: '', enabled: true, isAutoGenerated: true },
  { blockType: 'AUTO', blockNumber: 7, title: '', content: '', enabled: true, isAutoGenerated: true }
])

// Album-related state
const showAlbumSelector = ref(false)
const albums = ref<Album[]>([])
const selectedAlbum = ref<number | null>(null)
const albumImages = ref<AlbumImage[]>([])
const tempSelectedImages = ref<AlbumImage[]>([])
const selectedAlbumImages = ref<AlbumImage[]>([])
const defaultAlbumId = ref<number | null>(null)

// 庫存管理相關狀態
const inventoryAlerts = ref<InventoryAlert[]>([])
const showRestockDialog = ref(false)
const restockProduct = ref<Product | null>(null)
const restockQuantity = ref(0)
const restockLoading = ref(false)
const selectedProductImageFileName = computed(() => {
  const file = productImage.value as File | File[] | null
  if (!file) return ''
  if (Array.isArray(file)) return file[0]?.name || ''
  return file.name || ''
})
const selectedImageCount = computed(() => {
  const uploadCount = selectedProductImageFileName.value ? 1 : 0
  return selectedAlbumImages.value.length + uploadCount
})

const form = ref<Product>({
  name: '',
  description: '',
  price: 0,
  stock: 0,
  status: 'DRAFT',
  salesMode: 'NORMAL',
  categoryId: null
})

const pagination = ref({
  page: 1,
  rowsPerPage: 10
})

const columns = [
  { name: 'image', label: '圖片', align: 'left' as const, field: 'image' },
  { name: 'id', label: 'ID', align: 'left' as const, field: 'id', sortable: true },
  { name: 'name', label: '商品名稱', align: 'left' as const, field: 'name', sortable: true },
  { name: 'price', label: '價格', align: 'left' as const, field: 'price', sortable: true },
  { name: 'stock', label: '庫存', align: 'center' as const, field: 'stock', sortable: true },
  { name: 'status', label: '狀態', align: 'center' as const, field: 'status' },
  { name: 'actions', label: '操作', align: 'center' as const, field: 'actions' }
]

const statusOptions = [
  { label: '草稿', value: 'DRAFT' },
  { label: '已上架', value: 'PUBLISHED' },
  { label: '已下架', value: 'UNPUBLISHED' }
]

const salesModeOptions = [
  { label: '一般販售', value: 'NORMAL' },
  { label: '預購商品', value: 'PRE_ORDER' },
  { label: '票券商品', value: 'TICKET' },
  { label: '訂閱商品', value: 'SUBSCRIPTION' },
  { label: '門市限定', value: 'STORE_ONLY' }
]

const categories = ref<ProductCategory[]>([])
const categoryOptions = computed(() => {
  return categories.value.map(cat => ({
    label: cat.name,
    value: cat.id
  })).filter(opt => opt.value !== undefined)
})

const productMetrics = computed(() => {
  const list = products.value
  return {
    total: list.length,
    published: list.filter((p) => p.status === 'PUBLISHED').length,
    draft: list.filter((p) => p.status === 'DRAFT').length,
    lowStock: list.filter((p) => Number(p.stock || 0) <= 10).length
  }
})

const loadProducts = async () => {
  loading.value = true
  try {
    const response = await productApi.getProducts()
    console.log('[loadProducts] API 回應:', response)
    const data = response.data as PageResponse<Product> | Product[]
    let productList: Product[] = []
    if (Array.isArray(data)) {
      productList = data
    } else if (data && 'content' in data) {
      productList = data.content
    }

    // 調試：輸出有圖片的商品
    const productsWithImages = productList.filter(p => p.images && p.images.length > 0)
    console.log('[loadProducts] 有圖片的商品:', productsWithImages.map(p => ({ id: p.id, name: p.name, images: p.images })))

    // 將 basePrice/salePrice 轉換為 price（優先使用 salePrice，如果沒有則使用 basePrice）
    // 並將後端狀態值轉換為前端狀態值
    products.value = productList.map(product => {
      let status = product.status
      // 後端狀態值轉換為前端狀態值
      if (status === 'ACTIVE') {
        status = 'PUBLISHED'
      } else if (status === 'INACTIVE') {
        status = 'UNPUBLISHED'
      }
      // DRAFT 保持不變

      return {
        ...product,
        status: status as 'DRAFT' | 'PUBLISHED' | 'UNPUBLISHED',
        price: product.salePrice ?? product.basePrice ?? 0
      }
    })
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '載入商品清單失敗',
      position: 'top'
    })
    console.error('API Error:', error)
  } finally {
    loading.value = false
  }
}

// 載入庫存警示
const loadInventoryAlerts = async () => {
  try {
    const response = await inventoryApi.getUnresolvedAlerts()
    if (response.success && response.data) {
      inventoryAlerts.value = response.data
    }
  } catch (error) {
    console.warn('載入庫存警示失敗:', error)
    inventoryAlerts.value = []
  }
}

// 獲取商品的庫存警示級別
const getProductAlertLevel = (productId: number): string | null => {
  const alert = inventoryAlerts.value.find(a => a.productId === productId)
  return alert?.alertLevel || null
}

// 開啟快速補貨對話框
const openRestockDialog = (product: Product) => {
  restockProduct.value = product
  restockQuantity.value = 100 // 預設補貨數量
  showRestockDialog.value = true
}

// 執行快速補貨
const handleRestock = async () => {
  if (!restockProduct.value?.id || restockQuantity.value <= 0) return

  restockLoading.value = true
  try {
    await inventoryApi.updateInventory(restockProduct.value.id, restockQuantity.value)
    $q.notify({
      type: 'positive',
      message: `已為「${restockProduct.value.name}」補貨 ${restockQuantity.value} 件`,
      position: 'top'
    })
    showRestockDialog.value = false
    // 重新載入庫存警示
    await loadInventoryAlerts()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '補貨失敗，請稍後再試',
      position: 'top'
    })
    console.error('補貨失敗:', error)
  } finally {
    restockLoading.value = false
  }
}

const handleEdit = async (product: Product) => {
  // 將 basePrice/salePrice 轉換為 price（優先使用 salePrice，如果沒有則使用 basePrice）
  // 並將後端狀態值轉換為前端狀態值
  let status = product.status
  if (status === 'ACTIVE') {
    status = 'PUBLISHED'
  } else if (status === 'INACTIVE') {
    status = 'UNPUBLISHED'
  }
  // DRAFT 保持不變
  
  form.value = {
    ...product,
    status: status as 'DRAFT' | 'PUBLISHED' | 'UNPUBLISHED',
    price: product.salePrice ?? product.basePrice ?? 0
  }
  showDialog.value = true
  dialogTab.value = 'basic'
  
  // 載入商品規格和描述區塊
  if (product.id) {
    await loadSpecifications(product.id)
    await loadDescriptionBlocks(product.id)
  }

  // Load existing album images for this product if it has images
  if (product.id && product.images && product.images.length > 0) {
    try {
      // Try to match product images with album images
      // This is a best-effort approach since we need to query albums for matching URLs
      selectedAlbumImages.value = []

      // 先提取所有商品圖片的 URL
      const productImageUrls: string[] = product.images.map(img => {
        if (typeof img === 'string') {
          return img
        } else if (img && typeof img === 'object' && 'imageUrl' in img) {
          return img.imageUrl
        }
        return ''
      }).filter(url => url !== '')

      // Load all albums and their images to find matches
      const albumsResponse = await albumApi.getAlbums({ page: 0, size: 100 })
      if (albumsResponse.success && albumsResponse.data) {
        const allAlbums = albumsResponse.data.content || []

        // For each album, load images and check if they match product images
        for (const album of allAlbums) {
          if (album.id) {
            const imagesResponse = await albumApi.getAlbumImages(album.id)
            if (imagesResponse.success && imagesResponse.data) {
              const albumImages = imagesResponse.data
              // Check if any product images match album images
              for (const productImageUrl of productImageUrls) {
                const matchingAlbumImage = albumImages.find(
                  (albumImg) => albumImg.imageUrl === productImageUrl
                )
                if (matchingAlbumImage && !selectedAlbumImages.value.some(img => img.id === matchingAlbumImage.id)) {
                  selectedAlbumImages.value.push(matchingAlbumImage)
                }
              }
            }
          }
        }
      }
    } catch (error) {
      console.error('Failed to load existing album images:', error)
    }
  } else {
    selectedAlbumImages.value = []
  }
}

const handlePublishToggle = async (product: Product) => {
  try {
    if (product.status === 'PUBLISHED') {
      await productApi.deactivateProduct(product.id!)
      $q.notify({
        type: 'positive',
        message: '商品已下架',
        position: 'top'
      })
    } else {
      await productApi.activateProduct(product.id!)
      $q.notify({
        type: 'positive',
        message: '商品已上架',
        position: 'top'
      })
    }
    loadProducts()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '操作失敗',
      position: 'top'
    })
  }
}

const handleDelete = (id?: number) => {
  if (!id) return

  $q.dialog({
    title: '確認刪除',
    message: '確定要刪除這個商品嗎？此操作無法復原。',
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await productApi.deleteProduct(id)
      $q.notify({
        type: 'positive',
        message: '刪除成功',
        position: 'top'
      })
      loadProducts()
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: '操作失敗',
        position: 'top'
      })
    }
  })
}
const handleSubmit = async () => {
  try {
    // 1. 複製一份表單資料，避免直接修改影響 UI 顯示
    // 使用 'any' 類型轉換是為了方便刪除屬性而不報錯
    const payload: any = { ...form.value }

    // 2. --- 關鍵修改：欄位轉換 ---
    // 將前端的 'price' 填入後端需要的價格欄位
    payload.basePrice = form.value.price
    payload.salePrice = form.value.price
    payload.costPrice = 0 // 必須給個預設值，否則後端 @DecimalMin 檢查可能會擋

    // 3. 狀態值轉換：將前端狀態值轉換為後端可接受的狀態值
    // 前端: PUBLISHED/UNPUBLISHED  -> 後端: ACTIVE/INACTIVE
    if (payload.status === 'PUBLISHED') {
      payload.status = 'ACTIVE'
    } else if (payload.status === 'UNPUBLISHED') {
      payload.status = 'INACTIVE'
    }
    // DRAFT 保持不變

    // 4. 移除後端不認識的欄位
    delete payload.price // 後端沒有 'price' 欄位，刪掉避免報錯

    // !!! 注意 !!!
    // 如果您還沒在 Java DTO 加入 'stock' 欄位，請把下面這行取消註解，否則後端會報錯
    // delete payload.stock

    // 5. 先創建或更新商品（需要先有商品ID才能添加圖片）
    let productId = form.value.id
    if (!productId) {
      const response = await productApi.createProduct(payload)
      // 更新 form ID 以便後續操作
      if (response.data && response.data.id) {
        form.value.id = response.data.id
        productId = response.data.id
      }
    } else {
      await productApi.updateProduct(productId, payload)
    }

    // 6. 處理直接上傳的圖片（需要商品ID）
    let uploadedImageId: number | null = null
    if (productImage.value && productId) {
      // 確保預設相冊存在
      if (!defaultAlbumId.value) {
        await ensureDefaultAlbum()
      }

      if (defaultAlbumId.value) {
        try {
          // 將圖片上傳到預設相冊
          const uploadResponse = await albumApi.uploadImage(
            defaultAlbumId.value,
            productImage.value as File,
            `商品圖片 - ${form.value.name || '未命名'}`
          )

          if (uploadResponse.success && uploadResponse.data && uploadResponse.data.id) {
            uploadedImageId = uploadResponse.data.id
            // 將上傳的圖片添加到 selectedAlbumImages（用於顯示）
            selectedAlbumImages.value.push(uploadResponse.data)
          }
        } catch (uploadError) {
          console.error('圖片上傳失敗:', uploadError)
          $q.notify({
            type: 'negative',
            message: '圖片上傳失敗',
            position: 'top'
          })
        }
      }
    }

    // 7. 收集所有要添加到商品的圖片ID（包括新上傳的和已選中的相冊圖片）
    const imageIdsToAdd: number[] = []
    
    // 添加新上傳的圖片ID
    if (uploadedImageId) {
      imageIdsToAdd.push(uploadedImageId)
    }
    
    // 添加已選中的相冊圖片ID
    const selectedImageIds = selectedAlbumImages.value
      .map(img => img.id)
      .filter((id): id is number => id !== undefined && id !== uploadedImageId) // 避免重複添加
    
    imageIdsToAdd.push(...selectedImageIds)

    // 8. 將所有圖片添加到商品
    if (productId && imageIdsToAdd.length > 0) {
      try {
        await productApi.addAlbumImages(productId, imageIdsToAdd)
      } catch (error) {
        console.error('添加圖片到商品失敗:', error)
        $q.notify({
          type: 'negative',
          message: '添加圖片到商品失敗',
          position: 'top'
        })
      }
    }

    // 9. 顯示成功訊息
    $q.notify({
      type: 'positive',
      message: productId ? '更新成功' : '建立成功',
      position: 'top'
    })

    // 10. 重置圖片上傳欄位
    productImage.value = null

    loadProducts()
    return true
  } catch (error) {
    console.error(error) // 建議印出錯誤以便除錯
    $q.notify({
      type: 'negative',
      message: '操作失敗',
      position: 'top'
    })
    return false
  }
}

const handleDone = async () => {
  const success = await handleSubmit()
  if (success) {
    closeDialog()
  }
}

const closeDialog = async () => {
  showDialog.value = false
  dialogTab.value = 'basic'
  form.value = { name: '', description: '', price: 0, stock: 0, status: 'DRAFT', salesMode: 'NORMAL', categoryId: null }
  selectedAlbumImages.value = []
  productImage.value = null
  // 重置規格和描述區塊
  specifications.value = []
  resetDescriptionBlocks()
}

// 載入商品規格
const loadSpecifications = async (productId: number) => {
  specLoading.value = true
  try {
    const response = await productSpecificationApi.getProductSpecifications(productId)
    if (response.success && response.data) {
      specifications.value = response.data
    }
  } catch (error) {
    console.error('載入商品規格失敗:', error)
    $q.notify({
      type: 'negative',
      message: '載入商品規格失敗',
      position: 'top'
    })
  } finally {
    specLoading.value = false
  }
}

// 保存規格
const saveSpecification = async () => {
  try {
    if (!specForm.value.specName) {
      $q.notify({
        type: 'negative',
        message: '請輸入規格名稱',
        position: 'top'
      })
      return
    }

    if (!form.value.id) {
      $q.notify({
        type: 'negative',
        message: '請先保存商品',
        position: 'top'
      })
      return
    }

    specForm.value.productId = form.value.id

    if (specForm.value.id) {
      // 更新規格
      await productSpecificationApi.updateSpecification(specForm.value.id, specForm.value)
      $q.notify({
        type: 'positive',
        message: '規格已更新',
        position: 'top'
      })
    } else {
      // 新增規格
      await productSpecificationApi.addSpecification(specForm.value)
      $q.notify({
        type: 'positive',
        message: '規格已新增',
        position: 'top'
      })
    }

    showSpecDialog.value = false
    specForm.value = {
      productId: form.value.id,
      specName: '',
      sku: '',
      price: 0,
      cost: 0,
      stock: 0,
      image: '',
      weight: undefined,
      enabled: true
    }

    // 重新載入規格列表
    if (form.value.id) {
      await loadSpecifications(form.value.id)
    }
  } catch (error) {
    console.error('保存規格失敗:', error)
    $q.notify({
      type: 'negative',
      message: '保存規格失敗',
      position: 'top'
    })
  }
}

// 編輯規格
const editSpecification = (spec: ProductSpecification) => {
  specForm.value = { ...spec }
  showSpecDialog.value = true
}

// 刪除規格
const deleteSpecification = (specId?: number) => {
  if (!specId) return

  $q.dialog({
    title: '確認刪除',
    message: '確定要刪除這個規格嗎？此操作無法復原。',
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await productSpecificationApi.deleteSpecification(specId)
      $q.notify({
        type: 'positive',
        message: '規格已刪除',
        position: 'top'
      })
      if (form.value.id) {
        await loadSpecifications(form.value.id)
      }
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: '刪除規格失敗',
        position: 'top'
      })
    }
  })
}

// 切換規格啟用狀態
const toggleSpecEnabled = async (spec: ProductSpecification, enabled: boolean) => {
  try {
    await productSpecificationApi.updateSpecification(spec.id!, { ...spec, enabled })
    if (form.value.id) {
      await loadSpecifications(form.value.id)
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '更新規格狀態失敗',
      position: 'top'
    })
  }
}

// 載入描述區塊
const loadDescriptionBlocks = async (productId: number) => {
  try {
    const response = await productDescriptionBlockApi.getProductBlocks(productId)
    if (response.success && response.data) {
      const blocks = response.data
      
      // 分離手動區塊和自動區塊
      const manual = blocks.filter(b => b.blockType === 'MANUAL').sort((a, b) => (a.blockNumber || 0) - (b.blockNumber || 0))
      const auto = blocks.filter(b => b.blockType === 'AUTO').sort((a, b) => (a.blockNumber || 0) - (b.blockNumber || 0))
      
      // 更新手動區塊
      for (let i = 0; i < 3; i++) {
        if (manual[i]) {
          manualBlocks.value[i] = { ...manualBlocks.value[i], ...manual[i] }
        } else {
          manualBlocks.value[i] = {
            blockType: 'MANUAL',
            blockNumber: i + 1,
            title: '',
            content: '',
            enabled: true
          }
        }
      }
      
      // 更新自動區塊
      for (let i = 0; i < 7; i++) {
        if (auto[i]) {
          autoBlocks.value[i] = { ...autoBlocks.value[i], ...auto[i] }
        } else {
          autoBlocks.value[i] = {
            blockType: 'AUTO',
            blockNumber: i + 1,
            title: '',
            content: '',
            enabled: true,
            isAutoGenerated: true
          }
        }
      }
    }
  } catch (error) {
    console.error('載入描述區塊失敗:', error)
  }
}

// 初始化自動區塊
const initializeAutoBlocks = async () => {
  if (!form.value.id) return
  
  try {
    const response = await productDescriptionBlockApi.initializeAutoBlocks(form.value.id)
    if (response.success && response.data) {
      $q.notify({
        type: 'positive',
        message: '自動區塊已初始化',
        position: 'top'
      })
      await loadDescriptionBlocks(form.value.id)
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '初始化自動區塊失敗',
      position: 'top'
    })
    console.error('初始化自動區塊失敗', error)
  }
}

// 保存所有描述區塊
const saveDescriptionBlocks = async () => {
  if (!form.value.id) return
  
  try {
    // 收集所有區塊
    const allBlocks: ProductDescriptionBlock[] = [
      ...manualBlocks.value,
      ...autoBlocks.value
    ].map(block => ({
      ...block,
      productId: form.value.id
    }))
    
    const response = await productDescriptionBlockApi.saveBlocks(form.value.id, allBlocks)
    if (response.success) {
      $q.notify({
        type: 'positive',
        message: '描述區塊已保存',
        position: 'top'
      })
      await loadDescriptionBlocks(form.value.id)
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '保存描述區塊失敗',
      position: 'top'
    })
    console.error('保存描述區塊失敗', error)
  }
}

// 重置描述區塊
const resetDescriptionBlocks = () => {
  manualBlocks.value = [
    { blockType: 'MANUAL', blockNumber: 1, title: '', content: '', enabled: true },
    { blockType: 'MANUAL', blockNumber: 2, title: '', content: '', enabled: true },
    { blockType: 'MANUAL', blockNumber: 3, title: '', content: '', enabled: true }
  ]
  autoBlocks.value = [
    { blockType: 'AUTO', blockNumber: 1, title: '', content: '', enabled: true, isAutoGenerated: true },
    { blockType: 'AUTO', blockNumber: 2, title: '', content: '', enabled: true, isAutoGenerated: true },
    { blockType: 'AUTO', blockNumber: 3, title: '', content: '', enabled: true, isAutoGenerated: true },
    { blockType: 'AUTO', blockNumber: 4, title: '', content: '', enabled: true, isAutoGenerated: true },
    { blockType: 'AUTO', blockNumber: 5, title: '', content: '', enabled: true, isAutoGenerated: true },
    { blockType: 'AUTO', blockNumber: 6, title: '', content: '', enabled: true, isAutoGenerated: true },
    { blockType: 'AUTO', blockNumber: 7, title: '', content: '', enabled: true, isAutoGenerated: true }
  ]
}

// Album-related functions
const loadAlbums = async () => {
  try {
    const response = await albumApi.getAlbums({ page: 0, size: 100 })
    if (response.success && response.data) {
      albums.value = response.data.content || []
      // 查找或創建預設相冊（用於儲存直接上傳的商品圖片）
      await ensureDefaultAlbum()
    }
  } catch (error) {
    console.error('Failed to load albums:', error)
  }
}

// 確保預設相冊存在
const ensureDefaultAlbum = async () => {
  try {
    // 查找名為「商品圖片」的相冊
    const defaultAlbumName = '商品圖片'
    const existingAlbum = albums.value.find(album => album.name === defaultAlbumName)
    
    if (existingAlbum && existingAlbum.id) {
      defaultAlbumId.value = existingAlbum.id
    } else {
      // 如果不存在，創建一個
      const response = await albumApi.createAlbum({
        name: defaultAlbumName,
        description: '系統自動建立的商品圖片相簿'
      })
      if (response.success && response.data && response.data.id) {
        defaultAlbumId.value = response.data.id
        albums.value.push(response.data)
      }
    }
  } catch (error) {
    console.error('Failed to ensure default album:', error)
  }
}

const loadAlbumImages = async () => {
  if (!selectedAlbum.value) return

  try {
    const response = await albumApi.getAlbumImages(selectedAlbum.value)
    if (response.success && response.data) {
      albumImages.value = response.data
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '載入相簿圖片失敗',
      position: 'top'
    })
  }
}

const isImageSelected = (imageId?: number) => {
  return tempSelectedImages.value.some(img => img.id === imageId)
}

const toggleImageSelection = (image: AlbumImage) => {
  const index = tempSelectedImages.value.findIndex(img => img.id === image.id)
  if (index > -1) {
    tempSelectedImages.value.splice(index, 1)
  } else {
    tempSelectedImages.value.push(image)
  }
}

const addSelectedImagesToProduct = async () => {
  if (!form.value.id || tempSelectedImages.value.length === 0) return

  try {
    const imageIds = tempSelectedImages.value.map(img => img.id).filter((id): id is number => id !== undefined)
    await productApi.addAlbumImages(form.value.id, imageIds)

    // Add only new images to avoid duplicates
    tempSelectedImages.value.forEach(img => {
      if (!selectedAlbumImages.value.some(existing => existing.id === img.id)) {
        selectedAlbumImages.value.push(img)
      }
    })

    $q.notify({
      type: 'positive',
      message: `已添加 ${tempSelectedImages.value.length} 張圖片`,
      position: 'top'
    })

    // Reset and close
    tempSelectedImages.value = []
    showAlbumSelector.value = false
    selectedAlbum.value = null
    albumImages.value = []
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '添加圖片失敗',
      position: 'top'
    })
  }
}

const removeSelectedImage = (imageId?: number) => {
  // Remove from local preview
  // The actual backend update happens when user clicks "保存" or "完成"
  const index = selectedAlbumImages.value.findIndex(img => img.id === imageId)
  if (index > -1) {
    selectedAlbumImages.value.splice(index, 1)
  }
}

const isPrimarySelectedImage = (index: number) => index === 0

const setSelectedImageAsPrimary = (index: number) => {
  if (index <= 0 || index >= selectedAlbumImages.value.length) return
  const [target] = selectedAlbumImages.value.splice(index, 1)
  if (target) selectedAlbumImages.value.unshift(target)
}

const moveSelectedImage = (index: number, direction: -1 | 1) => {
  const nextIndex = index + direction
  if (index < 0 || nextIndex < 0 || nextIndex >= selectedAlbumImages.value.length) return
  const list = [...selectedAlbumImages.value]
  ;[list[index], list[nextIndex]] = [list[nextIndex], list[index]]
  selectedAlbumImages.value = list
}

const clearSelectedImages = () => {
  selectedAlbumImages.value = []
}

const getStatusColor = (status: string) => {
  switch (status) {
    case 'PUBLISHED': return 'positive'
    case 'UNPUBLISHED': return 'warning'
    case 'DRAFT': return 'grey'
    default: return 'grey'
  }
}

const getStatusLabel = (status: string) => {
  switch (status) {
    case 'PUBLISHED': return '已上架'
    case 'UNPUBLISHED': return '已下架'
    case 'DRAFT': return '草稿'
    default: return status
  }
}

// 處理圖片載入錯誤
const handleImageError = (event: Event, product: Product) => {
  const imageUrl = getProductImageUrl(product)
  console.warn(`圖片載入失敗: ${imageUrl}`, { productId: product.id, productName: product.name })
}

// 獲取商品圖片 URL（獲取第一張圖片）
const getProductImageUrl = (product: Product): string | null => {
  // 調試：輸出商品圖片數據
  if (product.images) {
    console.log(`[getProductImageUrl] 商品 ${product.id} (${product.name}) 的 images:`, product.images)
  }

  if (!product.images || product.images.length === 0) {
    return null
  }

  let imageUrl: string | null = null

  // 如果 images 是字符串數組
  if (typeof product.images[0] === 'string') {
    imageUrl = product.images[0] as string
  } else if (typeof product.images[0] === 'object' && product.images[0] !== null) {
    // 如果 images 是對象數組（ProductImageDTO）
    const firstImage = product.images[0] as { imageUrl?: string; albumImageId?: number }
    imageUrl = firstImage?.imageUrl || null
  }

  console.log(`[getProductImageUrl] 提取的 imageUrl:`, imageUrl)

  if (!imageUrl) {
    return null
  }

  // 如果 URL 已經是完整 URL（http/https），直接返回
  if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
    return imageUrl
  }

  // 如果 URL 已經以 /api 開頭，直接返回
  if (imageUrl.startsWith('/api')) {
    return imageUrl
  }

  // 如果 URL 以 /albums 開頭（相簿圖片路徑），加上 /api 前綴
  if (imageUrl.startsWith('/albums')) {
    return `/api${imageUrl}`
  }

  // 否則假設是相對於 /api/albums/images/ 的檔案名稱
  return `/api/albums/images/${imageUrl}`
}

const loadCategories = async () => {
  try {
    const response = await categoryApi.getEnabledCategories()
    if (response.success && response.data) {
      categories.value = response.data
    }
  } catch (error) {
    console.error('Failed to load categories:', error)
  }
}

// 啟動商品管理導覽
const handleStartTour = () => {
  nextTick(() => {
    startProductTour(true)
  })
}

onMounted(() => {
  loadProducts()
  loadAlbums()
  loadCategories()
  loadInventoryAlerts()

  // 如果用戶是第一次訪問商品管理頁面，自動啟動導覽
  if (!isProductTourCompleted()) {
    setTimeout(() => {
      startProductTour()
    }, 1500)
  }
})
</script>

<style scoped>
.product-admin-page {
  max-width: none;
}

.metric-card {
  border-radius: 16px;
  border: 1px solid #e5eaf4;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.04);
  background: rgba(255, 255, 255, 0.95);
}

.metric-card :deep(.q-card__section) {
  padding: 14px 16px;
}

.metric-label {
  color: #64748b;
  font-size: 0.82rem;
}

.metric-value {
  margin-top: 4px;
  font-size: 1.6rem;
  font-weight: 800;
  line-height: 1.1;
  color: #0f172a;
}

.metric-sub {
  margin-top: 6px;
  color: #94a3b8;
  font-size: 0.78rem;
}

.metric-card--blue {
  background: linear-gradient(180deg, #fff 0%, #f8fbff 100%);
}

.metric-card--green {
  background: linear-gradient(180deg, #fff 0%, #f5fff8 100%);
}

.metric-card--amber {
  background: linear-gradient(180deg, #fff 0%, #fffaf1 100%);
}

.metric-card--rose {
  background: linear-gradient(180deg, #fff 0%, #fff7f7 100%);
}

.filter-card {
  border-radius: 16px;
}

.filter-card__top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 10px;
}

.table-shell-card {
  border-radius: 16px;
  overflow: hidden;
}

.product-table :deep(.q-table__top) {
  padding-top: 10px;
  padding-bottom: 10px;
}

.product-table :deep(thead th) {
  white-space: nowrap;
}

.mobile-product-card {
  border-radius: 14px;
  border-color: #e5eaf4;
  box-shadow: 0 6px 18px rgba(15, 23, 42, 0.04);
}

.mobile-product-card :deep(.q-card__actions .q-btn) {
  min-height: 38px;
}

.product-dialog-card {
  border-radius: 18px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  max-height: min(92vh, 960px);
}

.product-dialog-tabs {
  border-bottom: 1px solid #e8edf6;
  margin-bottom: 8px;
}

.product-dialog-tabs :deep(.q-tab) {
  min-height: 42px;
}

.product-dialog-card__body {
  flex: 1 1 auto;
  min-height: 0;
  overflow: auto;
  overscroll-behavior: contain;
}

.product-dialog-card__actions {
  position: sticky;
  bottom: 0;
  z-index: 2;
  background: rgba(255, 255, 255, 0.96);
  border-top: 1px solid #e8edf6;
  backdrop-filter: blur(8px);
}

.product-dialog-card__actions :deep(.q-btn) {
  min-height: 40px;
}

.product-image-setup {
  border-radius: 14px;
}

.image-setup-card {
  border-radius: 14px;
  border-color: #e5eaf4;
  background: #fff;
}

.selected-image-card {
  border: 1px solid #e5eaf4;
  border-radius: 12px;
  overflow: hidden;
  background: #fff;
  transition: box-shadow 180ms ease, border-color 180ms ease;
}

.selected-image-card--primary {
  border-color: #2563eb;
  box-shadow: 0 0 0 2px rgba(37, 99, 235, 0.12);
}

.selected-image-card__thumb {
  background: #f8fafc;
}

.selected-image-card__actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 8px;
}

.selected-image-card__actions :deep(.q-btn) {
  min-height: 36px;
}

.image-setup-empty {
  border: 1px dashed #cbd5e1;
  border-radius: 12px;
  padding: 20px 16px;
  text-align: center;
  background: #f8fafc;
}

@media (prefers-reduced-motion: reduce) {
  .selected-image-card {
    transition: none;
  }
}

@media (max-width: 700px) {
  .filter-card__top {
    flex-direction: column;
  }

  .metric-value {
    font-size: 1.35rem;
  }

  .mobile-product-card :deep(.q-card__section) {
    padding: 12px;
  }

  .product-dialog-card {
    max-height: 100dvh;
    border-radius: 0;
  }

  .product-dialog-card__actions {
    padding-bottom: calc(12px + env(safe-area-inset-bottom, 0px));
  }

  .product-dialog-card__actions :deep(.q-btn) {
    min-height: 44px;
  }

  .selected-image-card__actions {
    flex-direction: column;
    align-items: stretch;
  }

  .selected-image-card__actions > .row {
    justify-content: flex-end;
  }
}
</style>
