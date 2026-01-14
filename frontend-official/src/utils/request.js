import axios from 'axios';
import { Notify } from 'quasar';
import cookies from './cookies.js';
import { TokenKey } from '../config/constant.js';

// 建立 axios 實例
const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
  },
});

// 請求攔截器
service.interceptors.request.use(
  config => {
    // 從 cookies 獲取 token 並添加到請求標頭
    const token = cookies.get(TokenKey);
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    console.error('請求錯誤：', error);
    return Promise.reject(error);
  }
);

// 回應攔截器
service.interceptors.response.use(
  response => {
    const res = response.data;

    // 如果回應狀態碼不是 200，則判定為錯誤
    if (response.status !== 200) {
      Notify.create({
        type: 'negative',
        message: res.message || '請求失敗',
        position: 'top',
      });
      return Promise.reject(new Error(res.message || '請求失敗'));
    }

    return res;
  },
  error => {
    console.error('回應錯誤：', error);

    let message = '網路錯誤，請稍後再試';

    if (error.response) {
      // 伺服器回應了錯誤狀態碼
      switch (error.response.status) {
        case 400:
          message = '請求參數錯誤';
          break;
        case 401:
          message = '未授權，請重新登入';
          break;
        case 403:
          message = '拒絕訪問';
          break;
        case 404:
          message = '請求的資源不存在';
          break;
        case 500:
          message = '伺服器錯誤';
          break;
        case 503:
          message = '服務暫時無法使用';
          break;
        default:
          message = error.response.data?.message || message;
      }
    } else if (error.request) {
      // 請求已發送但沒有收到回應
      message = '無法連接到伺服器';
    }

    Notify.create({
      type: 'negative',
      message,
      position: 'top',
    });

    return Promise.reject(error);
  }
);

// 封裝請求方法
const http = {
  request(config) {
    return service.request(config);
  },
  get(url, params, config = {}) {
    return service.get(url, { params, ...config });
  },
  post(url, data, config = {}) {
    return service.post(url, data, config);
  },
  put(url, data, config = {}) {
    return service.put(url, data, config);
  },
  delete(url, config = {}) {
    return service.delete(url, config);
  },
};

export default http;
