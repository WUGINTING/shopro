import http from '@/utils/request';

/**
 * 設定 SSO 配置
 * @param {Object} data - SSO 配置資料
 */
export function ssoSet(data) {
  return http.request({
    url: '/account/ssoSet',
    method: 'post',
    data,
  });
}
