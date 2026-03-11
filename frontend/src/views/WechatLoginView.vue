<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import AppShell from '../components/layout/AppShell.vue'
import UiButton from '../components/ui/UiButton.vue'
import { api } from '../lib/api'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const loading = ref(false)
const message = ref<string | null>(null)

const appId = ref('')
const uuid = ref('')
const qrImage = ref<string | null>(null)

const status = ref<number | null>(null)
const expiredTime = ref<number | null>(null)
const nickName = ref<string | null>(null)
const loggedIn = ref(false)

let timer: number | null = null

const statusText = computed(() => {
  if (loggedIn.value) return '已登录'
  if (status.value == null) return '-'
  // gewechat 返回的 status 约定：2=登录成功，其余为扫码中/等待
  if (status.value === 2) return '登录成功'
  return `等待扫码（status=${status.value}）`
})

function stopPolling() {
  if (timer != null) {
    window.clearInterval(timer)
    timer = null
  }
}

async function refreshQr() {
  stopPolling()
  loading.value = true
  message.value = null
  status.value = null
  expiredTime.value = null
  nickName.value = null
  loggedIn.value = false
  qrImage.value = null

  try {
    const res = await api.wechatQr()
    if (!res.success) {
      message.value = res.message ?? '获取二维码失败'
      return
    }
    appId.value = res.appId ?? ''
    uuid.value = res.uuid ?? ''
    qrImage.value = res.qrImage ?? null
    startPolling()
  } catch (e: any) {
    message.value = e?.message ?? String(e)
  } finally {
    loading.value = false
  }
}

async function pollOnce() {
  if (!appId.value || !uuid.value) return
  try {
    const res = await api.wechatQrStatus(appId.value, uuid.value)
    if (!res.success) {
      message.value = res.message ?? '查询状态失败'
      return
    }
    status.value = res.status ?? null
    expiredTime.value = res.expiredTime ?? null
    nickName.value = res.nickName ?? null
    loggedIn.value = !!res.loggedIn

    const exp = Number(res.expiredTime ?? 0)
    if (!loggedIn.value && Number.isFinite(exp) && exp > 0 && exp <= 5) {
      message.value = '二维码即将过期，正在自动刷新…'
      await refreshQr()
    }
    if (loggedIn.value) {
      message.value = `登录成功：${nickName.value ?? ''}`.trim()
      stopPolling()
    }
  } catch (e: any) {
    message.value = e?.message ?? String(e)
  }
}

function startPolling() {
  stopPolling()
  timer = window.setInterval(() => void pollOnce(), 2000)
  void pollOnce()
}

function ensureAuthed() {
  if (!auth.token) {
    router.push('/login')
  }
}

onMounted(() => {
  ensureAuthed()
  if (auth.token) void refreshQr()
})

watch(
  () => auth.token,
  () => {
    if (!auth.token) {
      stopPolling()
    }
  },
)

onBeforeUnmount(() => stopPolling())
</script>

<template>
  <AppShell>
    <div class="max-w-[1100px]">
      <div class="flex items-start justify-between gap-4">
        <div>
          <div class="text-xl font-semibold">扫码登录</div>
          <div class="mt-1 text-sm text-slate-400">
            扫码成功后，后端会保存 <span class="text-slate-200">appId</span> 到数据库配置中
          </div>
        </div>
        <div class="flex items-center gap-2">
          <UiButton variant="ghost" :loading="loading" @click="refreshQr">刷新二维码</UiButton>
        </div>
      </div>

      <div class="mt-5 grid grid-cols-1 xl:grid-cols-[420px_1fr] gap-4">
        <div class="rounded-2xl bg-white/5 ring-1 ring-white/10 p-4">
          <div class="flex items-center justify-between">
            <div class="text-sm text-slate-300">二维码</div>
            <span
              class="text-[11px] px-2 py-1 rounded-full ring-1"
              :class="
                loggedIn
                  ? 'bg-emerald-500/10 ring-emerald-400/20 text-emerald-200'
                  : 'bg-white/5 ring-white/10 text-slate-300'
              "
            >
              {{ loggedIn ? 'LOGGED IN' : 'WAITING' }}
            </span>
          </div>
          <div class="mt-3 rounded-2xl bg-slate-950/40 ring-1 ring-white/10 p-4 grid place-items-center">
            <img v-if="qrImage" :src="qrImage" alt="qr" class="h-[300px] w-[300px] rounded-2xl bg-white" />
            <div v-else class="text-sm text-slate-500">暂无二维码</div>
          </div>
        </div>

        <div class="rounded-2xl bg-white/5 ring-1 ring-white/10 p-4">
          <div class="text-sm text-slate-300">连接信息</div>

          <div class="mt-4 grid grid-cols-1 md:grid-cols-2 gap-3">
            <div class="rounded-xl bg-slate-950/40 ring-1 ring-white/10 p-3">
              <div class="text-xs text-slate-400">AppId</div>
              <div class="mt-1 text-sm break-all">{{ appId || '-' }}</div>
            </div>
            <div class="rounded-xl bg-slate-950/40 ring-1 ring-white/10 p-3">
              <div class="text-xs text-slate-400">UUID</div>
              <div class="mt-1 text-sm break-all">{{ uuid || '-' }}</div>
            </div>
            <div class="rounded-xl bg-slate-950/40 ring-1 ring-white/10 p-3">
              <div class="text-xs text-slate-400">登录状态</div>
              <div class="mt-1 text-sm">{{ statusText }}</div>
            </div>
            <div class="rounded-xl bg-slate-950/40 ring-1 ring-white/10 p-3">
              <div class="text-xs text-slate-400">剩余秒数</div>
              <div class="mt-1 text-sm">{{ expiredTime ?? '-' }}</div>
            </div>
            <div class="rounded-xl bg-slate-950/40 ring-1 ring-white/10 p-3 md:col-span-2">
              <div class="text-xs text-slate-400">昵称</div>
              <div class="mt-1 text-sm">{{ nickName ?? '-' }}</div>
            </div>
          </div>

          <div
            v-if="message"
            class="mt-4 rounded-xl p-3 text-sm ring-1"
            :class="loggedIn ? 'bg-emerald-500/10 ring-emerald-400/20 text-emerald-200' : 'bg-amber-500/10 ring-amber-400/20 text-amber-200'"
          >
            {{ message }}
          </div>

          <div class="mt-4 text-xs text-slate-500">
            如果提示 “Network is unreachable”，请检查“微信接入渠道”页面的 `baseUrl` 是否能访问到 gewechat 服务。
          </div>
        </div>
      </div>
    </div>
  </AppShell>
</template>
