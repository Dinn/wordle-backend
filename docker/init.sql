CREATE TABLE IF NOT EXISTS puzzle (
  id SERIAL PRIMARY KEY,
  answer VARCHAR(5) NOT NULL,
  created_at TIMESTAMP DEFAULT NOW()
);

INSERT INTO puzzle(answer) VALUES ('apple'), ('table'), ('chair');