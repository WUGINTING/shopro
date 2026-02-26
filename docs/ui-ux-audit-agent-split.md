# UI/UX Audit Issue List and 3-Agent Parallel Work Split

## Purpose

This document converts the current UI/UX audit findings into a parallel execution plan for 3 AI agents.

Goals:

- Remove user-facing mojibake/garbled text in critical flows
- Fix confusing navigation/role logic
- Improve UI/UX consistency without changing core business behavior unless necessary
- Avoid merge conflicts by splitting files cleanly

Scope for this round:

- Frontend only (`frontend/src`)
- Focus on storefront + admin shell + routing + high-frequency admin pages
- No backend API schema changes in this document

## Execution Strategy (Parallel)

Run 3 agents in parallel with non-overlapping file ownership:

- Agent A: Storefront critical conversion flow (home/layout/cart/checkout/product list)
- Agent B: Routing and role logic + shared UX consistency
- Agent C: Admin UI text cleanup (priority on `ProductView`, then remaining admin hotspots)

Recommended workflow:

1. Each agent works on a separate branch.
2. Agent B merges first (router/shared flow logic may affect A/C behavior).
3. Agent A merges second (storefront critical UX).
4. Agent C merges last (large admin-page text cleanup, highest conflict risk if delayed).

Suggested branch names:

- `agent-a/storefront-ux-text-fix`
- `agent-b/router-role-logic-ux`
- `agent-c/admin-text-cleanup`

## Global Rules for All Agents

- Preserve existing API contracts.
- Do not silently change business logic unless listed in assigned tasks.
- Keep `zh-TW` display formatting consistent (`toLocaleString('zh-TW')`, `NT$`).
- Add/keep `aria-label` for icon-only buttons.
- Run `npm run build-only` before handoff.
- Include a short changelog in PR description: files changed + user-visible behavior changes.

## Priority Matrix

P0 (must fix first):

- Storefront garbled text in homepage/layout/cart/checkout/product list
- Router/global loading/error garbled text
- Customer/admin route confusion

P1:

- Product detail language consistency + loading/error states
- Admin icon-button accessibility labels
- Admin dashboard dead quick actions

P2:

- Broader admin text cleanup and consistency pass

---

## Agent A (Storefront Critical UX / Conversion Flow)

### Objective

Fix all user-facing garbled text and key interaction clarity in the storefront paths that directly affect conversion.

### File Ownership (Agent A only)

- `frontend/src/layouts/PublicLayout.vue`
- `frontend/src/views/store/StoreHomeView.vue`
- `frontend/src/views/store/CartView.vue`
- `frontend/src/views/store/CheckoutView.vue`
- `frontend/src/views/store/StoreProductListView.vue`
- `frontend/src/views/store/StoreProductDetailView.vue`

### Tasks

1. Replace all garbled/mojibake text in the owned files.
2. Keep wording in Traditional Chinese and consistent with storefront terminology.
3. Preserve current working features:
   - favorites / compare (localStorage)
   - cart summary
   - checkout draft autofill
   - ECPay redirect flow
4. Ensure labels/hints/errors are understandable in cart/checkout.
5. Make product detail page language consistent (currently mixed English/Chinese).
6. Add missing loading/error states on product detail page (frontend-only behavior).

### Key Findings to Fix (from audit)

- `PublicLayout.vue`: top strip, nav labels, CTA labels, footer links/copy, aria labels
- `StoreHomeView.vue`: hero, trust signals, quick entries, featured promotions, latest blog, empty states
- `CartView.vue`: page title, empty state, table headers, summary labels, notices, checkout CTA, not-logged-in / wrong-role notices
- `CheckoutView.vue`: page title, form labels, hints, rules, payment/shipping labels, save draft prompt, error messages
- `StoreProductListView.vue`: hero, search/sort labels, compare/favorite labels and notices, empty state, stock labels
- `StoreProductDetailView.vue`: unify labels/buttons/messages to zh-TW and add error/loading feedback

### Acceptance Criteria

- No visible `???`, mojibake, or broken Chinese in owned files.
- Storefront critical routes are readable and usable:
  - `/`
  - `/products`
  - `/products/:id`
  - `/cart`
  - `/checkout`
- `npm run build-only` passes.

### Notes / Risks

- Avoid changing checkout payload fields.
- If touching ECPay wording, do not change redirect/form submit logic.

---

## Agent B (Routing / Role Logic / Shared UX Consistency)

### Objective

Fix logic-level UX inconsistencies that confuse users (especially customer vs admin path behavior), and clean shared messages.

### File Ownership (Agent B only)

- `frontend/src/router/index.ts`
- `frontend/src/views/HomeView.vue` (only routing/role UX issues and dead actions)
- `frontend/src/layouts/MainLayout.vue` (accessibility labels only)
- `frontend/src/views/store/OrderSuccessView.vue`
- `frontend/src/views/store/AccountOrdersView.vue` (accessibility tweak only)

### Tasks

1. Fix router loading/error mojibake messages.
2. Review and correct role access logic:
   - `CUSTOMER` should not be treated as normal admin operator for `/admin/*` routes unless explicitly required.
3. Resolve path ambiguity / UX confusion caused by legacy redirects and customer navigation.
4. Fix customer shortcuts in admin home (or prevent customer from landing there).
5. Improve order success page trust model:
   - reduce reliance on raw query string as the sole truth for “success” display
   - at minimum, add clearer state messaging if data is unverified
6. Add missing `aria-label` to icon-only buttons in `MainLayout`.
7. Fix table-row click accessibility pattern in `AccountOrdersView` (keyboard/semantic consistency).
8. Fix dead quick actions in admin `HomeView` (`analytics`, `systemSettings` no-op buttons).

### Key Findings to Fix (from audit)

- `router/index.ts`
  - loading message garbled
  - router error message garbled
  - customer allowed into multiple admin routes (`roles` includes `CUSTOMER`)
  - legacy redirects overlap storefront paths (`/products`, `/blog`, `/promotions`)
- `HomeView.vue`
  - customer cards route to legacy admin paths
  - dead quick-action buttons
- `MainLayout.vue`
  - icon-only buttons missing `aria-label`
- `OrderSuccessView.vue`
  - trusts `route.query.orderNumber/amount/status` directly
- `AccountOrdersView.vue`
  - clickable `<tr>` pattern without keyboard parity

### Acceptance Criteria

- Customer login flow no longer creates admin-path confusion.
- Router loading/error notifications are readable.
- Admin header icon-only buttons have `aria-label`.
- No dead primary quick actions in admin home.
- `npm run build-only` passes.

### Notes / Risks

- Coordinate with Agent A if changing `/products` route behavior or redirect policy.
- Keep backward compatibility where possible; document any route behavior changes.

---

## Agent C (Admin UI Text Cleanup / Large Mojibake Hotspots)

### Objective

Clean high-impact admin pages with garbled text, starting from the biggest hotspot (`ProductView.vue`), then other admin pages with visible issues.

### File Ownership (Agent C only)

Primary:

- `frontend/src/views/ProductView.vue`

Secondary (if time permits after ProductView):

- `frontend/src/views/OrderView.vue`
- `frontend/src/views/EcPayConfigView.vue`
- `frontend/src/views/UserView.vue`
- `frontend/src/views/PointView.vue`

Do not edit files owned by Agent A or B.

### Tasks

1. Remove mojibake/garbled text in `ProductView.vue` user-visible labels/messages/status mappings.
2. Normalize product management terminology to your shared admin glossary:
   - 商品 / 商品管理 / 上架 / 下架 / 狀態 / 庫存 / 價格 / 分類
3. Verify status labels (`PUBLISHED`/`UNPUBLISHED`) render correctly in all views/tooltips/badges.
4. Clean notifications and validation messages.
5. If `ProductView.vue` is too corrupted in places, prefer targeted stable rewrite of affected sections rather than risky in-place patching.
6. Continue to other admin pages only after ProductView is stable.

### Key Findings to Fix (from audit)

- `ProductView.vue` currently has the highest mojibake count (hotspot)
- Garbled strings exist in:
  - metric cards
  - tooltips
  - validation messages
  - action notifications
  - status options and status label mapping

### Acceptance Criteria

- `ProductView.vue` visible UI text is readable and consistent in zh-TW.
- Status mapping is correct and non-garbled in list/cards/dialogs/tooltips.
- No `???`/mojibake in common product management actions.
- `npm run build-only` passes.

### Notes / Risks

- `ProductView.vue` is large and may contain prior encoding corruption.
- Avoid wide refactors unrelated to visible text/UX unless needed to restore stability.

---

## Merge Order and Conflict Prevention

### Merge Order

1. Agent B
2. Agent A
3. Agent C

### Conflict Hotspots

- `router/index.ts` vs storefront navigation behavior
- `HomeView.vue` customer routing behavior vs admin access decisions
- Any shared glossary/constants if newly introduced

### Conflict Avoidance Rules

- Agent A does not edit router logic.
- Agent B does not edit storefront page copy in A-owned files.
- Agent C does not edit shared layouts/router.

---

## Handoff Template (for each agent)

Use this in each agent PR or status report:

```md
## Scope
- Files changed:
- Out-of-scope items left:

## User-visible changes
- 

## Risk / Behavior changes
- 

## Verification
- [ ] npm run build-only
- [ ] Manual route checks
```

---

## Final Integration Checklist (after all 3 agents merge)

- [ ] `npm run build-only` passes on merged branch
- [ ] Storefront routes readable: `/`, `/products`, `/cart`, `/checkout`
- [ ] Customer login does not land in confusing admin flows
- [ ] Admin header buttons are accessible (`aria-label`)
- [ ] Product management page text is readable (no mojibake)
- [ ] Router global loading/error messages are readable

