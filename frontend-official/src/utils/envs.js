const env = import.meta.env;
const { MODE } = env;

// TODO, 可以根據自己需要進行修改， 用途是為了打包一次，讓多個環境同時部署
export function getEnvs() {
  let envStr = '';
  if (MODE === 'development') {
    envStr = 'dev';
  } else if (MODE === 'production') {
    envStr = 'prod';
  } else {
    envStr = 'test';
  }
  return {
    envStr,
  };
}
