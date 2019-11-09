CREATE TABLE IF NOT EXISTS `tweaks` (
    Id INT PRIMARY KEY NOT NULL,
    id_name TEXT NOT NULL,
    name TEXT NOT NULL,
    recommended_amount INT NOT NULL
);

CREATE TABLE IF NOT EXISTS `weights` (
    Id INT PRIMARY KEY NOT NULL,
    date_id INTEGER NOT NULL,
    morning_weight REAL,
    evening_weight REAL,
    FOREIGN KEY(date_id) REFERENCES dates(id)
);
