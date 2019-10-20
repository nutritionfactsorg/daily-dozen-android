package org.nutritionfacts.dailydozen.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "weights")
public class Weight extends Model {
    @Column(name = "date_id")
    private Day day;

    @Column(name = "morning_weight")
    private float morningWeight;

    @Column(name = "evening_weight")
    private float eveningWeight;
}
