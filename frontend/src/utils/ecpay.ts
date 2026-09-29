/**
 * 導向綠界付款頁：後端回傳的付款網址帶有所有參數，綠界要求以 POST 表單送出
 */
export const redirectToEcPay = (paymentUrl: string) => {
  const url = new URL(paymentUrl)
  const formEl = document.createElement('form')
  formEl.method = 'POST'
  formEl.action = `${url.origin}${url.pathname}`
  formEl.style.display = 'none'

  url.searchParams.forEach((value, key) => {
    const input = document.createElement('input')
    input.type = 'hidden'
    input.name = key
    input.value = value
    formEl.appendChild(input)
  })

  document.body.appendChild(formEl)
  formEl.submit()
}
