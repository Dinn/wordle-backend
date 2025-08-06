CREATE TABLE IF NOT EXISTS player_stats (
  user_id     UUID PRIMARY KEY,
  games       INT  DEFAULT 0,
  wins        INT  DEFAULT 0,
  cur_streak  INT  DEFAULT 0,
  max_streak  INT  DEFAULT 0,
  one         INT  DEFAULT 0,
  two         INT  DEFAULT 0,
  three       INT  DEFAULT 0,
  four        INT  DEFAULT 0,
  five        INT  DEFAULT 0,
  six         INT  DEFAULT 0
);
