package org.nutritionfacts.dailydozen;

// This is an interface for representing something that has a Recommended Daily Allowance
public interface RDA {
    String getName();
    String getIdName();
    int getRecommendedAmount();
}
