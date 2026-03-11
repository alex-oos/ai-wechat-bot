(() => {
  const $ = (id) => document.getElementById(id);

  const state = {
    token: localStorage.getItem("admin_token") || "",
    appId: "",
    uuid: "",
    pollTimer: null,
  };

  function setMsg(el, text) {
    el.textContent = text || "";
  }

  function setView(isAuthed) {
    $("loginView").classList.toggle("hidden", isAuthed);
    $("qrView").classList.toggle("hidden", !isAuthed);
  }

  async function api(path, options = {}) {
    const headers = Object.assign({ "Content-Type": "application/json" }, options.headers || {});
    const disableAuth = !!options.disableAuth;
    if (!disableAuth && state.token) {
      headers["Authorization"] = `Bearer ${state.token}`;
    }
    const res = await fetch(path, Object.assign({}, options, { headers }));
    if (res.status === 401) {
      throw new Error("未登录或登录已过期，请重新登录");
    }
    return res.json();
  }

  async function doLogin() {
    setMsg($("loginMsg"), "");
    const username = $("username").value.trim();
    const password = $("password").value;
    if (!username || !password) {
      setMsg($("loginMsg"), "请输入账号和密码");
      return;
    }

    const data = await api("/api/auth/login", {
      method: "POST",
      body: JSON.stringify({ username, password }),
      headers: {},
      disableAuth: true,
    });

    if (!data.success) {
      setMsg($("loginMsg"), data.message || "登录失败");
      return;
    }

    state.token = data.token;
    localStorage.setItem("admin_token", state.token);
    setView(true);
    await refreshQr();
    startPolling();
  }

  function doLogout() {
    stopPolling();
    state.token = "";
    localStorage.removeItem("admin_token");
    setView(false);
    setMsg($("loginMsg"), "");
    setMsg($("qrMsg"), "");
  }

  async function refreshQr() {
    stopPolling();
    setMsg($("qrMsg"), "");
    $("status").textContent = "-";
    $("expired").textContent = "-";
    $("nick").textContent = "-";

    const data = await api("/api/wechat/qr", { method: "GET" });
    if (!data.success) {
      setMsg($("qrMsg"), data.message || "获取二维码失败");
      return;
    }

    state.appId = data.appId;
    state.uuid = data.uuid;
    $("appId").textContent = state.appId || "-";
    $("uuid").textContent = state.uuid || "-";
    $("qrImg").src = data.qrImage;

    startPolling();
  }

  async function pollOnce() {
    if (!state.appId || !state.uuid) return;

    const data = await api("/api/wechat/qr/status", {
      method: "POST",
      body: JSON.stringify({ appId: state.appId, uuid: state.uuid }),
    });

    if (!data.success) {
      setMsg($("qrMsg"), data.message || "查询状态失败");
      return;
    }

    $("status").textContent = String(data.status ?? "-");
    $("expired").textContent = String(data.expiredTime ?? "-");
    $("nick").textContent = data.nickName || "-";

    const exp = Number(data.expiredTime ?? 0);
    if (data.loggedIn) {
      setMsg($("qrMsg"), `登录成功：${data.nickName || "unknown"}`);
      stopPolling();
      return;
    }

    if (Number.isFinite(exp) && exp > 0 && exp <= 5) {
      setMsg($("qrMsg"), "二维码即将过期，正在自动刷新…");
      await refreshQr();
    }
  }

  function startPolling() {
    stopPolling();
    state.pollTimer = setInterval(() => {
      pollOnce().catch((e) => setMsg($("qrMsg"), e.message || String(e)));
    }, 2000);
    pollOnce().catch((e) => setMsg($("qrMsg"), e.message || String(e)));
  }

  function stopPolling() {
    if (state.pollTimer) {
      clearInterval(state.pollTimer);
      state.pollTimer = null;
    }
  }

  $("loginBtn").addEventListener("click", () => doLogin().catch((e) => setMsg($("loginMsg"), e.message || String(e))));
  $("refreshQrBtn").addEventListener("click", () => refreshQr().catch((e) => setMsg($("qrMsg"), e.message || String(e))));
  $("logoutBtn").addEventListener("click", doLogout);

  // init
  if (state.token) {
    setView(true);
    refreshQr().catch((e) => {
      setMsg($("qrMsg"), e.message || String(e));
      doLogout();
    });
  } else {
    setView(false);
  }
})();

