# ECPay Payment Integration Documentation

## Overview

This document describes the ECPay payment integration for the Shopro e-commerce platform. ECPay (綠界科技) is a popular payment gateway in Taiwan that supports multiple payment methods including credit cards, ATM transfers, and convenience store payments.

## Architecture

### Components

1. **EcPayConfig** - Configuration class that holds ECPay credentials and API endpoints
2. **EcPayService** - Core service implementing payment operations
3. **PaymentCallbackService** - Handles payment callback notifications from ECPay
4. **PaymentGatewayController** - REST API endpoints for payment operations

### Key Features

- ✅ Create payment requests with ECPay
- ✅ Secure CheckMacValue generation and verification
- ✅ Payment callback handling (success, failure, cancellation)
- ✅ Automatic order status updates
- ✅ Payment history tracking
- ✅ Comprehensive test coverage
- ✅ Support for ECPay staging and production environments

## Configuration

### Application Properties

Configure ECPay credentials in `application.properties`:

```properties
# ECPay Configuration
payment.ecpay.merchant-id=YOUR_ECPAY_MERCHANT_ID
payment.ecpay.hash-key=YOUR_ECPAY_HASH_KEY
payment.ecpay.hash-iv=YOUR_ECPAY_HASH_IV
payment.ecpay.api-url=https://payment-stage.ecpay.com.tw
payment.ecpay.return-url=http://localhost:8080/payment/result
payment.ecpay.notify-url=http://localhost:8080/api/payment-gateway/callback/ecpay
payment.ecpay.sandbox=true
```

### Test Environment

For testing, use ECPay's staging environment credentials:
- Merchant ID: `2000132`
- HashKey: `5294y06JbISpM5x9`
- HashIV: `v77hoKGq4kWxNNIS`
- API URL: `https://payment-stage.ecpay.com.tw`

## API Endpoints

### 1. Create Payment

**POST** `/api/payment-gateway/create?gateway=ECPAY`

Creates a new payment request with ECPay.

**Request Body:**
```json
{
  "orderNumber": "ORD20240101001",
  "amount": 1000,
  "productName": "測試商品",
  "customerEmail": "customer@example.com",
  "customerPhone": "0912345678"
}
```

**Response:**
```json
{
  "success": true,
  "message": "支付請求已建立",
  "data": {
    "gateway": "ECPAY",
    "status": "INITIATED",
    "orderNumber": "ORD20240101001",
    "amount": 1000,
    "paymentUrl": "https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5?...",
    "transactionId": "ORD20240101001"
  }
}
```

### 2. ECPay Callback (Server-to-Server)

**POST** `/api/payment-gateway/callback/ecpay`

ECPay sends payment result notifications to this endpoint.

**Parameters:** (Form-encoded)
- `MerchantID`: Merchant identifier
- `MerchantTradeNo`: Order number
- `TradeNo`: ECPay transaction ID
- `RtnCode`: Result code (1 = success)
- `RtnMsg`: Result message
- `TradeAmt`: Payment amount
- `CheckMacValue`: Security checksum

**Response:**
```
1|OK  (for success)
0|Error message  (for failure)
```

## Security

### CheckMacValue Generation

ECPay uses CheckMacValue to ensure data integrity and authenticity:

1. Sort all parameters alphabetically (case-insensitive)
2. Concatenate: `HashKey=xxx&param1=value1&param2=value2&...&HashIV=yyy`
3. URL encode the string
4. Convert to lowercase
5. SHA-256 hash
6. Convert to uppercase

The `EcPayService` handles this automatically in the `generateCheckMacValue()` method.

### Callback Verification

All callback requests from ECPay are verified using:
1. CheckMacValue validation
2. Matching order numbers with database records
3. Checking order status before updates

## Payment Flow

### 1. Customer Initiates Payment

```
Customer → Frontend → Backend API → ECPay Service
                                   → Generate CheckMacValue
                                   → Return Payment URL
```

### 2. Customer Completes Payment

```
Customer → ECPay Payment Page → Complete Payment → ECPay System
```

### 3. Payment Callback

```
ECPay System → Backend Callback Endpoint → Verify CheckMacValue
                                         → Update Order Status
                                         → Create Payment Record
                                         → Log History
                                         → Return "1|OK"
```

### 4. Customer Returns

```
ECPay System → Redirect Customer → Frontend Result Page
```

## Order Status Flow

```
PENDING_PAYMENT → (Payment Success) → PAID → PROCESSING → COMPLETED
                 ↓
                 (Payment Failed/Cancelled) → CANCELLED
```

## Payment Status Flow

```
PENDING → (Callback Success) → PAID
        ↓
        (Callback Failure) → FAILED
```

## Database Schema

### Order Table Updates
- Added `payment_time` column (DATETIME)
- Added `PAID` status to OrderStatus enum

### OrderPayment Table
- Stores payment transaction details
- Links to orders via `order_id`
- Tracks payment gateway and transaction IDs
- Records payment status changes

## Error Handling

### Common Errors

1. **Invalid CheckMacValue**
   - Cause: Incorrect HashKey/HashIV or parameter tampering
   - Response: "0|CheckMacValue 驗證失敗"

2. **Order Not Found**
   - Cause: Invalid order number in callback
   - Action: Log error and return failure

3. **Invalid Order Status**
   - Cause: Order already paid or cancelled
   - Action: Log warning and skip update

## Testing

### Unit Tests

Two comprehensive test suites are provided:

1. **EcPayServiceTest** - Tests payment creation, verification, and callback parsing
2. **PaymentCallbackServiceTest** - Tests callback handling logic

Run tests with:
```bash
./mvnw test -Dtest="EcPayServiceTest,PaymentCallbackServiceTest"
```

### Integration Testing

For integration testing with ECPay's staging environment:

1. Configure staging credentials in `application-test.properties`
2. Use ECPay's test credit cards:
   - Card: 4311-9522-2222-2222
   - Expiry: Any future date
   - CVV: 222

## Monitoring and Logging

All payment operations are logged with appropriate levels:

- **INFO**: Normal payment operations
- **WARN**: Non-critical issues (e.g., query not supported)
- **ERROR**: Critical failures (e.g., callback verification failure)

Key log events:
- Payment request creation
- CheckMacValue verification
- Callback processing
- Order status updates

## Best Practices

1. **Always verify CheckMacValue** in callbacks
2. **Use HTTPS** for all callback URLs in production
3. **Log all payment transactions** for audit trail
4. **Handle idempotency** - Process each callback only once
5. **Validate order status** before updates
6. **Return proper response codes** to ECPay (1|OK or 0|Error)

## Troubleshooting

### Payment URL Not Generated

**Symptoms:** Empty or invalid payment URL
**Solutions:**
- Verify ECPay configuration
- Check HashKey and HashIV
- Ensure orderNumber is unique

### Callback Not Received

**Symptoms:** Order stays in PENDING_PAYMENT status
**Solutions:**
- Verify notifyUrl is accessible from internet
- Check firewall settings
- Review ECPay merchant backend for callback failures

### CheckMacValue Mismatch

**Symptoms:** Callback verification fails
**Solutions:**
- Verify HashKey and HashIV match ECPay settings
- Check for URL encoding issues
- Ensure parameter order is correct

## Future Enhancements

Potential improvements for future versions:

1. **Refund Support** - Implement ECPay refund API
2. **Recurring Payments** - Support subscription billing
3. **Payment Method Selection** - Allow specific payment methods
4. **Installment Payments** - Support credit card installments
5. **Query API** - Implement payment status queries (currently not supported by ECPay for AIO checkout)

## References

- [ECPay Official Documentation](https://www.ecpay.com.tw/Service/API_Dwnld)
- [ECPay Developer Center](https://developers.ecpay.com.tw/)
- [ECPay Test Environment](https://payment-stage.ecpay.com.tw/)

## Support

For issues or questions regarding this integration:
1. Check logs in `/logs` directory
2. Review test cases for examples
3. Consult ECPay documentation
4. Contact ECPay technical support for gateway-specific issues

---

**Last Updated:** 2026-01-12
**Version:** 1.0.0
**Maintainers:** E-commerce Platform Team
