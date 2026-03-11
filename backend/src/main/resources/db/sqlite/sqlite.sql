CREATE TABLE IF NOT EXISTS friends (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  user_name TEXT NOT NULL,
  nick_name TEXT,
  py_initial TEXT,
  quan_pin TEXT,
  sex TEXT,
  remark TEXT,
  remark_py_initial TEXT,
  remark_quan_pin TEXT,
  signature TEXT,
  alias TEXT,
  sns_bg_img TEXT,
  country TEXT,
  big_head_img_url TEXT,
  small_head_img_url TEXT,
  description TEXT,
  card_img_url TEXT,
  label_list TEXT,
  province TEXT,
  city TEXT,
  phone_num_list TEXT,
  create_time TEXT,
  deleted INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS chat_rooms (
  chat_room_id TEXT PRIMARY KEY,
  nick_name TEXT,
  py_initial TEXT,
  quan_pin TEXT,
  sex TEXT,
  remark TEXT,
  remark_py_initial TEXT,
  remark_quan_pin TEXT,
  signature TEXT,
  create_time TEXT,
  deleted INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS system_config (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  app_id TEXT,
  token TEXT,
  create_time TEXT,
  login_time TEXT,
  deleted INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS timed_tasks (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  task_name TEXT,
  interval_time INTEGER,
  cron_expression TEXT,
  last_execute_time TEXT,
  next_execute_time TEXT,
  status TEXT,
  name TEXT,
  create_time TEXT,
  deleted INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS ai_system_prompt (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  role_name TEXT,
  role_type TEXT,
  content TEXT,
  create_time TEXT,
  update_time TEXT,
  deleted INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS admin_users (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  username TEXT NOT NULL UNIQUE,
  password_hash TEXT NOT NULL,
  display_name TEXT,
  status TEXT DEFAULT 'ACTIVE',
  create_time TEXT,
  update_time TEXT,
  deleted INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS bot_config (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  app_id TEXT,
  token TEXT,
  channel_type TEXT,
  dashscope_api_key TEXT,
  debug INTEGER,
  base_url TEXT,
  callback_url TEXT,
  download_url TEXT,
  group_chat_prefix TEXT,
  group_name_white_list TEXT,
  image_recognition INTEGER,
  model TEXT,
  single_chat_prefix TEXT,
  single_chat_reply_prefix TEXT,
  speech_recognition INTEGER,
  text_to_voice TEXT,
  voice_reply_voice INTEGER,
  voice_to_text TEXT,
  ai_type TEXT,
  text_to_voice_model TEXT,
  tts_voice_id TEXT,
  system_prompt TEXT,
  image_create_prefix TEXT,
  localhost_ip TEXT,
  create_time TEXT,
  update_time TEXT
);

CREATE TABLE IF NOT EXISTS wechat_channel_config (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  channel_type TEXT,
  base_url TEXT,
  callback_url TEXT,
  download_url TEXT,
  app_id TEXT,
  token TEXT,
  create_time TEXT,
  update_time TEXT
);

CREATE TABLE IF NOT EXISTS ai_config (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  ai_type TEXT,
  model TEXT,
  api_base_url TEXT,
  api_key TEXT,
  active_provider_id INTEGER,
  create_time TEXT,
  update_time TEXT
);

CREATE TABLE IF NOT EXISTS ai_providers (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT,
  ai_type TEXT,
  model TEXT,
  api_base_url TEXT,
  api_key TEXT,
  enabled INTEGER DEFAULT 1,
  create_time TEXT,
  update_time TEXT
);
