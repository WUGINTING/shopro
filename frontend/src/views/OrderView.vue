<template>
  <q-page class="q-pa-md">
    <div class="page-container">
      <!-- Page Header -->
      <div class="row items-center justify-between q-mb-md">
        <div>
          <div class="text-h5 text-weight-bold">訂單管理</div>
          <div class="text-caption text-grey-7">管理訂單狀態和發貨資訊</div>
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
            <q-tooltip>訂單管理教學</q-tooltip>
          </q-btn>
          <q-btn
            color="grey-7"
            icon="info"
            label="狀態說明"
            flat
            @click="showStatusModal = true"
          />
          <q-btn
            color="primary"
            icon="add"
            label="新增訂單"
            unelevated
            @click="handleAdd"
          />
        </div>
      </div>

      <!-- Metrics -->
      <div class="row q-col-gutter-md q-mb-md">
        <div class="col-12 col-sm-6 col-lg-3">
          <q-card flat bordered class="order-metric-card">
            <q-card-section>
              <div class="text-caption text-grey-7">目前資料筆數</div>
              <div class="order-metric-card__value">{{ pagination.rowsNumber || 0 }}</div>
              <div class="text-caption text-grey-6">符合目前篩選條件的總訂單數</div>
            </q-card-section>
          </q-card>
        </div>
        <div class="col-12 col-sm-6 col-lg-3">
          <q-card flat bordered class="order-metric-card order-metric-card--amber">
            <q-card-section>
              <div class="text-caption text-grey-7">待付款</div>
              <div class="order-metric-card__value">{{ orderMetrics.pendingPayment }}</div>
              <div class="text-caption text-grey-6">本頁需追蹤付款的訂單</div>
            </q-card-section>
          </q-card>
        </div>
        <div class="col-12 col-sm-6 col-lg-3">
          <q-card flat bordered class="order-metric-card order-metric-card--blue">
            <q-card-section>
              <div class="text-caption text-grey-7">待出貨 / 處理中</div>
              <div class="order-metric-card__value">{{ orderMetrics.fulfillmentQueue }}</div>
              <div class="text-caption text-grey-6">本頁需要履約處理的訂單</div>
            </q-card-section>
          </q-card>
        </div>
        <div class="col-12 col-sm-6 col-lg-3">
          <q-card flat bordered class="order-metric-card order-metric-card--green">
            <q-card-section>
              <div class="text-caption text-grey-7">本頁總金額</div>
              <div class="order-metric-card__value">{{ formatCurrency(orderMetrics.pageAmount) }}</div>
              <div class="text-caption text-grey-6">目前列表頁面可見訂單金額加總</div>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <!-- Filter Panel -->
      <q-card class="q-mb-md order-filter-card">
        <q-card-section class="order-filter-card__header">
          <div class="row items-center justify-between">
            <div>
              <div class="text-h6">篩選條件</div>
              <div class="text-caption text-grey-7">
                快速縮小範圍，優先處理待付款與待出貨訂單
              </div>
            </div>
            <div class="row q-gutter-sm">
              <q-btn
                flat
                dense
                icon="filter_list"
                :label="showFilterPanel ? '收起篩選' : '展開篩選'"
                @click="showFilterPanel = !showFilterPanel"
              />
              <q-btn
                flat
                dense
                icon="refresh"
                label="重置"
                @click="resetFilters"
              />
              <q-btn
                flat
                dense
                icon="search"
                label="搜尋"
                color="primary"
                @click="applyFilters"
              />
            </div>
          </div>
          <div class="row items-center justify-between q-mt-sm filter-card__meta">
            <div class="text-caption text-grey-7">
              {{ hasActiveFilters ? `已啟用 ${activeFilterCount} 個篩選條件` : '尚未套用篩選條件' }}
            </div>
            <q-chip dense color="grey-2" text-color="grey-8" icon="receipt_long">
              共 {{ pagination.rowsNumber || 0 }} 筆訂單
            </q-chip>
          </div>
        </q-card-section>

        <q-slide-transition>
          <div v-show="showFilterPanel">
            <q-separator />
            <q-card-section>
              <div class="row q-col-gutter-md">
                <!-- 訂單編號 -->
                <div class="col-12 col-md-3">
                  <q-input
                    v-model="filterForm.orderNumber"
                    label="訂單編號"
                    outlined
                    dense
                    clearable
                    placeholder="輸入訂單編號"
                    name="order-number-filter"
                    autocomplete="off"
                  />
                </div>

                <!-- 客戶姓名 -->
                <div class="col-12 col-md-3">
                  <q-input
                    v-model="filterForm.customerName"
                    label="客戶姓名"
                    outlined
                    dense
                    clearable
                    placeholder="輸入客戶姓名"
                    name="order-customer-name-filter"
                    autocomplete="off"
                  />
                </div>

                <!-- 訂單狀態 -->
                <div class="col-12 col-md-3">
                  <q-select
                    v-model="filterForm.status"
                    label="訂單狀態"
                    outlined
                    dense
                    clearable
                    :options="statusFilterOptions"
                    option-value="value"
                    option-label="label"
                    emit-value
                    map-options
                  />
                </div>

                <!-- 客戶選擇 -->
                <div class="col-12 col-md-3">
                  <q-select
                    v-model="filterForm.customerId"
                    label="客戶"
                    outlined
                    dense
                    clearable
                    use-input
                    input-debounce="300"
                    :options="customerOptions"
                    option-value="id"
                    option-label="name"
                    emit-value
                    map-options
                    @filter="filterCustomers"
                    placeholder="選擇或搜索客戶"
                  >
                    <template v-slot:no-option>
                      <q-item>
                        <q-item-section class="text-grey">
                          沒有找到客戶
                        </q-item-section>
                      </q-item>
                    </template>
                  </q-select>
                </div>

                <!-- 開始日期 -->
                <div class="col-12 col-md-3">
                  <q-input
                    v-model="filterForm.startDate"
                    label="開始日期"
                    outlined
                    dense
                    clearable
                    type="date"
                  />
                </div>

                <!-- 結束日期 -->
                <div class="col-12 col-md-3">
                  <q-input
                    v-model="filterForm.endDate"
                    label="結束日期"
                    outlined
                    dense
                    clearable
                    type="date"
                  />
                </div>

                <!-- 最小金額 -->
                <div class="col-12 col-md-3">
                  <q-input
                    v-model.number="filterForm.minAmount"
                    label="最小金額"
                    outlined
                    dense
                    clearable
                    type="number"
                    prefix="¥"
                    step="0.01"
                    min="0"
                    inputmode="decimal"
                    name="order-min-amount-filter"
                  />
                </div>

                <!-- 最大金額 -->
                <div class="col-12 col-md-3">
                  <q-input
                    v-model.number="filterForm.maxAmount"
                    label="最大金額"
                    outlined
                    dense
                    clearable
                    type="number"
                    prefix="¥"
                    step="0.01"
                    min="0"
                    inputmode="decimal"
                    name="order-max-amount-filter"
                  />
                </div>
              </div>

              <!-- 已選篩選條件標籤 -->
              <div v-if="hasActiveFilters" class="q-mt-md">
                <div class="row items-center justify-between q-mb-sm">
                  <div class="text-caption text-grey-7">已選條件：</div>
                  <q-btn
                    flat
                    dense
                    size="sm"
                    color="grey-7"
                    icon="close"
                    label="清除全部"
                    @click="resetFilters"
                  />
                </div>
                <div class="row q-gutter-xs order-filter-chip-list">
                  <q-chip
                    v-if="filterForm.orderNumber"
                    removable
                    color="primary"
                    text-color="white"
                    @remove="filterForm.orderNumber = ''"
                  >
                    訂單編號: {{ filterForm.orderNumber }}
                  </q-chip>
                  <q-chip
                    v-if="filterForm.customerName"
                    removable
                    color="primary"
                    text-color="white"
                    @remove="filterForm.customerName = ''"
                  >
                    客戶姓名: {{ filterForm.customerName }}
                  </q-chip>
                  <q-chip
                    v-if="filterForm.status"
                    removable
                    color="primary"
                    text-color="white"
                    @remove="filterForm.status = null"
                  >
                    狀態: {{ getStatusLabel(filterForm.status) }}
                  </q-chip>
                  <q-chip
                    v-if="filterForm.customerId"
                    removable
                    color="primary"
                    text-color="white"
                    @remove="filterForm.customerId = null"
                  >
                    客戶ID: {{ filterForm.customerId }}
                  </q-chip>
                  <q-chip
                    v-if="filterForm.startDate"
                    removable
                    color="primary"
                    text-color="white"
                    @remove="filterForm.startDate = ''"
                  >
                    開始: {{ filterForm.startDate }}
                  </q-chip>
                  <q-chip
                    v-if="filterForm.endDate"
                    removable
                    color="primary"
                    text-color="white"
                    @remove="filterForm.endDate = ''"
                  >
                    結束: {{ filterForm.endDate }}
                  </q-chip>
                  <q-chip
                    v-if="filterForm.minAmount"
                    removable
                    color="primary"
                    text-color="white"
                    @remove="filterForm.minAmount = null"
                  >
                    最小金額: ¥{{ filterForm.minAmount }}
                  </q-chip>
                  <q-chip
                    v-if="filterForm.maxAmount"
                    removable
                    color="primary"
                    text-color="white"
                    @remove="filterForm.maxAmount = null"
                  >
                    最大金額: ¥{{ filterForm.maxAmount }}
                  </q-chip>
                </div>
              </div>
            </q-card-section>
          </div>
        </q-slide-transition>
      </q-card>

      <!-- Orders Table -->
      <q-card class="order-table-card">
        <q-table
          class="order-admin-table"
          :rows="orders"
          :columns="columns"
          row-key="id"
          :loading="loading"
          :pagination="pagination"
          :rows-per-page-options="[10, 20, 50]"
          wrap-cells
          separator="horizontal"
          flat
        >
          <template v-slot:top>
            <div class="order-table-toolbar full-width">
              <div>
                <div class="text-subtitle1 text-weight-bold">訂單列表</div>
                <div class="text-caption text-grey-7">
                  顯示 {{ orders.length }} 筆（總計 {{ pagination.rowsNumber || 0 }} 筆）
                </div>
              </div>
              <div class="row items-center q-gutter-xs">
                <q-chip dense color="amber-1" text-color="amber-10">待付款 {{ orderMetrics.pendingPayment }}</q-chip>
                <q-chip dense color="blue-1" text-color="blue-10">待處理 {{ orderMetrics.fulfillmentQueue }}</q-chip>
                <q-chip dense color="green-1" text-color="green-10">已完成 {{ orderMetrics.completed }}</q-chip>
              </div>
            </div>
          </template>

          <template v-slot:body-cell-id="props">
            <q-td :props="props">
              <q-chip dense square color="grey-2" text-color="grey-8" class="text-caption">
                #{{ props.row.id }}
              </q-chip>
            </q-td>
          </template>

          <template v-slot:body-cell-orderNumber="props">
            <q-td :props="props">
              <div
                class="column q-gutter-xs order-number-link"
                role="button"
                tabindex="0"
                @click="handleViewDetail(props.row)"
                @keyup.enter="handleViewDetail(props.row)"
              >
                <span class="text-weight-bold text-primary">{{ props.row?.orderNumber || '-' }}</span>
                <span class="text-caption text-grey-6">ID #{{ props.row?.id ?? '-' }}</span>
              </div>
            </q-td>
          </template>

          <template v-slot:body-cell-totalAmount="props">
            <q-td :props="props">
              <span class="text-weight-bold text-primary">{{ formatCurrency(props.row.totalAmount) }}</span>
            </q-td>
          </template>

          <template v-slot:body-cell-qa="props">
            <q-td :props="props">
              <div class="column items-center">
                <q-badge
                  :color="getOrderQAStats(props.row?.id)?.unanswered ? 'warning' : 'positive'"
                  :label="getOrderQALabel(props.row?.id)"
                />
                <div class="text-caption text-grey-6">回答/總數</div>
              </div>
            </q-td>
          </template>

          <template v-slot:body-cell-customerName="props">
            <q-td :props="props">
              <div class="column q-gutter-xs">
                <span>{{ props.row?.customerName || (props.row?.customerId ? `客戶 #${props.row.customerId}` : '-') }}</span>
                <span v-if="props.row?.customerPhone" class="text-caption text-grey-6">{{ props.row.customerPhone }}</span>
              </div>
            </q-td>
          </template>

          <template v-slot:body-cell-status="props">
            <q-td :props="props">
              <div v-if="props.row?.status" class="column items-start q-gutter-xs">
                <q-badge :color="getStatusColor(props.row.status)" :label="getStatusLabel(props.row.status)" />
                <span class="text-caption text-grey-6">{{ props.row.status }}</span>
              </div>
              <span v-else>-</span>
            </q-td>
          </template>

          <template v-slot:body-cell-createdAt="props">
            <q-td :props="props">
              <div class="column q-gutter-xs">
                <span>{{ formatDateTime(props.row?.createdAt) }}</span>
                <span class="text-caption text-grey-6">建立時間</span>
              </div>
            </q-td>
          </template>

          <template v-slot:body-cell-actions="props">
            <q-td :props="props" class="order-actions-cell">
              <q-btn-dropdown flat dense color="primary" label="更新狀態" size="sm" class="order-row-btn">
                <q-list>
                  <q-item clickable v-close-popup @click="handleStatusChange(props.row.id, 'PROCESSING')">
                    <q-item-section>
                      <q-item-label>處理中</q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-item clickable v-close-popup @click="handleStatusChange(props.row.id, 'COMPLETED')">
                    <q-item-section>
                      <q-item-label>已完成</q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-separator />
                  <q-item clickable v-close-popup @click="handleStatusChange(props.row.id, 'CANCELLED')">
                    <q-item-section>
                      <q-item-label class="text-negative">已取消</q-item-label>
                    </q-item-section>
                  </q-item>
                </q-list>
              </q-btn-dropdown>

              <q-btn
                v-if="props.row.status === 'PENDING_PAYMENT'"
                flat
                dense
                round
                icon="payment"
                color="positive"
                size="sm"
                class="order-row-icon-btn"
                @click="handlePayment(props.row)"
              >
                <q-tooltip>前往付款</q-tooltip>
              </q-btn>

              <q-btn
                v-if="props.row.status === 'PAID'"
                flat
                dense
                round
                icon="local_shipping"
                color="info"
                size="sm"
                class="order-row-icon-btn"
                @click="handleShipment(props.row)"
              >
                <q-tooltip>已出貨</q-tooltip>
              </q-btn>

              <q-btn flat dense round icon="edit" color="primary" size="sm" class="order-row-icon-btn" @click="handleEdit(props.row)">
                <q-tooltip>編輯訂單</q-tooltip>
              </q-btn>
              <q-btn flat dense round icon="visibility" color="primary" size="sm" class="order-row-icon-btn" @click="handleViewDetail(props.row)">
                <q-tooltip>查看詳情</q-tooltip>
              </q-btn>
              <q-btn flat dense round icon="forum" color="orange" size="sm" class="order-row-icon-btn" @click="openOrderQA(props.row)">
                <q-tooltip>訂單問答</q-tooltip>
              </q-btn>
              <q-btn flat dense round icon="delete" color="negative" size="sm" class="order-row-icon-btn" @click="handleDelete(props.row)">
                <q-tooltip>刪除訂單</q-tooltip>
              </q-btn>
            </q-td>
          </template>
        </q-table>
      </q-card>

      <!-- Add/Edit Order Dialog -->
      <q-dialog v-model="showDialog" maximized>
        <q-card style="min-width: 800px">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">{{ editingOrderId ? '編輯訂單' : '新增訂單' }}</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <q-form @submit="handleSubmit">
              <div class="row q-col-gutter-md">
                <!-- 客戶選擇或輸入 -->
                <div class="col-12">
                  <q-select
                    v-model="form.customerId"
                    :input-value="customerInputValue"
                    label="客戶（可選或輸入）"
                    outlined
                    :options="customerOptions"
                    option-value="id"
                    option-label="name"
                    emit-value
                    map-options
                    use-input
                    fill-input
                    hide-selected
                    :loading="customerSearchLoading"
                    input-debounce="500"
                    @filter="filterCustomers"
                    @update:model-value="onCustomerChange"
                    @input-value="onCustomerInput"
                    clearable
                  >
                    <template v-slot:option="scope">
                      <q-item v-bind="scope.itemProps">
                        <q-item-section>
                          <q-item-label>{{ scope.opt.name }}</q-item-label>
                          <q-item-label caption>{{ scope.opt.email }}</q-item-label>
                        </q-item-section>
                      </q-item>
                    </template>
                    <template v-slot:no-option>
                      <q-item>
                        <q-item-section class="text-grey">
                          沒有找到客戶，可以手動輸入
                        </q-item-section>
                      </q-item>
                    </template>
                  </q-select>
                </div>

                <!-- 客戶信息輸入（始終顯示，選擇客戶後自動填充） -->
                <div class="col-12">
                  <div class="text-subtitle2 q-mb-sm">客戶信息（可選）</div>
                  <div class="row q-col-gutter-md">
                    <div class="col-12 col-md-4">
                      <q-input
                        v-model="form.customerName"
                        label="客戶姓名"
                        outlined
                        dense
                      />
                    </div>
                    <div class="col-12 col-md-4">
                      <q-input
                        v-model="form.customerPhone"
                        label="客戶電話"
                        outlined
                        dense
                      />
                    </div>
                    <div class="col-12 col-md-4">
                      <q-input
                        v-model="form.customerEmail"
                        label="客戶郵箱"
                        outlined
                        dense
                        type="email"
                      />
                    </div>
                  </div>
                </div>

                <!-- 取貨方式 -->
                <div class="col-12 col-md-6">
                  <q-select
                    v-model="form.pickupType"
                    label="取貨方式 *"
                    outlined
                    :options="pickupTypeOptions"
                    option-value="value"
                    option-label="label"
                    emit-value
                    map-options
                    :rules="[val => !!val || '請選擇取貨方式']"
                  />
                </div>

                <!-- 收貨地址 -->
                <div class="col-12" v-if="form.pickupType === 'DELIVERY'">
                  <q-input
                    v-model="form.shippingAddress"
                    label="收貨地址"
                    outlined
                    type="textarea"
                    rows="2"
                  />
                </div>

                <!-- 訂單項目 -->
                <div class="col-12">
                  <div class="text-subtitle2 q-mb-sm">訂單項目 *</div>
                  <q-table
                    :rows="form.items"
                    :columns="itemColumns"
                    row-key="tempId"
                    flat
                    hide-pagination
                    :rows-per-page-options="[0]"
                  >
                    <template v-slot:body-cell-product="props">
                      <q-td :props="props">
                        <q-select
                          v-model="props.row.productId"
                          outlined
                          dense
                          :options="productOptions"
                          option-value="id"
                          option-label="name"
                          emit-value
                          map-options
                          @update:model-value="(val) => onProductChange(props.row, val)"
                        />
                      </q-td>
                    </template>

                    <template v-slot:body-cell-specification="props">
                      <q-td :props="props">
                        <q-select
                          v-model="props.row.specificationId"
                          outlined
                          dense
                          :options="getSpecificationOptions(props.row.productId)"
                          option-value="id"
                          :option-label="(spec) => spec.specName || spec.sku || '無規格'"
                          emit-value
                          map-options
                          clearable
                          :disable="!props.row.productId"
                          @update:model-value="(val) => onSpecificationChange(props.row, val)"
                        >
                          <template v-slot:option="scope">
                            <q-item v-bind="scope.itemProps">
                              <q-item-section>
                                <q-item-label>{{ scope.opt.specName || '無規格名稱' }}</q-item-label>
                                <q-item-label caption v-if="scope.opt.sku">
                                  SKU: {{ scope.opt.sku }} | 價格: ¥{{ (scope.opt.price || 0).toFixed(2) }} | 庫存: {{ scope.opt.stock || 0 }}
                                </q-item-label>
                              </q-item-section>
                            </q-item>
                          </template>
                        </q-select>
                      </q-td>
                    </template>

                    <template v-slot:body-cell-quantity="props">
                      <q-td :props="props">
                        <q-input
                          v-model.number="props.row.quantity"
                          outlined
                          dense
                          type="number"
                          min="1"
                          @update:model-value="calculateItemSubtotal(props.row)"
                        />
                      </q-td>
                    </template>

                    <template v-slot:body-cell-unitPrice="props">
                      <q-td :props="props">
                        <q-input
                          v-model.number="props.row.unitPrice"
                          outlined
                          dense
                          type="number"
                          prefix="$"
                          step="0.01"
                          @update:model-value="calculateItemSubtotal(props.row)"
                        />
                      </q-td>
                    </template>

                    <template v-slot:body-cell-subtotal="props">
                      <q-td :props="props">
                        <span class="text-weight-bold">${{ (props.row.subtotal || 0).toFixed(2) }}</span>
                      </q-td>
                    </template>

                    <template v-slot:body-cell-actions="props">
                      <q-td :props="props">
                        <q-btn
                          flat
                          dense
                          round
                          icon="delete"
                          color="negative"
                          @click="removeOrderItem(props.rowIndex)"
                        />
                      </q-td>
                    </template>

                    <template v-slot:top>
                      <q-btn
                        color="primary"
                        icon="add"
                        label="添加商品"
                        dense
                        unelevated
                        @click="addOrderItem"
                      />
                    </template>
                  </q-table>
                </div>

                <!-- 折扣金額 -->
                <div class="col-12 col-md-6">
                  <q-input
                    v-model.number="form.discountAmount"
                    label="折扣金額"
                    outlined
                    type="number"
                    prefix="$"
                    step="0.01"
                    min="0"
                    @update:model-value="calculateTotal"
                  />
                </div>

                <!-- 運費 -->
                <div class="col-12 col-md-6">
                  <q-input
                    v-model.number="form.shippingFee"
                    label="運費"
                    outlined
                    type="number"
                    prefix="$"
                    step="0.01"
                    min="0"
                    @update:model-value="calculateTotal"
                  />
                </div>

                <!-- 訂單總金額 -->
                <div class="col-12">
                  <q-input
                    v-model.number="form.totalAmount"
                    label="訂單總金額"
                    outlined
                    type="number"
                    prefix="$"
                    step="0.01"
                    readonly
                    class="text-h6 text-weight-bold"
                  />
                </div>

                <!-- 備註 -->
                <div class="col-12">
                  <q-input
                    v-model="form.notes"
                    label="備註"
                    outlined
                    type="textarea"
                    rows="3"
                  />
                </div>
              </div>

              <q-card-actions align="right" class="q-mt-md">
                <q-btn flat label="取消" color="grey" v-close-popup />
                <q-btn flat label="儲存" color="primary" type="submit" />
              </q-card-actions>
            </q-form>
          </q-card-section>
        </q-card>
      </q-dialog>

      <!-- Discount Management Dialog -->
      <q-dialog v-model="showDiscountDialog" persistent>
        <q-card style="min-width: 600px; max-width: 800px">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">管理折扣記錄</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <!-- Order ID Display -->
            <div class="q-mb-md" v-if="discountForm.orderId">
              <q-banner class="bg-primary text-white">
                <template v-slot:avatar>
                  <q-icon name="receipt" />
                </template>
                訂單ID: <strong>{{ discountForm.orderId }}</strong>
              </q-banner>
            </div>

            <q-banner v-else class="bg-warning text-white q-mb-md">
              <template v-slot:avatar>
                <q-icon name="warning" />
              </template>
              請先保存訂單後才能添加折扣記錄
            </q-banner>

            <!-- Discount List -->
            <div class="q-mb-md">
              <div class="text-subtitle2 q-mb-sm">折扣記錄列表</div>
              <q-table
                :rows="orderDiscounts"
                :columns="discountColumns"
                row-key="id"
                flat
                hide-pagination
                :rows-per-page-options="[0]"
              >
                <template v-slot:body-cell-discountAmount="props">
                  <q-td :props="props">
                    <span class="text-weight-bold text-primary">¥{{ props.row.discountAmount?.toFixed(2) }}</span>
                  </q-td>
                </template>
                <template v-slot:body-cell-discountType="props">
                  <q-td :props="props">
                    <q-badge :color="getDiscountTypeColor(props.row.discountType)">
                      {{ props.row.discountType }}
                    </q-badge>
                  </q-td>
                </template>
                <template v-slot:body-cell-actions="props">
                  <q-td :props="props">
                    <q-btn flat dense round icon="delete" color="negative" size="sm" @click="handleDeleteDiscount(props.row.id!)">
                      <q-tooltip>刪除</q-tooltip>
                    </q-btn>
                  </q-td>
                </template>
                <template v-slot:no-data>
                  <div class="text-center text-grey-6 q-py-md">暫無折扣記錄</div>
                </template>
              </q-table>
              <div class="text-caption text-grey-7 q-mt-sm">
                總折扣金額：<span class="text-weight-bold text-primary">${{ totalDiscountAmount.toFixed(2) }}</span>
              </div>
            </div>

            <!-- Add Discount Form -->
            <q-separator class="q-my-md" />
            <div class="text-subtitle2 q-mb-sm">新增折扣記錄</div>
            <q-form @submit="handleAddDiscount">
              <div class="row q-col-gutter-md">
                <div class="col-12 col-md-6">
                  <q-select
                    v-model="discountForm.discountType"
                    label="折扣類型 *"
                    outlined
                    dense
                    :options="discountTypeOptions"
                    :rules="[val => !!val || '請選擇折扣類型']"
                  />
                </div>
                <div class="col-12 col-md-6">
                  <q-input
                    v-model="discountForm.discountCode"
                    label="折扣代碼"
                    outlined
                    dense
                  />
                </div>
                <div class="col-12 col-md-6">
                  <q-input
                    v-model.number="discountForm.discountAmount"
                    label="折扣金額 *"
                    outlined
                    dense
                    type="number"
                    step="0.01"
                    prefix="$"
                    :rules="[val => val >= 0 || '折扣金額不能小於0']"
                  />
                </div>
                <div class="col-12 col-md-6">
                  <q-input
                    v-model.number="discountForm.discountPercentage"
                    label="折扣百分比"
                    outlined
                    dense
                    type="number"
                    step="0.01"
                    suffix="%"
                  />
                </div>
                <div class="col-12">
                  <q-input
                    v-model="discountForm.description"
                    label="折扣描述"
                    outlined
                    dense
                    type="textarea"
                    rows="2"
                  />
                </div>
              </div>
            </q-form>
          </q-card-section>

          <q-card-actions align="right" class="q-px-md q-pb-md">
            <q-btn flat label="關閉" color="grey-7" v-close-popup />
            <q-btn unelevated label="新增折扣" color="primary" @click="handleAddDiscount" />
          </q-card-actions>
        </q-card>
      </q-dialog>

      <!-- Status Info Modal -->
      <q-dialog v-model="showStatusModal">
        <q-card class="order-status-guide-card" style="min-width: 600px; max-width: 700px">
          <q-card-section class="row items-center q-pb-none">
            <div class="text-h6">訂單狀態說明</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section>
            <q-banner dense rounded class="order-status-guide-banner q-mb-md">
              建議流程：先處理「待付款」，再追蹤「已付款/處理中」的履約與出貨。
            </q-banner>
            <div class="text-body2 text-grey-7 q-mb-md">
              以下是系統中所有可用的訂單狀態及其說明：
            </div>

            <q-list bordered separator class="order-status-guide-list">
              <q-item v-for="status in orderStatusList" :key="status.value" class="order-status-guide-item">
                <q-item-section avatar>
                  <q-badge :color="getStatusColor(status.value)" :label="getStatusLabel(status.value)" />
                </q-item-section>
                <q-item-section>
                  <q-item-label class="text-weight-bold">{{ getStatusLabel(status.value) }}</q-item-label>
                  <q-item-label caption>{{ status.description }}</q-item-label>
                </q-item-section>
                <q-item-section side>
                  <q-chip :color="getStatusColor(status.value)" text-color="white" size="sm">
                    {{ status.value }}
                  </q-chip>
                </q-item-section>
              </q-item>
            </q-list>

            <q-separator class="q-my-md" />

            <div class="text-body2 text-grey-8">
              <div class="text-weight-bold q-mb-sm">狀態流程：</div>
              <div class="q-pl-sm">
                <div>待付款 → 已付款 → 處理中 → 已完成</div>
                <div class="text-grey-6 text-caption q-mt-xs">
                  * 訂單也可能直接變更為「已取消」或「已退款」狀態
                </div>
              </div>
            </div>
          </q-card-section>

          <q-card-actions align="right" class="q-px-md q-pb-md">
            <q-btn flat label="關閉" color="grey-7" v-close-popup />
          </q-card-actions>
        </q-card>
      </q-dialog>

      <!-- Payment Dialog -->
      <q-dialog v-model="showPaymentDialog">
        <q-card class="order-flow-dialog-card" style="min-width: 500px">
          <q-card-section class="row items-center q-pb-none order-flow-dialog-card__header">
            <div class="text-h6">前往付款</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section class="order-flow-dialog-card__body">
            <q-banner rounded dense class="order-flow-banner q-mb-md">
              付款完成後，訂單可進入履約/出貨流程。
            </q-banner>

            <div class="q-mb-md order-info-panel">
              <div class="text-subtitle2 q-mb-sm">訂單資訊</div>
              <q-list bordered class="order-info-panel__list">
                <q-item>
                  <q-item-section>
                    <q-item-label>訂單編號</q-item-label>
                    <q-item-label caption>{{ paymentOrder?.orderNumber }}</q-item-label>
                  </q-item-section>
                </q-item>
                <q-item>
                  <q-item-section>
                    <q-item-label>付款金額</q-item-label>
                    <q-item-label caption class="text-h6 text-positive">
                      {{ formatCurrency(paymentOrder?.totalAmount) }}
                    </q-item-label>
                  </q-item-section>
                </q-item>
              </q-list>
            </div>

            <div class="q-mb-md order-info-panel">
              <div class="text-subtitle2 q-mb-sm">選擇支付方式</div>
              <q-option-group
                v-model="selectedGateway"
                :options="paymentGatewayOptions"
                color="primary"
                class="order-payment-options"
              />
            </div>

            <q-banner v-if="paymentError" class="bg-negative text-white q-mb-md">
              {{ paymentError }}
            </q-banner>

            <q-banner v-if="showTestInfo" class="bg-info text-white q-mb-md">
              <template v-slot:avatar>
                <q-icon name="info" />
              </template>
              <div class="text-subtitle2 q-mb-sm">測試環境資訊</div>
              <div class="text-caption">
                <div>測試信用卡號：4311-9522-2222-2222</div>
                <div>有效期限：任何未來日期</div>
                <div>安全碼：222</div>
              </div>
            </q-banner>
          </q-card-section>

          <q-card-actions align="right" class="q-px-md q-pb-md order-flow-dialog-card__actions">
            <q-btn flat label="取消" color="grey-7" v-close-popup />
            <q-btn
              unelevated
              label="前往付款"
              color="primary"
              :loading="paymentLoading"
              @click="processPayment"
            />
          </q-card-actions>
        </q-card>
      </q-dialog>

      <!-- Shipment Dialog -->
      <q-dialog v-model="showShipmentDialog">
        <q-card class="order-flow-dialog-card" style="min-width: 600px; max-width: 700px">
          <q-card-section class="row items-center q-pb-none order-flow-dialog-card__header">
            <div class="text-h6">設定已出貨</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup />
          </q-card-section>

          <q-card-section class="order-flow-dialog-card__body">
            <q-banner rounded dense class="order-flow-banner q-mb-md">
              建議填寫完整物流資訊，降低客服追件與查詢成本。
            </q-banner>

            <div class="q-mb-md order-info-panel">
              <div class="text-subtitle2 q-mb-sm">訂單資訊</div>
              <q-list bordered class="order-info-panel__list">
                <q-item>
                  <q-item-section>
                    <q-item-label>訂單編號</q-item-label>
                    <q-item-label caption>{{ shipmentOrder?.orderNumber }}</q-item-label>
                  </q-item-section>
                </q-item>
                <q-item>
                  <q-item-section>
                    <q-item-label>客戶</q-item-label>
                    <q-item-label caption>{{ shipmentOrder?.customerName || `客戶 #${shipmentOrder?.customerId}` }}</q-item-label>
                  </q-item-section>
                </q-item>
              </q-list>
            </div>

            <q-form @submit="handleShipmentSubmit">
              <div class="row q-col-gutter-md">
                <div class="col-12">
                  <q-input
                    v-model="shipmentForm.shippingCompany"
                    label="物流公司 *"
                    outlined
                    dense
                    :rules="[val => !!val || '請輸入物流公司']"
                    placeholder="例如：順豐速運、中通快遞等"
                    name="shipment-company"
                    autocomplete="organization"
                  />
                </div>
                <div class="col-12">
                  <q-input
                    v-model="shipmentForm.trackingNumber"
                    label="物流單號 *"
                    outlined
                    dense
                    :rules="[val => !!val || '請輸入物流單號']"
                    placeholder="請輸入物流追蹤單號"
                    name="shipment-tracking-number"
                    autocomplete="off"
                  />
                </div>
                <div class="col-12">
                  <q-input
                    v-model="shipmentForm.recipientName"
                    label="收件人姓名"
                    outlined
                    dense
                    :hint="shipmentOrder?.customerName ? `預設：${shipmentOrder.customerName}` : ''"
                    name="shipment-recipient-name"
                    autocomplete="shipping name"
                  />
                </div>
                <div class="col-12">
                  <q-input
                    v-model="shipmentForm.recipientPhone"
                    label="收件人電話"
                    outlined
                    dense
                    :hint="shipmentOrder?.customerPhone ? `預設：${shipmentOrder.customerPhone}` : ''"
                    name="shipment-recipient-phone"
                    type="tel"
                    inputmode="tel"
                    autocomplete="shipping tel"
                  />
                </div>
                <div class="col-12">
                  <q-input
                    v-model="shipmentForm.recipientAddress"
                    label="收件地址"
                    outlined
                    dense
                    type="textarea"
                    rows="2"
                    :hint="shipmentOrder?.shippingAddress ? `預設：${shipmentOrder.shippingAddress}` : ''"
                    name="shipment-recipient-address"
                    autocomplete="shipping street-address"
                  />
                </div>
                <div class="col-12">
                  <q-input
                    v-model="shipmentForm.notes"
                    label="備註"
                    outlined
                    dense
                    type="textarea"
                    rows="2"
                    placeholder="可選：添加其他備註資訊"
                    name="shipment-notes"
                    autocomplete="off"
                  />
                </div>
              </div>

              <div class="q-mt-md">
                <q-banner class="bg-info text-white order-shipment-note-banner">
                  <template v-slot:avatar>
                    <q-icon name="info" />
                  </template>
                  <div class="text-caption">
                    設定為已出貨後，訂單狀態將自動更新為「處理中」。
                  </div>
                </q-banner>
              </div>
            </q-form>
          </q-card-section>

          <q-card-actions align="right" class="q-px-md q-pb-md order-flow-dialog-card__actions">
            <q-btn flat label="取消" color="grey-7" v-close-popup />
            <q-btn
              unelevated
              label="確認出貨"
              color="primary"
              :loading="shipmentLoading"
              @click="handleShipmentSubmit"
            />
          </q-card-actions>
        </q-card>
      </q-dialog>

      <!-- Order Detail Dialog -->
      <q-dialog v-model="showDetailDialog" maximized>
        <q-card class="order-detail-dialog-card">
          <q-card-section class="row items-center q-pb-none bg-primary text-white order-detail-dialog-card__header">
            <div>
              <div class="text-h6">訂單詳情</div>
              <div class="text-caption text-blue-1">
                {{ selectedOrder?.orderNumber || '檢視訂單資料與物流記錄' }}
              </div>
            </div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup color="white" />
          </q-card-section>

          <q-card-section v-if="selectedOrder" class="q-pa-lg order-detail-dialog-card__body">
            <div class="row q-col-gutter-md q-mb-md">
              <div class="col-12 col-md-4">
                <q-card flat bordered class="order-detail-summary-card">
                  <q-card-section>
                    <div class="text-caption text-grey-7">訂單狀態</div>
                    <div class="q-mt-xs">
                      <q-badge :color="getStatusColor(selectedOrder.status)" :label="getStatusLabel(selectedOrder.status)" />
                    </div>
                    <div class="text-caption text-grey-6 q-mt-sm">{{ selectedOrder.status }}</div>
                  </q-card-section>
                </q-card>
              </div>
              <div class="col-12 col-md-4">
                <q-card flat bordered class="order-detail-summary-card">
                  <q-card-section>
                    <div class="text-caption text-grey-7">訂單總金額</div>
                    <div class="text-h6 text-primary text-weight-bold q-mt-xs">
                      {{ formatCurrency(selectedOrder.totalAmount) }}
                    </div>
                    <div class="text-caption text-grey-6">建立於 {{ formatDateTime(selectedOrder.createdAt) }}</div>
                  </q-card-section>
                </q-card>
              </div>
              <div class="col-12 col-md-4">
                <q-card flat bordered class="order-detail-summary-card">
                  <q-card-section>
                    <div class="text-caption text-grey-7">履約狀態提示</div>
                    <div class="text-body2 q-mt-xs">
                      {{ selectedOrder.status === 'PAID' ? '可安排出貨與新增物流記錄' : selectedOrder.status === 'PENDING_PAYMENT' ? '等待付款完成後再安排出貨' : '請依目前狀態追蹤後續處理' }}
                    </div>
                  </q-card-section>
                </q-card>
              </div>
            </div>

            <div class="row q-col-gutter-lg">
              <!-- 左側：訂單基本資訊 -->
              <div class="col-12 col-md-6">
                <q-card flat bordered class="order-detail-panel-card">
                  <q-card-section>
                    <div class="text-h6 q-mb-md">訂單資訊</div>
                    <q-list bordered separator class="order-detail-list">
                      <q-item>
                        <q-item-section>
                          <q-item-label caption>訂單編號</q-item-label>
                          <q-item-label class="text-weight-bold">{{ selectedOrder.orderNumber }}</q-item-label>
                        </q-item-section>
                      </q-item>
                      <q-item>
                        <q-item-section>
                          <q-item-label caption>訂單狀態</q-item-label>
                          <q-item-label>
                            <q-badge :color="getStatusColor(selectedOrder.status)" :label="getStatusLabel(selectedOrder.status)" />
                          </q-item-label>
                        </q-item-section>
                      </q-item>
                      <q-item>
                        <q-item-section>
                          <q-item-label caption>客戶姓名</q-item-label>
                          <q-item-label>{{ selectedOrder.customerName || `客戶 #${selectedOrder.customerId}` }}</q-item-label>
                        </q-item-section>
                      </q-item>
                      <q-item>
                        <q-item-section>
                          <q-item-label caption>客戶電話</q-item-label>
                          <q-item-label>{{ selectedOrder.customerPhone || '-' }}</q-item-label>
                        </q-item-section>
                      </q-item>
                      <q-item>
                        <q-item-section>
                          <q-item-label caption>客戶郵箱</q-item-label>
                          <q-item-label>{{ selectedOrder.customerEmail || '-' }}</q-item-label>
                        </q-item-section>
                      </q-item>
                      <q-item>
                        <q-item-section>
                          <q-item-label caption>配送地址</q-item-label>
                          <q-item-label>{{ selectedOrder.shippingAddress || '-' }}</q-item-label>
                        </q-item-section>
                      </q-item>
                      <q-item>
                        <q-item-section>
                          <q-item-label caption>訂單總金額</q-item-label>
                          <q-item-label class="text-h6 text-primary">{{ formatCurrency(selectedOrder.totalAmount) }}</q-item-label>
                        </q-item-section>
                      </q-item>
                      <q-item>
                        <q-item-section>
                          <q-item-label caption>創建時間</q-item-label>
                          <q-item-label>{{ formatDateTime(selectedOrder.createdAt) }}</q-item-label>
                        </q-item-section>
                      </q-item>
                    </q-list>
                  </q-card-section>
                </q-card>

                <!-- 訂單項目 -->
                <q-card flat bordered class="q-mt-md order-detail-panel-card">
                  <q-card-section>
                    <div class="text-h6 q-mb-md">訂單項目</div>
                    <q-list bordered separator class="order-detail-list" v-if="getOrderItems(selectedOrder) && getOrderItems(selectedOrder).length > 0">
                      <q-item v-for="item in getOrderItems(selectedOrder)" :key="item.id">
                        <q-item-section>
                          <q-item-label class="text-weight-bold">
                            {{ item.productName || `商品 #${item.productId}` }}
                          </q-item-label>
                          <q-item-label caption>
                            <!-- 調試信息（開發時可查看） -->
                            <!-- <div class="text-caption text-grey-5 q-mb-xs">
                              調試: {{ JSON.stringify(item) }}
                            </div> -->

                            <!-- 規格/產品信息 -->
                            <div class="q-mb-xs">
                              <span class="text-caption text-grey-6">買什麼：</span>
                              <span class="text-weight-medium">{{ item.productName || `商品 #${item.productId}` }}</span>
                            </div>
                            <div class="q-mb-xs">
                              <span class="text-caption text-grey-6">規格：</span>
                              <span class="text-weight-medium">
                                {{ item.productSpec || item.specificationName || (item.specificationId ? `ID:${item.specificationId}` : '無') }}
                              </span>
                              <span v-if="item.productSku" class="text-caption text-grey-5 q-ml-sm">(SKU {{ item.productSku }})</span>
                            </div>

                            <!-- 價格信息 -->
                            <div class="q-mt-sm">
                              <span class="text-grey-7">數量：</span>
                              <span class="text-weight-medium">{{ item.quantity }}</span>
                              <span class="text-grey-7 q-ml-md">單價：</span>
                              <span class="text-weight-medium">{{ formatCurrency(item.unitPrice || item.price || 0) }}</span>
                              <span class="text-grey-7 q-ml-md">小計：</span>
                              <span class="text-weight-bold text-primary">{{ formatCurrency(item.subtotal || item.subtotalAmount || 0) }}</span>
                            </div>

                            <!-- 如果沒有任何規格信息，顯示提示 -->
                            <div v-if="!item.productSpec && !item.productSku && !item.specificationId" class="q-mt-xs text-caption text-grey-6">
                              （無規格信息）
                            </div>
                          </q-item-label>
                        </q-item-section>
                      </q-item>
                    </q-list>
                    <div v-else class="text-grey-6 text-center q-pa-md">暫無訂單項目</div>
                  </q-card-section>
                </q-card>
              </div>

              <!-- 右側：物流記錄 -->
              <div class="col-12 col-md-6">
                <q-card flat bordered class="order-detail-panel-card">
                  <q-card-section>
                    <div class="row items-center justify-between q-mb-md">
                      <div class="text-h6">物流記錄</div>
                      <q-btn
                        v-if="selectedOrder.status === 'PAID'"
                        flat
                        dense
                        icon="add"
                        label="新增物流"
                        color="primary"
                        size="sm"
                        @click="handleShipmentFromDetail"
                      />
                    </div>

                    <q-inner-loading :showing="shipmentsLoading" />

                    <q-list bordered separator class="order-detail-list" v-if="shipments.length > 0">
                      <q-item v-for="shipment in shipments" :key="shipment.id">
                        <q-item-section>
                          <q-item-label class="text-weight-bold">
                            {{ shipment.shippingCompany || '未指定物流公司' }}
                          </q-item-label>
                          <q-item-label caption>
                            <div>物流單號：{{ shipment.trackingNumber || '-' }}</div>
                            <div>狀態：{{ getShippingStatusLabel(shipment.shippingStatus) }}</div>
                            <div v-if="shipment.shippedAt">
                              出貨時間：{{ formatDateTime(shipment.shippedAt) }}
                            </div>
                            <div v-if="shipment.deliveredAt">
                              送達時間：{{ formatDateTime(shipment.deliveredAt) }}
                            </div>
                            <div v-if="shipment.recipientName">
                              收件人：{{ shipment.recipientName }}
                              <span v-if="shipment.recipientPhone"> ({{ shipment.recipientPhone }})</span>
                            </div>
                            <div v-if="shipment.recipientAddress">
                              收件地址：{{ shipment.recipientAddress }}
                            </div>
                            <div v-if="shipment.notes" class="q-mt-xs text-grey-7">
                              備註：{{ shipment.notes }}
                            </div>
                          </q-item-label>
                        </q-item-section>
                        <q-item-section side>
                          <q-badge :color="getShippingStatusColor(shipment.shippingStatus)">
                            {{ getShippingStatusLabel(shipment.shippingStatus) }}
                          </q-badge>
                        </q-item-section>
                      </q-item>
                    </q-list>
                    <div v-else class="text-grey-6 text-center q-pa-md">
                      暫無物流記錄
                      <div v-if="selectedOrder.status === 'PAID'" class="q-mt-sm">
                        <q-btn
                          flat
                          dense
                          icon="add"
                          label="新增物流記錄"
                          color="primary"
                          size="sm"
                          @click="handleShipmentFromDetail"
                        />
                      </div>
                    </div>
                  </q-card-section>
                </q-card>
              </div>
            </div>
          </q-card-section>

          <q-card-actions align="right" class="q-px-lg q-pb-lg order-detail-dialog-card__actions">
            <q-btn flat label="關閉" color="grey-7" v-close-popup />
          </q-card-actions>
        </q-card>
      </q-dialog>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch, nextTick } from 'vue'
import { useQuasar } from 'quasar'
import { useAuthStore } from '@/stores/auth'
import { orderApi, orderQAApi, type Order, type OrderItem, type OrderQA, type PageResponse, type OrderQueryParams } from '@/api'
import { crmApi, type Customer } from '@/api/crm'
import { productApi, productSpecificationApi, type Product, type ProductSpecification } from '@/api/product'
import { orderDiscountApi, type OrderDiscount } from '@/api/orderDiscount'
import { createPayment, type PaymentRequest } from '@/api/payment'
import { shipmentApi, type OrderShipment } from '@/api/shipment'
import { startOrderTour, isOrderTourCompleted } from '@/utils/orderTour'
import { useRouter } from 'vue-router'

const $q = useQuasar()
const authStore = useAuthStore()
const router = useRouter()

const orders = ref<Order[]>([])
const loading = ref(false)
const showDialog = ref(false)
const editingOrderId = ref<number | null>(null)
const customers = ref<Customer[]>([])
const products = ref<Product[]>([])
const productSpecifications = ref<Map<number, ProductSpecification[]>>(new Map())
const orderDiscounts = ref<OrderDiscount[]>([])
const showDiscountDialog = ref(false)
const discountForm = ref<OrderDiscount>({
  orderId: 0,
  discountType: '',
  discountCode: '',
  discountAmount: 0,
  discountPercentage: 0,
  description: ''
})

// 狀態說明 Modal
const showStatusModal = ref(false)

// 訂單狀態列表
const orderStatusList = [
  { value: 'PENDING_PAYMENT', description: '訂單已建立，等待客戶付款' },
  { value: 'PAID', description: '客戶已成功付款，等待處理' },
  { value: 'PROCESSING', description: '訂單正在處理中（已發貨或正在準備）' },
  { value: 'COMPLETED', description: '訂單已完成所有流程' },
  { value: 'CANCELLED', description: '訂單已被取消' },
  { value: 'REFUNDED', description: '訂單已退款' }
] as const

// 支付相關
const showPaymentDialog = ref(false)
const paymentOrder = ref<Order | null>(null)
const selectedGateway = ref('ECPAY')
const paymentLoading = ref(false)
const paymentError = ref('')
const showTestInfo = ref(true) // 測試環境顯示測試資訊

// 出貨相關
const showShipmentDialog = ref(false)
const shipmentOrder = ref<Order | null>(null)
const shipmentLoading = ref(false)
const shipmentForm = ref<Partial<OrderShipment>>({
  orderId: 0,
  shippingStatus: 'SHIPPED',
  shippingCompany: '',
  trackingNumber: '',
  recipientName: '',
  recipientPhone: '',
  recipientAddress: '',
  notes: ''
})

// 訂單詳情相關
const showDetailDialog = ref(false)
const selectedOrder = ref<Order | null>(null)
const shipments = ref<OrderShipment[]>([])
const shipmentsLoading = ref(false)

// 篩選相關
const showFilterPanel = ref(false)
const filterForm = ref({
  orderNumber: '',
  customerName: '',
  customerId: null as number | null,
  status: null as Order['status'] | null,
  startDate: '',
  endDate: '',
  minAmount: null as number | null,
  maxAmount: null as number | null,
  isDraft: null as boolean | null,
  storeId: null as number | null
})

const statusFilterOptions = [
  { label: '待付款', value: 'PENDING_PAYMENT' },
  { label: '已付款', value: 'PAID' },
  { label: '處理中', value: 'PROCESSING' },
  { label: '已完成', value: 'COMPLETED' },
  { label: '已取消', value: 'CANCELLED' },
  { label: '已退款', value: 'REFUNDED' }
]

const hasActiveFilters = computed(() => {
  return !!(
    filterForm.value.orderNumber ||
    filterForm.value.customerName ||
    filterForm.value.customerId ||
    filterForm.value.status ||
    filterForm.value.startDate ||
    filterForm.value.endDate ||
    filterForm.value.minAmount ||
    filterForm.value.maxAmount
  )
})

const activeFilterCount = computed(() => {
  let count = 0
  if (filterForm.value.orderNumber) count++
  if (filterForm.value.customerName) count++
  if (filterForm.value.customerId) count++
  if (filterForm.value.status) count++
  if (filterForm.value.startDate) count++
  if (filterForm.value.endDate) count++
  if (filterForm.value.minAmount !== null && filterForm.value.minAmount !== undefined) count++
  if (filterForm.value.maxAmount !== null && filterForm.value.maxAmount !== undefined) count++
  return count
})

const orderMetrics = computed(() => {
  const list = orders.value || []
  return {
    pendingPayment: list.filter(order => order.status === 'PENDING_PAYMENT').length,
    fulfillmentQueue: list.filter(order => order.status === 'PAID' || order.status === 'PROCESSING').length,
    completed: list.filter(order => order.status === 'COMPLETED').length,
    pageAmount: list.reduce((sum, order) => sum + (Number(order.totalAmount) || 0), 0)
  }
})

type OrderQAStats = {
  total: number
  answered: number
  unanswered: number
}

const qaStats = ref<Record<number, OrderQAStats>>({})

const loadOrderQAStats = async () => {
  try {
    const response = await orderQAApi.getAllQA()
    const stats: Record<number, OrderQAStats> = {}
    const qaList = response.data ?? []
    qaList.forEach((qa: OrderQA) => {
      const orderId = qa.orderId
      if (!orderId) return
      const entry = stats[orderId] || { total: 0, answered: 0, unanswered: 0 }
      entry.total += 1
      if (qa.answer) {
        entry.answered += 1
      } else {
        entry.unanswered += 1
      }
      stats[orderId] = entry
    })
    qaStats.value = stats
  } catch (error) {
    console.error('Failed to load order QA stats:', error)
  }
}

const getOrderQAStats = (orderId?: number) => {
  if (!orderId) return undefined
  return qaStats.value[orderId]
}

const getOrderQALabel = (orderId?: number) => {
  const stats = getOrderQAStats(orderId)
  if (!stats) return '0/0'
  return `${stats.answered}/${stats.total}`
}

const openOrderQA = (order: Order) => {
  if (!order.id) return
  router.push({
    name: 'orderQA',
    query: { orderId: String(order.id) }
  })
}

const paymentGatewayOptions = [
  { label: '綠界 ECPay', value: 'ECPAY' }
]

const pagination = ref({
  page: 1,
  rowsPerPage: 20,
  rowsNumber: 0,
  sortBy: 'createdAt',
  descending: true
})

// 訂單表單
const form = ref<{
  customerId: number | null
  status: Order['status']
  pickupType: 'DELIVERY' | 'STORE_PICKUP' | 'CROSS_STORE_PICKUP'
  items: Array<OrderItem & { tempId?: number; subtotal?: number }>
  subtotalAmount: number
  discountAmount: number
  shippingFee: number
  totalAmount: number
  shippingAddress?: string
  notes?: string
}>({
  customerId: null,
  status: 'PENDING_PAYMENT',
  pickupType: 'DELIVERY',
  items: [],
  subtotalAmount: 0,
  discountAmount: 0,
  shippingFee: 0,
  totalAmount: 0,
  shippingAddress: '',
  notes: ''
})

let tempIdCounter = 0

const customerOptions = ref<Customer[]>([])
const allCustomers = ref<Customer[]>([])
const customerInputValue = ref<string>('')
const customerSearchLoading = ref(false)

const filterCustomers = async (val: string, update: (callback: () => void) => void) => {
  customerInputValue.value = val

  if (val === '') {
    update(() => {
      customerOptions.value = allCustomers.value
    })
    return
  }

  // 如果輸入長度小於2，只從已載入的客戶中搜索
  if (val.length < 2) {
    update(() => {
      const needle = val.toLowerCase()
      customerOptions.value = allCustomers.value.filter(
        customer =>
          customer.name?.toLowerCase().includes(needle) ||
          customer.email?.toLowerCase().includes(needle) ||
          customer.phone?.toLowerCase().includes(needle)
      )
    })
    return
  }

  // 當輸入長度 >= 2 時，從服務器搜索
  customerSearchLoading.value = true
  try {
    // 使用後端的搜索 API
    const response = await crmApi.searchCustomers(val, { page: 0, size: 20 })

    update(() => {
      if (response.success && response.data) {
        const searchResults = Array.isArray(response.data)
          ? response.data
          : (response.data as any)?.content || []

        // 合併搜索結果和已載入的客戶，去重
        const merged = [...allCustomers.value]
        searchResults.forEach((customer: Customer) => {
          if (!merged.find(c => c.id === customer.id)) {
            merged.push(customer)
          }
        })

        // 更新已載入的客戶列表
        allCustomers.value = merged

        // 過濾匹配的客戶（支持姓名、郵箱、電話）
        const needle = val.toLowerCase()
        customerOptions.value = merged.filter(
          customer =>
            customer.name?.toLowerCase().includes(needle) ||
            customer.email?.toLowerCase().includes(needle) ||
            customer.phone?.toLowerCase().includes(needle)
        )
      } else {
        // 如果搜索失敗，使用本地過濾
        const needle = val.toLowerCase()
        customerOptions.value = allCustomers.value.filter(
          customer =>
            customer.name?.toLowerCase().includes(needle) ||
            customer.email?.toLowerCase().includes(needle) ||
            customer.phone?.toLowerCase().includes(needle)
        )
      }
    })
  } catch (error) {
    console.error('Failed to search customers:', error)
    // 搜索失敗時使用本地過濾
    update(() => {
      const needle = val.toLowerCase()
      customerOptions.value = allCustomers.value.filter(
        customer =>
          customer.name?.toLowerCase().includes(needle) ||
          customer.email?.toLowerCase().includes(needle) ||
          customer.phone?.toLowerCase().includes(needle)
      )
    })
  } finally {
    customerSearchLoading.value = false
  }
}

const onCustomerInput = (val: string) => {
  customerInputValue.value = val
  // 如果用戶輸入文字但沒有選擇客戶，將輸入的文字保存到客戶姓名
  if (val && !form.value.customerId) {
    form.value.customerName = val
  }
}
const productOptions = computed(() => products.value)

const pickupTypeOptions = [
  { label: '宅配', value: 'DELIVERY' },
  { label: '門市取貨', value: 'STORE_PICKUP' },
  { label: '跨店取貨', value: 'CROSS_STORE_PICKUP' }
]

const itemColumns = [
  { name: 'product', label: '商品', align: 'left' as const, field: 'productId' },
  { name: 'specification', label: '規格（SKU）', align: 'left' as const, field: 'specificationId' },
  { name: 'quantity', label: '數量', align: 'center' as const, field: 'quantity' },
  { name: 'unitPrice', label: '單價', align: 'left' as const, field: 'unitPrice' },
  { name: 'subtotal', label: '小計', align: 'right' as const, field: 'subtotal' },
  { name: 'actions', label: '操作', align: 'center' as const, field: 'actions' }
]

const columns = [
  { name: 'id', label: 'ID', align: 'left' as const, field: 'id', sortable: true },
  { name: 'orderNumber', label: '訂單號', align: 'left' as const, field: 'orderNumber' },
  { name: 'customerName', label: '客戶', align: 'left' as const, field: 'customerName' },
  { name: 'totalAmount', label: '總金額', align: 'left' as const, field: 'totalAmount', sortable: true },
  { name: 'qa', label: '訂單問答', align: 'center' as const, field: 'qa' },
  { name: 'status', label: '狀態', align: 'center' as const, field: 'status' },
  { name: 'createdAt', label: '創建時間', align: 'left' as const, field: 'createdAt', sortable: true },
  { name: 'actions', label: '操作', align: 'center' as const, field: 'actions' }
]

const discountColumns = [
  { name: 'discountType', label: '折扣類型', align: 'center' as const, field: 'discountType' },
  { name: 'discountCode', label: '折扣代碼', align: 'left' as const, field: 'discountCode' },
  { name: 'discountAmount', label: '折扣金額', align: 'right' as const, field: 'discountAmount' },
  { name: 'description', label: '描述', align: 'left' as const, field: 'description' },
  { name: 'actions', label: '操作', align: 'center' as const, field: 'actions' }
]

const discountTypeOptions = ['COUPON', 'PROMOTION', 'MEMBER_DISCOUNT', 'SEASONAL', 'BULK_ORDER', 'OTHER']

const totalDiscountAmount = computed(() => {
  return orderDiscounts.value.reduce((sum, discount) => sum + (discount.discountAmount || 0), 0)
})

const getDiscountTypeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    COUPON: 'purple',
    PROMOTION: 'orange',
    MEMBER_DISCOUNT: 'teal',
    SEASONAL: 'pink',
    BULK_ORDER: 'blue',
    OTHER: 'grey'
  }
  return colorMap[type] || 'grey'
}

const loadOrders = async (useFilter = false) => {
  loading.value = true
  try {
    let response

    if (useFilter && hasActiveFilters.value) {
      // 使用篩選條件搜索
      const queryParams: any = {
        page: pagination.value.page - 1, // 後端從0開始
        size: pagination.value.rowsPerPage
      }

      if (filterForm.value.orderNumber) {
        queryParams.orderNumber = filterForm.value.orderNumber
      }
      if (filterForm.value.customerName) {
        queryParams.customerName = filterForm.value.customerName
      }
      if (filterForm.value.customerId) {
        queryParams.customerId = filterForm.value.customerId
      }
      if (filterForm.value.status) {
        queryParams.status = filterForm.value.status
      }
      if (filterForm.value.startDate) {
        queryParams.startDate = filterForm.value.startDate + 'T00:00:00'
      }
      if (filterForm.value.endDate) {
        queryParams.endDate = filterForm.value.endDate + 'T23:59:59'
      }
      if (filterForm.value.minAmount !== null && filterForm.value.minAmount !== undefined) {
        queryParams.minAmount = filterForm.value.minAmount
      }
      if (filterForm.value.maxAmount !== null && filterForm.value.maxAmount !== undefined) {
        queryParams.maxAmount = filterForm.value.maxAmount
      }
      if (filterForm.value.isDraft !== null && filterForm.value.isDraft !== undefined) {
        queryParams.isDraft = filterForm.value.isDraft
      }
      if (filterForm.value.storeId) {
        queryParams.storeId = filterForm.value.storeId
      }

      response = await orderApi.searchOrders(queryParams)
    } else {
      // 根據用戶角色選擇不同的 API
      if (authStore.userRole === 'CUSTOMER') {
        // CUSTOMER 使用專屬 API 獲取自己的訂單
        response = await orderApi.getMyOrders({
          page: pagination.value.page - 1,
          size: pagination.value.rowsPerPage
        })
      } else {
        // 其他角色使用普通查詢
        response = await orderApi.getOrders({
          page: pagination.value.page - 1,
          size: pagination.value.rowsPerPage
        })
      }
    }

    // 後端返回的格式是 ApiResponse<Page<OrderDTO>>
    // response.data 本身就是 Page 對象（包含 content, totalElements 等）
    const data = response.data as PageResponse<Order> | Order[]
    let orderList: Order[] = []
    let totalCount = 0

    // 調試信息
    console.log('Order API Response:', response)
    console.log('Response data:', data)
    console.log('Data type:', Array.isArray(data) ? 'Array' : typeof data)
    console.log('Has content?', data && typeof data === 'object' && 'content' in data)

    if (Array.isArray(data)) {
      // 如果是數組，直接使用
      orderList = data
      totalCount = data.length
    } else if (data && typeof data === 'object' && 'content' in data) {
      // 如果是分頁對象，提取 content
      const pageData = data as PageResponse<Order>
      orderList = pageData.content || []
      totalCount = pageData.totalElements || pageData.total || 0
    } else if (data && typeof data === 'object') {
      // 嘗試直接訪問可能的字段
      console.warn('Unexpected data format:', data)
      orderList = []
      totalCount = 0
    } else {
      orderList = []
      totalCount = 0
    }

    console.log('Parsed orderList:', orderList)
    console.log('Total count:', totalCount)

    // 如果訂單的 customerName 為空但有 customerId，從已載入的客戶列表中查找並填充
    orderList.forEach(order => {
      if (!order.customerName && order.customerId) {
        const customer = customers.value.find(c => c.id === order.customerId)
        if (customer) {
          order.customerName = customer.name
        }
      }
    })

    orderList.sort((a, b) => {
      const aTime = a.createdAt ? new Date(a.createdAt).getTime() : 0
      const bTime = b.createdAt ? new Date(b.createdAt).getTime() : 0
      return bTime - aTime
    })

    orders.value = orderList

    // 更新分頁資訊
    if (data && 'totalElements' in data) {
      pagination.value.rowsNumber = totalCount
    }
  } catch (error: any) {
    console.error('Load orders error:', error)
    console.error('Error response:', error.response)
    console.error('Error message:', error.message)
    
    // 如果是 403 錯誤，可能是權限問題
    if (error.response?.status === 403) {
      $q.notify({
        type: 'negative',
        message: '沒有權限訪問訂單列表',
        position: 'top'
      })
    } else if (error.response?.status === 401) {
      $q.notify({
        type: 'negative',
        message: '請先登入',
        position: 'top'
      })
    } else {
      $q.notify({
        type: 'negative',
        message: error.response?.data?.message || error.message || '載入訂單清單失敗',
        position: 'top'
      })
    }
  } finally {
    loading.value = false
  }
}

const applyFilters = () => {
  pagination.value.page = 1 // 重置到第一頁
  loadOrders(true)
}

const resetFilters = () => {
  filterForm.value = {
    orderNumber: '',
    customerName: '',
    customerId: null,
    status: null,
    startDate: '',
    endDate: '',
    minAmount: null,
    maxAmount: null,
    isDraft: null,
    storeId: null
  }
  pagination.value.page = 1
  loadOrders(false)
}

const getStatusColor = (status: Order['status']) => {
  const colorMap: Record<string, string> = {
    PENDING: 'grey',
    PENDING_PAYMENT: 'grey',
    PAID: 'blue',
    PROCESSING: 'warning',
    SHIPPED: 'info',
    DELIVERED: 'positive',
    COMPLETED: 'positive',
    CANCELLED: 'negative',
    REFUNDED: 'negative'
  }
  return colorMap[status] || 'grey'
}

const getStatusLabel = (status: Order['status']) => {
  const labelMap: Record<string, string> = {
    PENDING: '待處理',
    PENDING_PAYMENT: '待付款',
    PAID: '已付款',
    PROCESSING: '處理中',
    SHIPPED: '已發貨',
    DELIVERED: '已送達',
    COMPLETED: '已完成',
    CANCELLED: '已取消',
    REFUNDED: '已退款'
  }
  return labelMap[status] || status
}

const formatCurrency = (amount?: number | null) => {
  const value = Number(amount) || 0
  return new Intl.NumberFormat('zh-TW', {
    style: 'currency',
    currency: 'TWD',
    maximumFractionDigits: 2
  }).format(value)
}

const formatDateTime = (value?: string | null) => {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)
  return date.toLocaleString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const handleStatusChange = async (id?: number, status?: Order['status']) => {
  if (!id || !status) return

  // 對於取消狀態，需要確認
  if (status === 'CANCELLED') {
    $q.dialog({
      title: '警告',
      message: '確定要取消此訂單嗎？此操作無法復原。',
      cancel: true,
      persistent: true
    }).onOk(async () => {
      await updateStatus(id, status)
    })
  } else {
    await updateStatus(id, status)
  }
}

const updateStatus = async (id: number, status: Order['status']) => {
  try {
    await orderApi.updateOrderStatus(id, status)
    $q.notify({
      type: 'positive',
      message: '狀態更新成功',
      position: 'top'
    })
    loadOrders()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '狀態更新失敗',
      position: 'top'
    })
  }
}

const loadCustomers = async () => {
  try {
    // 載入前20個客戶作為初始列表
    const response = await crmApi.getCustomers({ page: 0, size: 20 })
    if (response.success && response.data) {
      const customerList = Array.isArray(response.data)
        ? response.data
        : (response.data as any)?.content || []
      customers.value = customerList
      allCustomers.value = customerList
      customerOptions.value = customerList
    }
  } catch (error) {
    console.error('Failed to load customers:', error)
  }
}

const loadProducts = async () => {
  try {
    const response = await productApi.getProducts()
    const data = response.data as PageResponse<Product> | Product[]
    if (Array.isArray(data)) {
      products.value = data
    } else if (data && 'content' in data) {
      products.value = data.content
    }
  } catch (error) {
    console.error('Failed to load products:', error)
  }
}

const handleAdd = () => {
  resetForm()
  showDialog.value = true
}

const resetForm = () => {
  form.value = {
    customerId: null,
    customerName: '',
    customerPhone: '',
    customerEmail: '',
    status: 'PENDING_PAYMENT',
    pickupType: 'DELIVERY',
    items: [],
    subtotalAmount: 0,
    discountAmount: 0,
    shippingFee: 0,
    totalAmount: 0,
    shippingAddress: '',
    notes: ''
  }
  editingOrderId.value = null
  tempIdCounter = 0
  customerInputValue.value = ''
  orderDiscounts.value = []
  discountForm.value = {
    orderId: 0,
    discountType: '',
    discountCode: '',
    discountAmount: 0,
    discountPercentage: 0,
    description: ''
  }
}

const handleDelete = (order: Order) => {
  if (!order.id) return

  $q.dialog({
    title: '確認刪除',
    message: `確定要刪除訂單 ${order.orderNumber || order.id} 嗎？此操作無法復原。`,
    cancel: true,
    persistent: true,
    color: 'negative'
  }).onOk(async () => {
    try {
      await orderApi.deleteOrder(order.id!)
      $q.notify({
        type: 'positive',
        message: '訂單已刪除',
        position: 'top'
      })
      loadOrders()
    } catch (error: any) {
      $q.notify({
        type: 'negative',
        message: error.response?.data?.message || '刪除訂單失敗',
        position: 'top'
      })
      console.error('Failed to delete order:', error)
    }
  })
}

const handlePayment = async (order: Order) => {
  if (!order.id) return

  try {
    // 載入完整的訂單資訊（如果需要）
    const response = await orderApi.getOrder(order.id)
    if (response.success && response.data) {
      paymentOrder.value = response.data
      paymentError.value = ''
      showPaymentDialog.value = true
      selectedGateway.value = 'ECPAY' // 預設選擇 ECPay
    } else {
      paymentOrder.value = order
      showPaymentDialog.value = true
      selectedGateway.value = 'ECPAY'
    }
  } catch (error: any) {
    // 如果載入失敗，使用現有的訂單資料
    paymentOrder.value = order
    showPaymentDialog.value = true
    selectedGateway.value = 'ECPAY'
  }
}

const handleShipment = async (order: Order) => {
  if (!order.id) return

  try {
    // 載入完整的訂單資訊
    const response = await orderApi.getOrder(order.id)
    if (response.success && response.data) {
      shipmentOrder.value = response.data
    } else {
      shipmentOrder.value = order
    }

    // 重置表單並填充預設值
    shipmentForm.value = {
      orderId: order.id!,
      shippingStatus: 'SHIPPED',
      shippingCompany: '',
      trackingNumber: '',
      recipientName: order.customerName || '',
      recipientPhone: order.customerPhone || '',
      recipientAddress: order.shippingAddress || '',
      notes: ''
    }

    showShipmentDialog.value = true
  } catch (error: any) {
    console.error('Failed to load order:', error)
    shipmentOrder.value = order
    shipmentForm.value = {
      orderId: order.id!,
      shippingStatus: 'SHIPPED',
      shippingCompany: '',
      trackingNumber: '',
      recipientName: order.customerName || '',
      recipientPhone: order.customerPhone || '',
      recipientAddress: order.shippingAddress || '',
      notes: ''
    }
    showShipmentDialog.value = true
  }
}

const handleShipmentFromDetail = () => {
  if (selectedOrder.value) {
    handleShipment(selectedOrder.value)
  }
}

const handleViewDetail = async (order: Order) => {
  if (!order.id) return

  try {
    // 載入完整的訂單資訊
    const response = await orderApi.getOrder(order.id)
    if (response.success && response.data) {
      // 後端返回的字段是 items，需要映射為 orderItems
      const orderData = response.data as any
      if (orderData.items && !orderData.orderItems) {
        orderData.orderItems = orderData.items
      }
      selectedOrder.value = orderData
    } else {
      selectedOrder.value = order
    }

    // 載入物流記錄
    await loadShipments(order.id)

    showDetailDialog.value = true
  } catch (error: any) {
    console.error('Failed to load order detail:', error)
    selectedOrder.value = order
    await loadShipments(order.id)
    showDetailDialog.value = true
  }
}

const loadShipments = async (orderId: number) => {
  shipmentsLoading.value = true
  try {
    const response = await shipmentApi.getShipmentsByOrderId(orderId)
    if (response.success && response.data) {
      shipments.value = Array.isArray(response.data) ? response.data : []
    } else {
      shipments.value = []
    }
  } catch (error: any) {
    console.error('Failed to load shipments:', error)
    shipments.value = []
  } finally {
    shipmentsLoading.value = false
  }
}

const getShippingStatusLabel = (status: OrderShipment['shippingStatus']) => {
  const labelMap: Record<string, string> = {
    PENDING: '待出貨',
    SHIPPED: '已出貨',
    DELIVERED: '已送達',
    RETURNED: '已退貨'
  }
  return labelMap[status] || status
}

const getShippingStatusColor = (status: OrderShipment['shippingStatus']) => {
  const colorMap: Record<string, string> = {
    PENDING: 'grey',
    SHIPPED: 'info',
    DELIVERED: 'positive',
    RETURNED: 'negative'
  }
  return colorMap[status] || 'grey'
}

// 獲取訂單項目（兼容後端的 items 和前端的 orderItems）
const getOrderItems = (order: Order | null): OrderItem[] => {
  if (!order) return []
  const orderData = order as any
  // 後端返回的是 items，前端接口定義的是 orderItems
  return orderData.items || orderData.orderItems || []
}

const handleShipmentSubmit = async () => {
  if (!shipmentForm.value.orderId) {
    $q.notify({
      type: 'negative',
      message: '訂單資訊不完整',
      position: 'top'
    })
    return
  }

  if (!shipmentForm.value.shippingCompany || !shipmentForm.value.trackingNumber) {
    $q.notify({
      type: 'negative',
      message: '請填寫物流公司和物流單號',
      position: 'top'
    })
    return
  }

  shipmentLoading.value = true

  try {
    // 創建物流記錄
    const shipmentData: OrderShipment = {
      orderId: shipmentForm.value.orderId,
      shippingStatus: 'SHIPPED',
      shippingCompany: shipmentForm.value.shippingCompany,
      trackingNumber: shipmentForm.value.trackingNumber,
      recipientName: shipmentForm.value.recipientName || undefined,
      recipientPhone: shipmentForm.value.recipientPhone || undefined,
      recipientAddress: shipmentForm.value.recipientAddress || undefined,
      notes: shipmentForm.value.notes || undefined
    }

    const response = await shipmentApi.createShipment(shipmentData)

    if (response.success) {
      $q.notify({
        type: 'positive',
        message: '已出貨設定成功！訂單狀態已更新為「處理中」',
        position: 'top'
      })

      showShipmentDialog.value = false
      loadOrders() // 重新載入訂單列表

      // 如果訂單詳情 dialog 是打開的，重新載入物流記錄
      if (showDetailDialog.value && selectedOrder.value?.id) {
        await loadShipments(selectedOrder.value.id)
      }
    } else {
      $q.notify({
        type: 'negative',
        message: response.message || '設定已出貨失敗',
        position: 'top'
      })
    }
  } catch (error: any) {
    console.error('Failed to create shipment:', error)
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || '設定已出貨失敗：' + (error.message || '未知錯誤'),
      position: 'top'
    })
  } finally {
    shipmentLoading.value = false
  }
}

const processPayment = async () => {
  if (!paymentOrder.value || !paymentOrder.value.orderNumber) {
    paymentError.value = '訂單資訊不完整'
    return
  }

  paymentLoading.value = true
  paymentError.value = ''

  try {
    // 準備支付請求
    const paymentRequest: PaymentRequest = {
      orderId: paymentOrder.value.id,
      orderNumber: paymentOrder.value.orderNumber,
      amount: paymentOrder.value.totalAmount,
      productName: `訂單 ${paymentOrder.value.orderNumber}`,
      customerName: paymentOrder.value.customerName || '',
      customerEmail: paymentOrder.value.customerEmail || '',
      customerPhone: paymentOrder.value.customerPhone || ''
    }

    // 創建支付請求
    const response = await createPayment(selectedGateway.value, paymentRequest)

    if (response.success && response.data) {
      const paymentData = response.data

      if (paymentData.paymentUrl) {
        // ECPay 需要使用 POST 表單提交
        if (selectedGateway.value === 'ECPAY') {
          submitECPayForm(paymentData.paymentUrl)
        } else {
          // 其他支付方式直接跳轉
          window.location.href = paymentData.paymentUrl
        }
      } else {
        paymentError.value = '未獲取到支付連結'
      }
    } else {
      paymentError.value = response.message || '創建支付請求失敗'
    }
  } catch (error: any) {
    console.error('Payment error:', error)
    paymentError.value = error.response?.data?.message || '支付處理失敗，請稍後再試'
  } finally {
    paymentLoading.value = false
  }
}

// 提交 ECPay 表單（使用 POST 方式）
const submitECPayForm = (paymentUrl: string) => {
  try {
    console.log('Submitting ECPay form with URL:', paymentUrl)

    // 解析 URL 獲取參數
    const url = new URL(paymentUrl)
    const params = new URLSearchParams(url.search)

    // ECPay 的 POST URL（不含參數）
    const postUrl = `${url.protocol}//${url.host}${url.pathname}`
    console.log('POST URL:', postUrl)

    // 創建動態表單
    const form = document.createElement('form')
    form.method = 'POST'
    form.action = postUrl
    form.target = '_self' // 確保在同一窗口提交
    form.style.display = 'none'

    // 添加所有參數作為隱藏 input
    // URLSearchParams 已經自動解碼，直接使用原始值
    // 瀏覽器在提交 POST 表單時會自動對表單字段值進行 URL 編碼
    params.forEach((value, key) => {
      const input = document.createElement('input')
      input.type = 'hidden'
      input.name = key
      input.value = value // URLSearchParams 已經解碼，使用原始值
      form.appendChild(input)
      console.log(`Added form field: ${key} = ${input.value}`)
    })

    // 添加到頁面並提交
    document.body.appendChild(form)
    console.log('Form created, submitting...')
    form.submit()
  } catch (error: any) {
    console.error('Failed to submit ECPay form:', error)
    paymentError.value = '提交支付表單失敗：' + error.message
    $q.notify({
      type: 'negative',
      message: '提交支付表單失敗：' + error.message,
      position: 'top'
    })
  }
}

const handleEdit = async (order: Order) => {
  if (!order.id) return

  editingOrderId.value = order.id
  showDialog.value = true

  try {
    // 載入訂單詳情
    const response = await orderApi.getOrder(order.id)
    if (response.success && response.data) {
      const orderData = response.data

      // 填充表單數據
      form.value.customerId = orderData.customerId || null
      form.value.customerName = orderData.customerName || ''
      form.value.customerPhone = orderData.customerPhone || ''
      form.value.customerEmail = orderData.customerEmail || ''
      form.value.status = orderData.status
      form.value.pickupType = orderData.pickupType as 'DELIVERY' | 'STORE_PICKUP' | 'CROSS_STORE_PICKUP'
      form.value.subtotalAmount = orderData.subtotalAmount || 0
      form.value.discountAmount = orderData.discountAmount || 0
      form.value.shippingFee = orderData.shippingFee || 0
      form.value.totalAmount = orderData.totalAmount || 0
      form.value.shippingAddress = orderData.shippingAddress || ''
      form.value.notes = orderData.notes || ''

      // 填充訂單項目
      if (orderData.items && orderData.items.length > 0) {
        form.value.items = orderData.items.map((item: any, index: number) => ({
          tempId: ++tempIdCounter,
          productId: item.productId,
          quantity: item.quantity,
          unitPrice: item.unitPrice || item.price || 0,
          subtotal: (item.unitPrice || item.price || 0) * (item.quantity || 0),
          productName: item.productName
        }))
        calculateTotal()
      }

      // 設置客戶輸入值
      if (orderData.customerName) {
        customerInputValue.value = orderData.customerName
      }

      // 載入訂單的折扣記錄
      await loadOrderDiscounts(order.id!)
    }
  } catch (error) {
    console.error('Failed to load order:', error)
    $q.notify({
      type: 'negative',
      message: '載入訂單失敗',
      position: 'top'
    })
  }
}

const addOrderItem = () => {
  form.value.items.push({
    tempId: ++tempIdCounter,
    productId: 0,
    specificationId: undefined,
    quantity: 1,
    unitPrice: 0,
    subtotal: 0
  })
}

const removeOrderItem = (index: number) => {
  form.value.items.splice(index, 1)
  calculateTotal()
}

const onProductChange = async (item: any, productId: number) => {
  const product = products.value.find(p => p.id === productId)
  if (product) {
    item.productName = product.name
    // 重置規格選擇
    item.specificationId = undefined

    // 載入該商品的規格
    if (productId) {
      await loadProductSpecifications(productId)
    }

    // 如果沒有規格，使用商品價格
    const specs = productSpecifications.value.get(productId) || []
    if (specs.length === 0) {
      item.unitPrice = product.salePrice ?? product.basePrice ?? 0
      calculateItemSubtotal(item)
    }
  }
}

const onSpecificationChange = (item: any, specificationId: number | undefined) => {
  if (specificationId && item.productId) {
    const specs = productSpecifications.value.get(item.productId) || []
    const spec = specs.find(s => s.id === specificationId)
    if (spec) {
      // 從規格中獲取價格
      if (spec.price != null) {
        item.unitPrice = spec.price
      }
      // 設置規格信息
      item.productSku = spec.sku
      item.productSpec = spec.specName
      calculateItemSubtotal(item)
    }
  } else {
    // 如果沒有選擇規格，使用商品價格
    const product = products.value.find(p => p.id === item.productId)
    if (product) {
      item.unitPrice = product.salePrice ?? product.basePrice ?? 0
      item.productSku = undefined
      item.productSpec = undefined
      calculateItemSubtotal(item)
    }
  }
}

const getSpecificationOptions = (productId: number | undefined): ProductSpecification[] => {
  if (!productId) return []
  return productSpecifications.value.get(productId) || []
}

const loadProductSpecifications = async (productId: number) => {
  // 如果已經載入過，就不重複載入
  if (productSpecifications.value.has(productId)) {
    return
  }

  try {
    const response = await productSpecificationApi.getProductSpecifications(productId)
    if (response.success && response.data) {
      // 只顯示啟用的規格
      const enabledSpecs = response.data.filter(spec => spec.enabled !== false)
      productSpecifications.value.set(productId, enabledSpecs)
    }
  } catch (error) {
    console.error('載入商品規格失敗:', error)
    // 如果載入失敗，設置為空數組
    productSpecifications.value.set(productId, [])
  }
}

const onCustomerChange = (customerId: number | null) => {
  if (customerId) {
    const customer = customers.value.find(c => c.id === customerId)
    if (customer) {
      // 自動填充客戶資訊
      form.value.customerName = customer.name
      form.value.customerPhone = customer.phone || ''
      form.value.customerEmail = customer.email
      customerInputValue.value = customer.name
    }
  } else {
    // 清除選擇時，保持輸入框中的文字
    // customerInputValue 會保持用戶輸入的文字
  }
}

const calculateItemSubtotal = (item: any) => {
  item.subtotal = (item.unitPrice || 0) * (item.quantity || 0)
  calculateTotal()
}

const openDiscountDialog = () => {
  if (!editingOrderId.value) {
    $q.notify({
      type: 'negative',
      message: '請先保存訂單後才能管理折扣記錄',
      position: 'top',
      timeout: 3000,
      actions: [
        { label: '知道了', color: 'white' }
      ]
    })
    return
  }

  // 設置訂單ID
  discountForm.value.orderId = editingOrderId.value
  showDiscountDialog.value = true

  // 載入該訂單的折扣記錄
  loadOrderDiscounts(editingOrderId.value)
}

const loadOrderDiscounts = async (orderId: number) => {
  try {
    const response = await orderDiscountApi.getDiscountsByOrderId(orderId)
    if (response.success && response.data) {
      orderDiscounts.value = response.data
    } else {
      orderDiscounts.value = []
    }
  } catch (error) {
    console.error('Failed to load order discounts:', error)
    orderDiscounts.value = []
  }
}

// 監聽折扣記錄變化，自動更新折扣金額
watch(totalDiscountAmount, (newValue) => {
  if (editingOrderId.value) {
    form.value.discountAmount = newValue
    calculateTotal()
  }
}, { immediate: true })

// 監聽折扣對話框打開，自動設置訂單ID
watch(showDiscountDialog, (isOpen) => {
  if (isOpen && editingOrderId.value) {
    discountForm.value.orderId = editingOrderId.value
  }
})

const handleAddDiscount = async () => {
  if (!discountForm.value.discountType || discountForm.value.discountAmount === undefined || discountForm.value.discountAmount < 0) {
    $q.notify({
      type: 'warning',
      message: '請填寫必填字段',
      position: 'top'
    })
    return
  }

  // 確保訂單ID已設置
  if (!editingOrderId.value) {
    $q.notify({
      type: 'warning',
      message: '請先保存訂單',
      position: 'top'
    })
    return
  }

  // 確保訂單ID與當前編輯的訂單一致（防止不一致的情況）
  if (!discountForm.value.orderId || discountForm.value.orderId !== editingOrderId.value) {
    discountForm.value.orderId = editingOrderId.value
  }

  try {
    await orderDiscountApi.addDiscount(discountForm.value)
    $q.notify({
      type: 'positive',
      message: '折扣添加成功',
      position: 'top'
    })
    // 重置表單
    discountForm.value = {
      orderId: editingOrderId.value,
      discountType: '',
      discountCode: '',
      discountAmount: 0,
      discountPercentage: 0,
      description: ''
    }
    // 重新載入折扣記錄
    await loadOrderDiscounts(editingOrderId.value)
  } catch (error: any) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || '添加折扣失敗',
      position: 'top'
    })
  }
}

const handleDeleteDiscount = (discountId: number) => {
  $q.dialog({
    title: '確認刪除',
    message: '確定要刪除這條折扣記錄嗎？',
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await orderDiscountApi.deleteDiscount(discountId)
      $q.notify({
        type: 'positive',
        message: '折扣刪除成功',
        position: 'top'
      })
      // 重新載入折扣記錄
      if (editingOrderId.value) {
        await loadOrderDiscounts(editingOrderId.value)
      }
    } catch (error: any) {
      $q.notify({
        type: 'negative',
        message: error.response?.data?.message || '刪除折扣失敗',
        position: 'top'
      })
    }
  })
}

const calculateTotal = () => {
  // 計算小計
  form.value.subtotalAmount = form.value.items.reduce((sum, item) => {
    return sum + (item.subtotal || 0)
  }, 0)

  // 計算總金額 = 小計 - 折扣 + 運費
  form.value.totalAmount = form.value.subtotalAmount - (form.value.discountAmount || 0) + (form.value.shippingFee || 0)
}

const handleSubmit = async () => {
  // 驗證訂單項目
  if (!form.value.items || form.value.items.length === 0) {
    $q.notify({
      type: 'warning',
      message: '請至少添加一個訂單項目',
      position: 'top'
    })
    return
  }

  // 驗證所有項目
  for (const item of form.value.items) {
    if (!item.productId || item.quantity <= 0 || item.unitPrice <= 0) {
      $q.notify({
        type: 'warning',
        message: '請完整填寫所有訂單項目',
        position: 'top'
      })
      return
    }
  }

  try {
    // 準備訂單數據
    const orderData: any = {
      status: form.value.status,
      pickupType: form.value.pickupType,
      subtotalAmount: form.value.subtotalAmount,
      discountAmount: form.value.discountAmount || 0,
      shippingFee: form.value.shippingFee || 0,
      totalAmount: form.value.totalAmount,
      shippingAddress: form.value.shippingAddress || '',
      notes: form.value.notes || '',
      items: form.value.items.map(item => ({
        productId: item.productId,
        specificationId: item.specificationId,
        quantity: item.quantity,
        unitPrice: item.unitPrice
      }))
    }

    // 如果有選擇客戶ID，使用ID；否則使用手動輸入的信息（設置 customerId 為 0 以避免後端驗證失敗）
    if (form.value.customerId) {
      orderData.customerId = form.value.customerId
      // 即使有 customerId，也保存客戶信息（用於顯示和備份）
      if (form.value.customerName) {
        orderData.customerName = form.value.customerName
      }
      if (form.value.customerPhone) {
        orderData.customerPhone = form.value.customerPhone
      }
      if (form.value.customerEmail) {
        orderData.customerEmail = form.value.customerEmail
      }
    } else {
      // 手動輸入時，customerId 設為 0（表示未註冊客戶），使用客戶信息
      orderData.customerId = 0
      if (form.value.customerName) {
        orderData.customerName = form.value.customerName
      }
      if (form.value.customerPhone) {
        orderData.customerPhone = form.value.customerPhone
      }
      if (form.value.customerEmail) {
        orderData.customerEmail = form.value.customerEmail
      }
    }

    if (editingOrderId.value) {
      // 更新訂單
      const response = await orderApi.updateOrder(editingOrderId.value, orderData)
      $q.notify({
        type: 'positive',
        message: '訂單更新成功',
        position: 'top'
      })
      // 更新訂單ID（雖然通常不會改變）
      if (response.success && response.data?.id) {
        editingOrderId.value = response.data.id
      }
    } else {
      // 創建訂單
      const response = await orderApi.createOrder(orderData)
      if (response.success && response.data?.id) {
        // 設置訂單ID，以便可以添加折扣記錄
        editingOrderId.value = response.data.id
        // 更新折扣表單的訂單ID
        discountForm.value.orderId = response.data.id
      }
      $q.notify({
        type: 'positive',
        message: '訂單創建成功',
        position: 'top'
      })
    }
    showDialog.value = false
    resetForm()
    loadOrders()
  } catch (error: any) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || '創建訂單失敗',
      position: 'top'
    })
    console.error('Failed to create order:', error)
  }
}

// 監聽分頁變化
watch(() => pagination.value.page, () => {
  if (hasActiveFilters.value) {
    loadOrders(true)
  } else {
    loadOrders(false)
  }
})

// 啟動訂單管理導覽
const handleStartTour = () => {
  nextTick(() => {
    startOrderTour(true)
  })
}

onMounted(() => {
  loadOrders()
  loadCustomers()
  loadProducts()
  loadOrderQAStats()

  // 如果用戶是第一次訪問訂單管理頁面，自動啟動導覽
  if (!isOrderTourCompleted()) {
    setTimeout(() => {
      startOrderTour()
    }, 1500)
  }
})
</script>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.order-metric-card {
  border-radius: 16px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 250, 252, 0.96));
  box-shadow: 0 10px 26px rgba(15, 23, 42, 0.06);
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.order-metric-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 14px 32px rgba(15, 23, 42, 0.1);
}

.order-metric-card__value {
  margin: 6px 0 4px;
  font-size: 1.55rem;
  font-weight: 700;
  line-height: 1.1;
  color: #0f172a;
}

.order-metric-card--amber {
  border-color: rgba(245, 158, 11, 0.24);
  background: linear-gradient(180deg, rgba(255, 251, 235, 0.98), rgba(255, 247, 237, 0.96));
}

.order-metric-card--blue {
  border-color: rgba(59, 130, 246, 0.2);
  background: linear-gradient(180deg, rgba(239, 246, 255, 0.98), rgba(238, 242, 255, 0.96));
}

.order-metric-card--green {
  border-color: rgba(34, 197, 94, 0.22);
  background: linear-gradient(180deg, rgba(240, 253, 244, 0.98), rgba(236, 253, 245, 0.96));
}

.order-filter-card,
.order-table-card {
  border-radius: 18px;
  border: 1px solid rgba(148, 163, 184, 0.2);
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 12px 32px rgba(15, 23, 42, 0.05);
}

.order-filter-card__header {
  padding-bottom: 12px;
}

.filter-card__meta {
  gap: 8px;
}

.order-filter-chip-list :deep(.q-chip) {
  border-radius: 999px;
  font-weight: 500;
}

.order-admin-table :deep(.q-table__top) {
  padding: 14px 16px 8px;
}

.order-table-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.order-admin-table :deep(thead tr th) {
  position: sticky;
  top: 0;
  z-index: 1;
  background: #f8fafc;
  color: #334155;
  font-weight: 700;
  border-bottom: 1px solid rgba(148, 163, 184, 0.22);
}

.order-admin-table :deep(tbody tr) {
  transition: background-color 0.18s ease;
}

.order-admin-table :deep(tbody tr:hover) {
  background: rgba(59, 130, 246, 0.04);
}

.order-admin-table :deep(td),
.order-admin-table :deep(th) {
  white-space: normal;
  vertical-align: middle;
}

.order-actions-cell {
  min-width: 250px;
}

.order-number-link {
  cursor: pointer;
  user-select: none;
}

.order-row-btn,
.order-row-icon-btn {
  margin-right: 4px;
  margin-bottom: 4px;
}

.order-status-guide-card {
  border-radius: 18px;
  border: 1px solid rgba(148, 163, 184, 0.2);
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.12);
}

.order-status-guide-banner {
  background: linear-gradient(90deg, rgba(59, 130, 246, 0.08), rgba(14, 165, 233, 0.08));
  color: #0f172a;
  border: 1px solid rgba(59, 130, 246, 0.14);
}

.order-status-guide-list {
  border-radius: 14px;
  overflow: hidden;
}

.order-status-guide-item {
  min-height: 64px;
}

.order-flow-dialog-card,
.order-detail-dialog-card {
  border-radius: 18px;
  border: 1px solid rgba(148, 163, 184, 0.22);
  box-shadow: 0 18px 42px rgba(15, 23, 42, 0.12);
}

.order-flow-dialog-card__header,
.order-detail-dialog-card__header {
  border-bottom: 1px solid rgba(148, 163, 184, 0.18);
}

.order-flow-dialog-card__body {
  padding-top: 14px;
}

.order-flow-dialog-card__actions {
  border-top: 1px solid rgba(148, 163, 184, 0.12);
}

.order-flow-banner {
  background: linear-gradient(90deg, rgba(59, 130, 246, 0.08), rgba(34, 197, 94, 0.08));
  color: #0f172a;
  border: 1px solid rgba(59, 130, 246, 0.12);
}

.order-info-panel {
  border-radius: 14px;
  padding: 12px;
  background: rgba(248, 250, 252, 0.8);
  border: 1px solid rgba(148, 163, 184, 0.18);
}

.order-info-panel__list {
  border-radius: 12px;
  overflow: hidden;
}

.order-payment-options {
  background: #fff;
  border: 1px solid rgba(148, 163, 184, 0.16);
  border-radius: 12px;
  padding: 8px 10px;
}

.order-shipment-note-banner {
  border-radius: 12px;
}

.order-detail-dialog-card__body {
  background: linear-gradient(180deg, rgba(248, 250, 252, 0.6), rgba(255, 255, 255, 1));
}

.order-detail-dialog-card__actions {
  border-top: 1px solid rgba(148, 163, 184, 0.14);
}

.order-detail-summary-card,
.order-detail-panel-card {
  border-radius: 16px;
  border-color: rgba(148, 163, 184, 0.2);
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.04);
  background: rgba(255, 255, 255, 0.96);
}

.order-detail-list {
  border-radius: 12px;
  overflow: hidden;
}

@media (max-width: 1023px) {
  .order-actions-cell {
    min-width: 220px;
  }

  .order-admin-table :deep(.q-table__middle) {
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
  }

  .order-admin-table :deep(table) {
    min-width: 980px;
  }

  .order-detail-dialog-card__body {
    padding: 16px !important;
  }
}

@media (max-width: 599px) {
  .order-metric-card__value {
    font-size: 1.35rem;
  }

  .filter-card__meta {
    align-items: flex-start;
    flex-direction: column;
  }

  .order-table-toolbar {
    align-items: flex-start;
  }

  .order-actions-cell {
    min-width: 210px;
  }

  .order-admin-table :deep(.q-table__top) {
    padding-top: 12px;
  }

  .order-status-guide-card {
    min-width: unset !important;
    width: calc(100vw - 24px);
  }

  .order-flow-dialog-card {
    min-width: unset !important;
    width: calc(100vw - 24px);
  }

  .order-flow-dialog-card__actions {
    justify-content: stretch;
    gap: 8px;
  }

  .order-flow-dialog-card__actions :deep(.q-btn) {
    flex: 1 1 0;
    min-height: 44px;
  }

  .order-detail-dialog-card__header {
    padding-bottom: 10px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .order-metric-card {
    transition: none;
  }
}
</style>
