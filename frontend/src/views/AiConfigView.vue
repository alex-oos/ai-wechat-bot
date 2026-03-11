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
  aiType: '',
  model: '',
  apiBaseUrl: '',
  apiKey: '',
  activeProviderId: null as number | null,
})

const providers = ref<any[]>([])
const selectedProviderId = ref<number | null>(null)
const providerForm = reactive({
  name: '',
  aiType: '',
  model: '',
  apiBaseUrl: '',
  apiKey: '',
  enabled: true,
})

async function fetchConfig() {
  loading.value = true
  error.value = null
  try {
    const res = await api.getAiConfig()
    if (!res.success || !res.config) {
      error.value = '获取配置失败'
      return
    }
    const cfg = res.config as any
    providers.value = Array.isArray(res.providers) ? res.providers : []
    form.aiType = cfg.aiType ?? ''
    form.model = cfg.model ?? ''
    form.apiBaseUrl = cfg.apiBaseUrl ?? ''
    form.apiKey = cfg.apiKey ?? ''
    form.activeProviderId = cfg.activeProviderId ?? null
    if (selectedProviderId.value == null && providers.value.length > 0) {
      selectedProviderId.value = form.activeProviderId ?? providers.value[0].id
      syncSelectedProvider()
    }
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
      aiType: form.aiType.trim(),
      model: form.model.trim(),
      apiBaseUrl: form.apiBaseUrl.trim(),
      apiKey: form.apiKey.trim(),
      activeProviderId: form.activeProviderId,
    }
    const res = await api.updateAiConfig(payload)
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

function syncSelectedProvider() {
  const current = providers.value.find((p) => p.id === selectedProviderId.value)
  if (!current) return
  providerForm.name = current.name ?? ''
  providerForm.aiType = current.aiType ?? ''
  providerForm.model = current.model ?? ''
  providerForm.apiBaseUrl = current.apiBaseUrl ?? ''
  providerForm.apiKey = current.apiKey ?? ''
  providerForm.enabled = current.enabled !== 0
}

async function saveProvider() {
  if (!selectedProviderId.value) return
  saving.value = true
  error.value = null
  success.value = null
  try {
    const payload = {
      name: providerForm.name.trim(),
      aiType: providerForm.aiType.trim(),
      model: providerForm.model.trim(),
      apiBaseUrl: providerForm.apiBaseUrl.trim(),
      apiKey: providerForm.apiKey.trim(),
      enabled: providerForm.enabled ? 1 : 0,
    }
    const res = await api.updateAiProvider(selectedProviderId.value, payload)
    if (!res.success) {
      error.value = '保存提供方失败'
      return
    }
    await fetchConfig()
    success.value = '提供方已保存'
  } catch (e: any) {
    error.value = e?.message ?? String(e)
  } finally {
    saving.value = false
  }
}

async function createProvider() {
  saving.value = true
  error.value = null
  success.value = null
  try {
    const payload = {
      name: providerForm.name.trim() || 'provider',
      aiType: providerForm.aiType.trim(),
      model: providerForm.model.trim(),
      apiBaseUrl: providerForm.apiBaseUrl.trim(),
      apiKey: providerForm.apiKey.trim(),
      enabled: providerForm.enabled ? 1 : 0,
    }
    const res = await api.createAiProvider(payload)
    if (!res.success) {
      error.value = '创建提供方失败'
      return
    }
    await fetchConfig()
    success.value = '提供方已创建'
  } catch (e: any) {
    error.value = e?.message ?? String(e)
  } finally {
    saving.value = false
  }
}

async function deleteProvider() {
  if (!selectedProviderId.value) return
  saving.value = true
  error.value = null
  success.value = null
  try {
    const res = await api.deleteAiProvider(selectedProviderId.value)
    if (!res.success) {
      error.value = '删除提供方失败'
      return
    }
    selectedProviderId.value = null
    await fetchConfig()
    success.value = '提供方已删除'
  } catch (e: any) {
    error.value = e?.message ?? String(e)
  } finally {
    saving.value = false
  }
}

async function setActiveProvider() {
  if (!selectedProviderId.value) return
  saving.value = true
  error.value = null
  success.value = null
  try {
    const res = await api.setActiveAiProvider(selectedProviderId.value)
    if (!res.success) {
      error.value = '切换失败'
      return
    }
    form.activeProviderId = selectedProviderId.value
    success.value = '已切换当前提供方'
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
          <div class="text-xl font-semibold">AI 配置</div>
          <div class="mt-1 text-sm text-slate-400">模型与接口信息</div>
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

      <div class="grid grid-cols-1 lg:grid-cols-[320px_1fr] gap-4">
        <div class="rounded-2xl bg-white/5 ring-1 ring-white/10 p-4 space-y-3">
          <div class="text-sm text-slate-300">提供方列表</div>
          <select
            v-model.number="selectedProviderId"
            class="w-full rounded-xl bg-slate-950/40 ring-1 ring-white/10 px-3 py-2 text-sm outline-none"
            @change="syncSelectedProvider"
          >
            <option v-for="p in providers" :key="p.id" :value="p.id">
              {{ p.name || p.aiType || `Provider-${p.id}` }}
            </option>
          </select>
          <div class="flex items-center gap-2">
            <UiButton variant="ghost" :loading="saving" @click="setActiveProvider">设为当前</UiButton>
            <UiButton variant="ghost" :loading="saving" @click="deleteProvider">删除</UiButton>
          </div>
          <div class="text-xs text-slate-500">当前生效：{{ form.activeProviderId ?? '-' }}</div>
        </div>

        <div class="rounded-2xl bg-white/5 ring-1 ring-white/10 p-4 space-y-3">
          <div class="text-sm text-slate-300">提供方配置</div>
          <div>
            <label class="text-xs text-slate-400">名称</label>
            <UiInput v-model="providerForm.name" placeholder="qwen / zhipu / openai / gemini" />
          </div>
          <div>
            <label class="text-xs text-slate-400">AI 类型</label>
            <UiInput v-model="providerForm.aiType" placeholder="qwen / zhipu / openai / gemini" />
          </div>
          <div>
            <label class="text-xs text-slate-400">模型</label>
            <UiInput v-model="providerForm.model" placeholder="qwen-max / glm-4 / gpt-4o-mini" />
          </div>
          <div>
            <label class="text-xs text-slate-400">API Base URL</label>
            <UiInput v-model="providerForm.apiBaseUrl" placeholder="https://api.xxx.com/v1" />
          </div>
          <div>
            <label class="text-xs text-slate-400">API Key</label>
            <UiInput v-model="providerForm.apiKey" placeholder="sk-xxx" />
          </div>
          <label class="flex items-center gap-2 text-sm text-slate-300">
            <input v-model="providerForm.enabled" type="checkbox" class="h-4 w-4 rounded border-slate-600 bg-slate-900" />
            <span>启用</span>
          </label>
          <div class="flex items-center gap-2">
            <UiButton variant="primary" :loading="saving" @click="saveProvider">保存提供方</UiButton>
            <UiButton variant="ghost" :loading="saving" @click="createProvider">新增提供方</UiButton>
          </div>
        </div>
      </div>

      <div class="rounded-2xl bg-white/5 ring-1 ring-white/10 p-4 space-y-3">
        <div class="text-sm text-slate-300">默认配置（兼容旧配置）</div>
        <div>
          <label class="text-xs text-slate-400">AI 类型</label>
          <UiInput v-model="form.aiType" placeholder="qwen / zhipu / openai / gemini" />
        </div>
        <div>
          <label class="text-xs text-slate-400">模型</label>
          <UiInput v-model="form.model" placeholder="qwen-max / glm-4 / gpt-4o-mini" />
        </div>
        <div>
          <label class="text-xs text-slate-400">API Base URL</label>
          <UiInput v-model="form.apiBaseUrl" placeholder="https://api.xxx.com/v1" />
        </div>
        <div>
          <label class="text-xs text-slate-400">API Key</label>
          <UiInput v-model="form.apiKey" placeholder="sk-xxx" />
        </div>
      </div>
    </div>
  </AppShell>
</template>
