/**
 * 後端以 LocalDateTime（商店當地時間，不含時區）接收日期時間：
 * 送出當地時間字串，不要用 toISOString()（會轉成 UTC，在台灣差 8 小時）
 */
const pad = (value: number) => String(value).padStart(2, '0')

/** 當地日期 YYYY-MM-DD */
export const toLocalDate = (date: Date) =>
  `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`

/** 當地日期時間 YYYY-MM-DDTHH:mm:ss；無法解析時原樣回傳 */
export const toLocalDateTime = (value: string | Date) => {
  const date = value instanceof Date ? value : new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)
  return `${toLocalDate(date)}T${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}
