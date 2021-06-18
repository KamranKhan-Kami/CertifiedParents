package com.ellalan.certifiedparent.interfaces;



public interface LoadStatementInterface {
    void LoadNextChildPsychologyStatement();
    void LoadNextParentPsychologyStatement();
    void LoadNextParentingTipsStatement();
    void LoadNextKnowFactsStatement();

    void LoadPreviousChildPsychologyStatement();
    void LoadPreviousParentPsychologyStatement();
    void LoadPreviousParentingTipsStatement();
    void LoadPreviousKnowFactsStatement();

    void ChangeHomeFragment();
}
