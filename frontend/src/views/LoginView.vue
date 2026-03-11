<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../lib/api'
import { useAuthStore } from '../stores/auth'
import AstrIcon from '../components/icons/AstrIcon.vue'
import UiButton from '../components/ui/UiButton.vue'
import UiInput from '../components/ui/UiInput.vue'

const router = useRouter()
const auth = useAuthStore()

const username = ref('admin')
const password = ref('123456')
const loading = ref(false)
const error = ref<string | null>(null)

async function submit() {
  error.value = null
  loading.value = true
  try {
    const res = await api.login(username.value.trim(), password.value)
    if (!res.success || !res.token) {
      error.value = res.message ?? '登录失败'
      return
    }
    auth.setToken(res.token)
    await router.push('/wechat-login')
  } catch (e: any) {
    error.value = e?.message ?? String(e)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="h-full w-full bg-slate-950">
    <div class="absolute inset-0 overflow-hidden">
      <div
        class="pointer-events-none absolute -top-24 -left-24 h-[420px] w-[420px] rounded-full bg-sky-500/10 blur-3xl"
      />
      <div
        class="pointer-events-none absolute -bottom-24 -right-24 h-[420px] w-[420px] rounded-full bg-indigo-500/10 blur-3xl"
      />
    </div>

    <div class="relative h-full w-full grid place-items-center p-6">
      <div class="w-full max-w-[440px] rounded-2xl bg-white/5 ring-1 ring-white/10 p-6 backdrop-blur">
        <div class="flex items-center gap-3">
          <AstrIcon />
          <div class="min-w-0">
            <div class="text-lg font-semibold leading-5">ai-wechat-bot</div>
            <div class="mt-0.5 text-xs text-slate-400">Sign in to Admin Console</div>
          </div>
        </div>

        <div class="mt-6 space-y-3">
          <div>
            <label class="text-xs text-slate-400">账号</label>
            <UiInput v-model="username" autocomplete="username" placeholder="admin" />
          </div>
          <div>
            <label class="text-xs text-slate-400">密码</label>
            <UiInput v-model="password" type="password" autocomplete="current-password" placeholder="123456" />
          </div>

          <div v-if="error" class="text-sm text-amber-200 bg-amber-500/10 ring-1 ring-amber-400/20 rounded-xl p-3">
            {{ error }}
          </div>

          <UiButton block variant="primary" :loading="loading" @click="submit">登录</UiButton>
        </div>

        <div class="mt-5 text-xs text-slate-500">
          默认账号密码会写入数据库用户表，可通过环境变量 `ADMIN_AUTH_USERNAME/PASSWORD` 初始化。
        </div>
      </div>
    </div>
  </div>
</template>
