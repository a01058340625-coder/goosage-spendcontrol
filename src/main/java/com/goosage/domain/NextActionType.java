package com.goosage.domain;

public enum NextActionType {

    // 충동 신호 1건 처리
    PROCESS_IMPULSE_SIGNAL,

    // 상태 점검 / 소비 제어 체크
    SPEND_CONTROL_CHECK,

    // 제어 행동 1회 실행
    DO_CONTROL_ACTION,

    // 최소 접촉 유지
    MINIMUM_CONTACT,

    // 오늘 제어 완료
    TODAY_SAFE
}