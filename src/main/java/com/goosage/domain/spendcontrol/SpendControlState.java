package com.goosage.domain.spendcontrol;

public record SpendControlState(
        int wrongReviews,
        int quizSubmits,
        int eventsCount,
        int wrongReviewDoneCount
) {

	public int justOpenCount() {
	    int value = eventsCount - quizSubmits - wrongReviewDoneCount;
	    return Math.max(value, 0);
	}

    // ===== spendcontrol semantic aliases =====

    // 충동 신호
    public int impulseSignalCount() {
        return wrongReviews;
    }

    // 소비 시도
    public int purchaseAttempts() {
        return quizSubmits;
    }

    // 취소/차단/제어 완료
    public int purchaseCancelDoneCount() {
        return wrongReviewDoneCount;
    }

    // 진입/열기 성격의 행동
    public int spendOpens() {
        return justOpenCount();
    }

    // 아직 별도 집계가 없으므로 임시 0
    public int itemViews() {
        return 0;
    }
}