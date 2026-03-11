<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import AppShell from '../components/layout/AppShell.vue'
import UiButton from '../components/ui/UiButton.vue'
import { api } from '../lib/api'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const loading = ref(false)
const saving = ref(false)
const error = ref<string | null>(null)
const success = ref<string | null>(null)

const form = reactive({
  groupChatPrefix: '',
  groupNameWhiteList: '',
  imageRecognition: false,
  imageCreatePrefix: '',
})

function listToText(list?: string[]) {
  if (!Array.isArray(list)) return ''
  return list.join('\n')
}

function textToList(text: string) {
  return text
    .split('\n')
    .map((item) => item.trim())
    .filter(Boolean)
}

async function fetchConfig() {
  loading.value = true
  error.value = null
  try {
    const res = await api.getBotConfig()
    if (!res.success || !res.config) {
      error.value = '获取配置失败'
      return
    }
    const cfg = res.config as any
    form.groupChatPrefix = listToText(cfg.groupChatPrefix)
    form.groupNameWhiteList = listToText(cfg.groupNameWhiteList)
    form.imageRecognition = !!cfg.imageRecognition
    form.imageCreatePrefix = listToText(cfg.imageCreatePrefix)
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
      groupChatPrefix: textToList(form.groupChatPrefix),
      groupNameWhiteList: textToList(form.groupNameWhiteList),
      imageRecognition: form.imageRecognition,
      imageCreatePrefix: textToList(form.imageCreatePrefix),
    }
    const res = await api.updateBotConfig(payload)
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
          <div class="text-xl font-semibold">机器人配置</div>
          <div class="mt-1 text-sm text-slate-400">前缀与图片相关功能</div>
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
          <label class="text-xs text-slate-400">群聊前缀（每行一个）</label>
          <textarea
            v-model="form.groupChatPrefix"
            rows="4"
            class="mt-1 w-full rounded-xl bg-slate-950/40 ring-1 ring-white/10 px-3 py-2 text-sm outline-none focus:ring-sky-400/40 placeholder:text-slate-600"
          />
        </div>
        <div>
          <label class="text-xs text-slate-400">群聊白名单（每行一个）</label>
          <textarea
            v-model="form.groupNameWhiteList"
            rows="4"
            class="mt-1 w-full rounded-xl bg-slate-950/40 ring-1 ring-white/10 px-3 py-2 text-sm outline-none focus:ring-sky-400/40 placeholder:text-slate-600"
          />
        </div>
        <div class="flex items-center gap-3 text-sm text-slate-300">
          <input v-model="form.imageRecognition" type="checkbox" class="h-4 w-4 rounded border-slate-600 bg-slate-900" />
          <span>图片识别</span>
        </div>
        <div>
          <label class="text-xs text-slate-400">图片生成前缀（每行一个）</label>
          <textarea
            v-model="form.imageCreatePrefix"
            rows="4"
            class="mt-1 w-full rounded-xl bg-slate-950/40 ring-1 ring-white/10 px-3 py-2 text-sm outline-none focus:ring-sky-400/40 placeholder:text-slate-600"
          />
        </div>
      </div>
    </div>
  </AppShell>
</template>
