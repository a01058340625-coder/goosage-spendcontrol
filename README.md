# GooSage SpendControl

Decision engine for managing impulsive spending behavior.

---

## 1. Overview

GooSage SpendControl applies the GooSage engine to spending behavior.

It detects impulse signals, evaluates risk, and recommends control actions.

---

## 2. Domain Focus

This domain models consumer impulse behavior.

### Event Types
- SPEND_OPEN
- ITEM_VIEW
- PURCHASE_ATTEMPT
- PURCHASE_CANCEL_DONE
- IMPULSE_SIGNAL

---

## 3. Core Flow

Event → Spend State → Risk Evaluation → Control Action

---

## 4. Prediction Goals

- detect impulsive behavior patterns
- identify risky purchase attempts
- evaluate control effectiveness
- guide intervention timing

---

## 5. Next Actions

- PROCESS_IMPULSE_SIGNAL
- SPEND_CONTROL_CHECK
- DO_CONTROL_ACTION
- MINIMUM_CONTACT
- TODAY_SAFE

---

## 6. Why this matters

Spending behavior is highly reactive and emotional.

This system introduces:
- structured control logic
- measurable decision flow
- consistent intervention strategy

---

## 7. Relation to GooSage

- goosage-api → core engine
- goosage-spendcontrol → domain extension

Demonstrates how the same engine applies across domains.

---

## 8. Long-Term Goal

- impulse control modeling
- financial behavior stabilization
- cross-domain behavioral decision system