package com.ellalan.certifiedparent.interfaces;


public interface WeeklyQuizInterface {
    void LoadQuestion();
    void LoadAnswer(boolean result);
    void UpdateWeeklyQuizResult(boolean re);

    void ChallengeAParent();
}
