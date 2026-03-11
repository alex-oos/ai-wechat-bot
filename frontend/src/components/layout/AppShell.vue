<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import AstrIcon from '../icons/AstrIcon.vue'
import UiButton from '../ui/UiButton.vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const nav = [
  { section: '连接', items: [{ label: '扫码登录', to: '/wechat-login' }] },
  {
    section: '配置',
    items: [
      { label: '微信接入渠道', to: '/config/channel' },
      { label: '机器人配置', to: '/config/bot' },
      { label: 'AI 配置', to: '/config/ai' },
    ],
  },
]

const activeTo = computed(() => route.path)

function logout() {
  auth.clear()
  router.push('/login')
}
</script>

<template>
  <div class="h-full w-full flex bg-slate-950">
    <aside class="w-[284px] shrink-0 border-r border-white/10 bg-slate-950/60 backdrop-blur">
      <div class="px-4 py-4 flex items-center gap-3 border-b border-white/10">
        <AstrIcon />
        <div class="min-w-0">
          <div class="font-semibold leading-5">ai-wechat-bot</div>
          <div class="text-xs text-slate-400">Admin Console</div>
        </div>
      </div>

      <nav class="p-3 space-y-4">
        <section v-for="group in nav" :key="group.section" class="space-y-1">
          <div class="px-2 text-[11px] font-medium tracking-wide text-slate-500 uppercase">
            {{ group.section }}
          </div>
          <RouterLink
            v-for="item in group.items"
            :key="item.to"
            :to="item.to"
            class="flex items-center gap-3 rounded-xl px-3 py-2 text-sm ring-1 ring-transparent hover:bg-white/5 hover:ring-white/10"
            :class="activeTo === item.to ? 'bg-white/5 ring-white/10 text-slate-100' : 'text-slate-300'"
          >
            <span class="h-2 w-2 rounded-full" :class="activeTo === item.to ? 'bg-sky-400' : 'bg-slate-600'" />
            <span class="truncate">{{ item.label }}</span>
          </RouterLink>
        </section>
      </nav>

      <div class="absolute bottom-0 left-0 right-0 p-3 border-t border-white/10">
        <UiButton block variant="ghost" @click="logout">退出登录</UiButton>
      </div>
    </aside>

    <div class="flex-1 min-w-0">
      <header class="h-14 border-b border-white/10 bg-slate-950/40 backdrop-blur flex items-center justify-between px-6">
        <div class="min-w-0">
          <div class="text-sm font-medium text-slate-100 truncate">{{ route.meta.title ?? route.path }}</div>
          <div class="text-xs text-slate-500 truncate">Control Panel</div>
        </div>
        <div class="flex items-center gap-2 text-xs text-slate-500">
          <span class="inline-flex items-center gap-2 rounded-full bg-white/5 ring-1 ring-white/10 px-3 py-1">
            <span class="h-2 w-2 rounded-full bg-emerald-400" />
            Service Online
          </span>
        </div>
      </header>

      <main class="p-6">
        <slot />
      </main>
    </div>
  </div>
</template>
