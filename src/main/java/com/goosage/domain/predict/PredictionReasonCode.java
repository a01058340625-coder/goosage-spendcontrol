package com.goosage.domain.predict;

public enum PredictionReasonCode {

    DATA_POOR,
    LOW_ACTIVITY_3D,

    // legacy naming (의미는 "충동 미처리")
    REVIEW_WRONG_PENDING,

    // legacy naming (의미는 "제어 진행 / 안정")
    RECOVERY_PROGRESS,
    RECOVERY_SAFE,

    // 행동 품질 문제
    LOW_QUALITY_OPEN,

    GOOD_PROGRESS,
    HABIT_STABLE,
    MINIMUM_ACTION,
    HABIT_COLLAPSE,

    TODAY_DONE,

    WRONG_HEAVY,
    STABLE_PROGRESS,

    // SpendControl 전용
    URGE_HIGH,
    RELAPSE_RISK
}