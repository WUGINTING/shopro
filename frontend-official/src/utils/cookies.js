import Cookies from 'js-cookie';
import { getEnvs } from '@/utils/envs';
import { cookieWhiteList } from '@/config/constant';
const { hostname } = window.location;

class CookieProxy {
  constructor() {
    this.prefix = this.getPrefix();
    this.baseParams = {
      expires: 7,
      path: '/',
      domain: hostname || undefined,
      // Secure : true,
      // SameSite : 'none',
    };
  }

  // TODO, 可以根據自己需要進行修改， 用途是為了打包一次，讓多個環境同時部署
  getPrefix() {
    const { envStr } = getEnvs();
    let cookiePreFix;
    if (envStr === 'dev') {
      cookiePreFix = 'dev_';
    } else if (envStr === 'prod') {
      cookiePreFix = 'prod_';
    } else {
      cookiePreFix = 'test_';
    }
    return cookiePreFix;
  }

  getAll() {
    return Cookies.get();
  }

  clearAll() {
    const keys = Object.keys(this.getAll());
    keys.forEach(key => {
      const newKey = key.replace(this.prefix, '');
      if (cookieWhiteList.indexOf(newKey) === -1) {
        this.remove(key, false);
      }
    });
  }

  get(key, hasPrefix = true) {
    const keyStr = hasPrefix ? this.prefix + '' + key : key;
    return Cookies.get(keyStr);
  }

  set(key, value, params) {
    const options = params === undefined ? this.baseParams : params;
    const keyStr = this.prefix + '' + key;
    return Cookies.set(keyStr, value, options);
  }

  remove(key, hasPrefix = true) {
    const keyStr = !hasPrefix ? key : this.prefix + '' + key;
    return Cookies.remove(keyStr, {
      path: '/',
      domain: hostname,
    });
  }
}
const cookies = new CookieProxy();

export default cookies;
