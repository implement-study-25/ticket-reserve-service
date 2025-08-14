DELETE FROM role_privileges;
DELETE FROM user_roles;
DELETE FROM privileges;
DELETE FROM roles;
DELETE FROM users;

-- users
INSERT INTO users (user_id, email, password_hash, name, created_at, updated_at)
VALUES
  (100, 'admin@test.com', '$2a$10$abcdefghijklmnopqrstuvABCDEFGHijklmnOPQRSTUV12', 'Admin', NOW(), NOW()),
  (200, 'user@test.com', '$2a$10$abcdefghijklmnopqrstuvABCDEFGHijklmnOPQRSTUV13', 'User', NOW(), NOW());

-- roles
INSERT INTO roles (role_id, name) VALUES (1, 'ADMIN'), (2, 'USER');

-- privileges
INSERT INTO privileges (privilege_id, name) VALUES
  (1, 'EVENT_CREATE'),
  (2, 'EVENT_UPDATE'),
  (3, 'EVENT_CHANGE_STATUS'),
  (4, 'EVENT_SEAT_RESERVE'),
  (5, 'EVENT_SEAT_CANCEL');

-- user_roles
INSERT INTO user_roles (user_role_id, user_id, role_id) VALUES
  (1, 100, 1), -- admin → ADMIN
  (2, 200, 2); -- user → USER

-- role_privileges
INSERT INTO role_privileges (role_privilege_id, role_id, privilege_id) VALUES
  (1, 1, 1), -- ADMIN: EVENT_CREATE
  (2, 1, 2), -- ADMIN: EVENT_UPDATE
  (3, 1, 3), -- ADMIN: EVENT_CHANGE_STATUS
  (4, 2, 4), -- USER: EVENT_SEAT_RESERVE
  (5, 2, 5); -- USER: EVENT_SEAT_CANCEL

-- 추가 사용자 (ADMIN, USER 각 1명씩 더 추가)
-- users
INSERT INTO users (user_id, email, password_hash, name, created_at, updated_at)
VALUES
  (300, 'admin2@test.com', '$2a$10$abcdefghijklmnopqrstuvABCDEFGHijklmnOPQRSTUV14', 'Admin2', NOW(), NOW()),
  (400, 'user2@test.com', '$2a$10$abcdefghijklmnopqrstuvABCDEFGHijklmnOPQRSTUV15', 'User2', NOW(), NOW());

-- user_roles (추가 사용자 매핑)
INSERT INTO user_roles (user_role_id, user_id, role_id) VALUES
  (3, 300, 1), -- admin2 → ADMIN
  (4, 400, 2); -- user2 → USER


