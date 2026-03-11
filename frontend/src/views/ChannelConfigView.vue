<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import AppShell from '../components/layout/AppShell.vue'
import UiButton from '../components/ui/UiButton.vue'
import UiInput from '../components/ui/UiInput.vue'
import { api } from '../lib/api'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const loading = ref(false)
const saving = ref(false)
const error = ref<string | null>(null)
const success = ref<string | null>(null)

const form = reactive({
  channelType: '',
  baseUrl: '',
  callbackUrl: '',
  downloadUrl: '',
  appId: '',
  token: '',
})

async function fetchConfig() {
  loading.value = true
  error.value = null
  try {
    const res = await api.getChannelConfig()
    if (!res.success || !res.config) {
      error.value = '获取配置失败'
      return
    }
    const cfg = res.config as any
    form.channelType = cfg.channelType ?? ''
    form.baseUrl = cfg.baseUrl ?? ''
    form.callbackUrl = cfg.callbackUrl ?? ''
    form.downloadUrl = cfg.downloadUrl ?? ''
    form.appId = cfg.appId ?? ''
    form.token = cfg.token ?? ''
  } catch (e: any) {
    error.value = e?.message ?? String(e)
  } finally {
    loading.value = false
  }
}

async function saveConfig() {
  saving.value = true
  error.value = null
  success.value = null
  try {
    const payload = {
      channelType: form.channelType.trim(),
      baseUrl: form.baseUrl.trim(),
      callbackUrl: form.callbackUrl.trim(),
      downloadUrl: form.downloadUrl.trim(),
      appId: form.appId.trim(),
      token: form.token.trim(),
    }
    const res = await api.updateChannelConfig(payload)
    if (!res.success) {
      error.value = '保存失败'
      return
    }
    success.value = '配置已保存'
  } catch (e: any) {
    error.value = e?.message ?? String(e)
  } finally {
    saving.value = false
  }
}

function ensureAuthed() {
  if (!auth.token) {
    router.push('/login')
  }
}

onMounted(() => {
  ensureAuthed()
  if (auth.token) void fetchConfig()
})
</script>

<template>
  <AppShell>
    <div class="max-w-[900px] space-y-4">
      <div class="flex items-center justify-between gap-3">
        <div>
          <div class="text-xl font-semibold">微信接入渠道</div>
          <div class="mt-1 text-sm text-slate-400">配置 gewechat 通道信息</div>
        </div>
        <UiButton variant="primary" :loading="saving" @click="saveConfig">保存配置</UiButton>
      </div>

      <div v-if="error" class="rounded-xl p-3 text-sm ring-1 bg-amber-500/10 ring-amber-400/20 text-amber-200">
        {{ error }}
      </div>
      <div
        v-if="success"
        class="rounded-xl p-3 text-sm ring-1 bg-emerald-500/10 ring-emerald-400/20 text-emerald-200"
      >
        {{ success }}
      </div>

      <div class="rounded-2xl bg-white/5 ring-1 ring-white/10 p-4 space-y-3">
        <div>
          <label class="text-xs text-slate-400">Channel Type</label>
          <UiInput v-model="form.channelType" placeholder="gewechat" />
        </div>
        <div>
          <label class="text-xs text-slate-400">Base URL</label>
          <UiInput v-model="form.baseUrl" placeholder="http://x.x.x.x:2531/v2/api" />
        </div>
        <div>
          <label class="text-xs text-slate-400">Callback URL</label>
          <UiInput v-model="form.callbackUrl" placeholder="http://x.x.x.x:9919/v2/api/callback/collect" />
        </div>
        <div>
          <label class="text-xs text-slate-400">Download URL</label>
          <UiInput v-model="form.downloadUrl" placeholder="http://x.x.x.x:2532/download" />
        </div>
        <div>
          <label class="text-xs text-slate-400">AppId</label>
          <UiInput v-model="form.appId" placeholder="appId" />
        </div>
        <div>
          <label class="text-xs text-slate-400">Token</label>
          <UiInput v-model="form.token" placeholder="token" />
        </div>
      </div>
    </div>
  </AppShell>
</template>
