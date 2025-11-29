INSERT INTO palja_user.p_user (login_id, password, role, status)
VALUES ('master', 'master', 'MASTER', 'ACTIVE')
ON CONFLICT (login_id) DO NOTHING;