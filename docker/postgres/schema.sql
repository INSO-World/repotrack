CREATE TABLE repositories (
                              id SERIAL PRIMARY KEY,
                              name VARCHAR(255) NOT NULL UNIQUE,
                              url VARCHAR(512) NOT NULL,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE contributors (
                              id SERIAL PRIMARY KEY,
                              name VARCHAR(255) NOT NULL,
                              email VARCHAR(255) NOT NULL UNIQUE 
);

CREATE TABLE commits (
                         hash CHAR(40) PRIMARY KEY,  -- Git commit SHA-1
                         repo_id INTEGER REFERENCES repositories(id) NOT NULL,
                         author_id INTEGER REFERENCES contributors(id) NOT NULL,
                         committer_id INTEGER REFERENCES contributors(id) NOT NULL,
                         message TEXT,
                         commit_date TIMESTAMP NOT NULL,
                         is_merge BOOLEAN DEFAULT FALSE
);

CREATE TABLE file_changes (
                              id SERIAL PRIMARY KEY,
                              commit_hash CHAR(40) REFERENCES commits(hash) NOT NULL,
                              file_path TEXT NOT NULL,
                              change_type VARCHAR(10) CHECK (change_type IN ('ADD', 'MODIFY', 'DELETE')),
                              lines_added INTEGER DEFAULT 0,
                              lines_deleted INTEGER DEFAULT 0
);

CREATE TABLE branches (
                          name VARCHAR(255) NOT NULL,
                          repo_id INTEGER REFERENCES repositories(id) NOT NULL,
                          latest_commit CHAR(40) REFERENCES commits(hash),
                          PRIMARY KEY (repo_id, name)
);

CREATE INDEX idx_commits_date ON commits(commit_date);
CREATE INDEX idx_file_path ON file_changes(file_path);