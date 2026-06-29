-- 1. CLEAN SLATE: Drop tables if they exist (with CASCADE to handle foreign key dependencies)
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS role CASCADE;

-- 2. CREATE ROLE TABLE FIRST (So users can reference it)
CREATE TABLE role (
    role_id INT PRIMARY KEY, 
    role_name VARCHAR(50)
);

INSERT INTO role (role_id, role_name) VALUES
(0, 'users'),
(1, 'admin');

-- Optional alter from your script: adding a 'role' column to 'role' table
ALTER TABLE role ADD COLUMN role_code INTEGER; -- Renamed 'role' to 'role_code' to avoid syntax confusion
UPDATE role SET role_code = 0 WHERE role_code IS NULL;  
ALTER TABLE role ALTER COLUMN role_code SET NOT NULL;


-- 3. CREATE USERS TABLE
CREATE TABLE users (
    id SERIAL PRIMARY KEY, 
    name VARCHAR(50),
    phone VARCHAR(12)
);

-- 4. ALTER USERS TABLE TO ADD ALL REQUIRED COLUMNS BEFORE USE
ALTER TABLE users ADD COLUMN email VARCHAR(100) NOT NULL DEFAULT 'xyz@gmail.com';
ALTER TABLE users ADD COLUMN softdelete INT DEFAULT 0 CHECK (softdelete IN (0,1));
ALTER TABLE users ADD COLUMN role_id INT DEFAULT 0 CHECK (role_id IN (0,1));

-- Add foreign key constraint linking to the role table
ALTER TABLE users ADD CONSTRAINT fk_users_role_id FOREIGN KEY (role_id) REFERENCES role(role_id);


-- 5. DATA INSERTION & MANIPULATION
INSERT INTO users (name, phone) VALUES
('user1', '9908585857'),
('user2', '7890234567'),
('user3', '1234567890'),
('user4', '8822445566'),
('user5', '8844559966');

UPDATE users SET name = ' Adarsh' WHERE id = 1;

-- Deletions
DELETE FROM users WHERE id = 2 AND name = 'user2' AND phone LIKE '%567%';
-- This second delete won't affect anything since id=2 is already deleted above, but it runs fine
DELETE FROM users WHERE id = 2; 

-- Inserting a new row explicitly passing ID = 2
INSERT INTO users (id, name, email, phone, softdelete, role_id)
VALUES (2, 'baibhav', 'baibhav@gmail.com', '9123456780', 0, 0);


-- 6. CONDITIONAL UPDATES & UNIQUE CONSTRAINTS
UPDATE users SET email = 'heshma28@gmail.com' WHERE name LIKE 'us%' AND (id >= 2 OR phone LIKE '1234%');
UPDATE users SET email = 'newuser@gmail.com' WHERE id > 4;

-- Enforce uniqueness on email now that data is populated
ALTER TABLE users ADD CONSTRAINT unique_user_email UNIQUE(email);

-- Updating names and emails (Fixed: changed 'names' to 'name')
UPDATE users SET name = 'adarsh', email = 'adarsh@gmail.com' WHERE id = 1;
UPDATE users SET name = 'archit', email = 'archit@gmail.com' WHERE id = 3;
UPDATE users SET name = 'meher',  email = 'meher@gmail.com'  WHERE id = 4;
UPDATE users SET name = 'dubey',  email = 'dubey@gmail.com'  WHERE id = 5;

UPDATE users SET phone = '9876543210' WHERE id = 4;
UPDATE users SET phone = '8765432109' WHERE id = 5;
UPDATE users SET name = 'user 1' WHERE id = 1;

UPDATE users SET email = 'heshma@gmail.com' WHERE id = 3 AND email IS NOT NULL;


-- 7. SOFT DELETE VALIDATIONS
-- The statement below is commented out because it will intentionally fail the CHECK constraint
-- UPDATE users SET softdelete = 2 WHERE id = 2; 

UPDATE users SET softdelete = 1 WHERE id = 1;
UPDATE users SET softdelete = 1 WHERE id = 4;


-- 8. SELECTS AND AGGREGATIONS
SELECT * FROM users;
SELECT email, COUNT(email) FROM users GROUP BY email;
SELECT email, COUNT(email) FROM users GROUP BY email HAVING COUNT(email) > 1;

-- Ordering Data
SELECT * FROM users ORDER BY id ASC;
SELECT * FROM users ORDER BY id DESC;
SELECT * FROM users ORDER BY softdelete DESC, id DESC;
SELECT * FROM users ORDER BY id DESC, softdelete DESC;


-- 9. TRANSACTIONS
BEGIN;
  UPDATE users SET role_id = 1 WHERE id > 3;
  SAVEPOINT sp1;
  UPDATE users SET role_id = 1 WHERE id > 1;
  ROLLBACK TO sp1;
COMMIT;


-- 10. PREPARED STATEMENTS
PREPARE statement1 (INT, VARCHAR) AS
SELECT * FROM users WHERE id = $1 OR name = $2;

EXECUTE statement1(1, 'heshma');


-- 11. FUNCTIONS
CREATE OR REPLACE FUNCTION fun1 (id INT)
RETURNS TEXT AS $$
BEGIN
    IF id = 0 THEN 
        RETURN 'zero';
    ELSE 
        RETURN 'one';
    END IF;
END;
$$ LANGUAGE plpgsql;

-- Calling the function
SELECT fun1(0);